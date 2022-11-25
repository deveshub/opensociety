import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVisitingFlat, getVisitingFlatIdentifier } from '../visiting-flat.model';

export type EntityResponseType = HttpResponse<IVisitingFlat>;
export type EntityArrayResponseType = HttpResponse<IVisitingFlat[]>;

@Injectable({ providedIn: 'root' })
export class VisitingFlatService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/visiting-flats', 'visitorservice');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(visitingFlat: IVisitingFlat): Observable<EntityResponseType> {
    return this.http.post<IVisitingFlat>(this.resourceUrl, visitingFlat, { observe: 'response' });
  }

  update(visitingFlat: IVisitingFlat): Observable<EntityResponseType> {
    return this.http.put<IVisitingFlat>(`${this.resourceUrl}/${getVisitingFlatIdentifier(visitingFlat) as number}`, visitingFlat, {
      observe: 'response',
    });
  }

  partialUpdate(visitingFlat: IVisitingFlat): Observable<EntityResponseType> {
    return this.http.patch<IVisitingFlat>(`${this.resourceUrl}/${getVisitingFlatIdentifier(visitingFlat) as number}`, visitingFlat, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVisitingFlat>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVisitingFlat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVisitingFlatToCollectionIfMissing(
    visitingFlatCollection: IVisitingFlat[],
    ...visitingFlatsToCheck: (IVisitingFlat | null | undefined)[]
  ): IVisitingFlat[] {
    const visitingFlats: IVisitingFlat[] = visitingFlatsToCheck.filter(isPresent);
    if (visitingFlats.length > 0) {
      const visitingFlatCollectionIdentifiers = visitingFlatCollection.map(
        visitingFlatItem => getVisitingFlatIdentifier(visitingFlatItem)!
      );
      const visitingFlatsToAdd = visitingFlats.filter(visitingFlatItem => {
        const visitingFlatIdentifier = getVisitingFlatIdentifier(visitingFlatItem);
        if (visitingFlatIdentifier == null || visitingFlatCollectionIdentifiers.includes(visitingFlatIdentifier)) {
          return false;
        }
        visitingFlatCollectionIdentifiers.push(visitingFlatIdentifier);
        return true;
      });
      return [...visitingFlatsToAdd, ...visitingFlatCollection];
    }
    return visitingFlatCollection;
  }
}
