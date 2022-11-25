import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMember, Member } from '../member.model';
import { MemberService } from '../service/member.service';

@Injectable({ providedIn: 'root' })
export class MemberRoutingResolveService implements Resolve<IMember> {
  constructor(protected service: MemberService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMember> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((member: HttpResponse<Member>) => {
          if (member.body) {
            return of(member.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Member());
  }
}
