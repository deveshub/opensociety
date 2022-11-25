import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMember, getMemberIdentifier } from '../member.model';

export type EntityResponseType = HttpResponse<IMember>;
export type EntityArrayResponseType = HttpResponse<IMember[]>;

@Injectable({ providedIn: 'root' })
export class MemberService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/members');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(member: IMember): Observable<EntityResponseType> {
    return this.http.post<IMember>(this.resourceUrl, member, { observe: 'response' });
  }

  update(member: IMember): Observable<EntityResponseType> {
    return this.http.put<IMember>(`${this.resourceUrl}/${getMemberIdentifier(member) as number}`, member, { observe: 'response' });
  }

  partialUpdate(member: IMember): Observable<EntityResponseType> {
    return this.http.patch<IMember>(`${this.resourceUrl}/${getMemberIdentifier(member) as number}`, member, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMember>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMember[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMemberToCollectionIfMissing(memberCollection: IMember[], ...membersToCheck: (IMember | null | undefined)[]): IMember[] {
    const members: IMember[] = membersToCheck.filter(isPresent);
    if (members.length > 0) {
      const memberCollectionIdentifiers = memberCollection.map(memberItem => getMemberIdentifier(memberItem)!);
      const membersToAdd = members.filter(memberItem => {
        const memberIdentifier = getMemberIdentifier(memberItem);
        if (memberIdentifier == null || memberCollectionIdentifiers.includes(memberIdentifier)) {
          return false;
        }
        memberCollectionIdentifiers.push(memberIdentifier);
        return true;
      });
      return [...membersToAdd, ...memberCollection];
    }
    return memberCollection;
  }
}
