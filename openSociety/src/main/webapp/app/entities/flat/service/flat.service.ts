import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFlat, getFlatIdentifier } from '../flat.model';

export type EntityResponseType = HttpResponse<IFlat>;
export type EntityArrayResponseType = HttpResponse<IFlat[]>;

@Injectable({ providedIn: 'root' })
export class FlatService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/flats');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(flat: IFlat): Observable<EntityResponseType> {
    return this.http.post<IFlat>(this.resourceUrl, flat, { observe: 'response' });
  }

  update(flat: IFlat): Observable<EntityResponseType> {
    return this.http.put<IFlat>(`${this.resourceUrl}/${getFlatIdentifier(flat) as number}`, flat, { observe: 'response' });
  }

  partialUpdate(flat: IFlat): Observable<EntityResponseType> {
    return this.http.patch<IFlat>(`${this.resourceUrl}/${getFlatIdentifier(flat) as number}`, flat, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFlat>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFlat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFlatToCollectionIfMissing(flatCollection: IFlat[], ...flatsToCheck: (IFlat | null | undefined)[]): IFlat[] {
    const flats: IFlat[] = flatsToCheck.filter(isPresent);
    if (flats.length > 0) {
      const flatCollectionIdentifiers = flatCollection.map(flatItem => getFlatIdentifier(flatItem)!);
      const flatsToAdd = flats.filter(flatItem => {
        const flatIdentifier = getFlatIdentifier(flatItem);
        if (flatIdentifier == null || flatCollectionIdentifiers.includes(flatIdentifier)) {
          return false;
        }
        flatCollectionIdentifiers.push(flatIdentifier);
        return true;
      });
      return [...flatsToAdd, ...flatCollection];
    }
    return flatCollection;
  }
}
