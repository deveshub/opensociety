import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISociety, Society } from '../society.model';
import { SocietyService } from '../service/society.service';

@Injectable({ providedIn: 'root' })
export class SocietyRoutingResolveService implements Resolve<ISociety> {
  constructor(protected service: SocietyService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISociety> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((society: HttpResponse<Society>) => {
          if (society.body) {
            return of(society.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Society());
  }
}
