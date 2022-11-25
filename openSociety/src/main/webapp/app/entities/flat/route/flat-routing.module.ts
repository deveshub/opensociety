import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FlatComponent } from '../list/flat.component';
import { FlatDetailComponent } from '../detail/flat-detail.component';
import { FlatUpdateComponent } from '../update/flat-update.component';
import { FlatRoutingResolveService } from './flat-routing-resolve.service';

const flatRoute: Routes = [
  {
    path: '',
    component: FlatComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FlatDetailComponent,
    resolve: {
      flat: FlatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FlatUpdateComponent,
    resolve: {
      flat: FlatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FlatUpdateComponent,
    resolve: {
      flat: FlatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(flatRoute)],
  exports: [RouterModule],
})
export class FlatRoutingModule {}
