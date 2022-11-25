import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFlat, Flat } from '../flat.model';
import { FlatService } from '../service/flat.service';

@Injectable({ providedIn: 'root' })
export class FlatRoutingResolveService implements Resolve<IFlat> {
  constructor(protected service: FlatService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFlat> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((flat: HttpResponse<Flat>) => {
          if (flat.body) {
            return of(flat.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Flat());
  }
}
