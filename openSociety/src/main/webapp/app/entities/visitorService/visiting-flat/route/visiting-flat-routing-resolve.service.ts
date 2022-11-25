import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVisitingFlat, VisitingFlat } from '../visiting-flat.model';
import { VisitingFlatService } from '../service/visiting-flat.service';

@Injectable({ providedIn: 'root' })
export class VisitingFlatRoutingResolveService implements Resolve<IVisitingFlat> {
  constructor(protected service: VisitingFlatService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVisitingFlat> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((visitingFlat: HttpResponse<VisitingFlat>) => {
          if (visitingFlat.body) {
            return of(visitingFlat.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new VisitingFlat());
  }
}
