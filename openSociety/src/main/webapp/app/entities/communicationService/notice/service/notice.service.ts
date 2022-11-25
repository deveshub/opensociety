import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INotice, getNoticeIdentifier } from '../notice.model';

export type EntityResponseType = HttpResponse<INotice>;
export type EntityArrayResponseType = HttpResponse<INotice[]>;

@Injectable({ providedIn: 'root' })
export class NoticeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/notices', 'communicationservice');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(notice: INotice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notice);
    return this.http
      .post<INotice>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(notice: INotice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notice);
    return this.http
      .put<INotice>(`${this.resourceUrl}/${getNoticeIdentifier(notice) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(notice: INotice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notice);
    return this.http
      .patch<INotice>(`${this.resourceUrl}/${getNoticeIdentifier(notice) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<INotice>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<INotice[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNoticeToCollectionIfMissing(noticeCollection: INotice[], ...noticesToCheck: (INotice | null | undefined)[]): INotice[] {
    const notices: INotice[] = noticesToCheck.filter(isPresent);
    if (notices.length > 0) {
      const noticeCollectionIdentifiers = noticeCollection.map(noticeItem => getNoticeIdentifier(noticeItem)!);
      const noticesToAdd = notices.filter(noticeItem => {
        const noticeIdentifier = getNoticeIdentifier(noticeItem);
        if (noticeIdentifier == null || noticeCollectionIdentifiers.includes(noticeIdentifier)) {
          return false;
        }
        noticeCollectionIdentifiers.push(noticeIdentifier);
        return true;
      });
      return [...noticesToAdd, ...noticeCollection];
    }
    return noticeCollection;
  }

  protected convertDateFromClient(notice: INotice): INotice {
    return Object.assign({}, notice, {
      publishDate: notice.publishDate?.isValid() ? notice.publishDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.publishDate = res.body.publishDate ? dayjs(res.body.publishDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((notice: INotice) => {
        notice.publishDate = notice.publishDate ? dayjs(notice.publishDate) : undefined;
      });
    }
    return res;
  }
}
