import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISociety, getSocietyIdentifier } from '../society.model';

export type EntityResponseType = HttpResponse<ISociety>;
export type EntityArrayResponseType = HttpResponse<ISociety[]>;

@Injectable({ providedIn: 'root' })
export class SocietyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/societies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(society: ISociety): Observable<EntityResponseType> {
    return this.http.post<ISociety>(this.resourceUrl, society, { observe: 'response' });
  }

  update(society: ISociety): Observable<EntityResponseType> {
    return this.http.put<ISociety>(`${this.resourceUrl}/${getSocietyIdentifier(society) as number}`, society, { observe: 'response' });
  }

  partialUpdate(society: ISociety): Observable<EntityResponseType> {
    return this.http.patch<ISociety>(`${this.resourceUrl}/${getSocietyIdentifier(society) as number}`, society, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISociety>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISociety[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSocietyToCollectionIfMissing(societyCollection: ISociety[], ...societiesToCheck: (ISociety | null | undefined)[]): ISociety[] {
    const societies: ISociety[] = societiesToCheck.filter(isPresent);
    if (societies.length > 0) {
      const societyCollectionIdentifiers = societyCollection.map(societyItem => getSocietyIdentifier(societyItem)!);
      const societiesToAdd = societies.filter(societyItem => {
        const societyIdentifier = getSocietyIdentifier(societyItem);
        if (societyIdentifier == null || societyCollectionIdentifiers.includes(societyIdentifier)) {
          return false;
        }
        societyCollectionIdentifiers.push(societyIdentifier);
        return true;
      });
      return [...societiesToAdd, ...societyCollection];
    }
    return societyCollection;
  }
}
