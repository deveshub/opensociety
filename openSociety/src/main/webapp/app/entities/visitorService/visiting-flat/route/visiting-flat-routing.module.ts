import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { VisitingFlatComponent } from '../list/visiting-flat.component';
import { VisitingFlatDetailComponent } from '../detail/visiting-flat-detail.component';
import { VisitingFlatUpdateComponent } from '../update/visiting-flat-update.component';
import { VisitingFlatRoutingResolveService } from './visiting-flat-routing-resolve.service';

const visitingFlatRoute: Routes = [
  {
    path: '',
    component: VisitingFlatComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VisitingFlatDetailComponent,
    resolve: {
      visitingFlat: VisitingFlatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VisitingFlatUpdateComponent,
    resolve: {
      visitingFlat: VisitingFlatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VisitingFlatUpdateComponent,
    resolve: {
      visitingFlat: VisitingFlatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(visitingFlatRoute)],
  exports: [RouterModule],
})
export class VisitingFlatRoutingModule {}
