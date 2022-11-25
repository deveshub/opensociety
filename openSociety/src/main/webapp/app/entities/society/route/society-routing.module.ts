import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SocietyComponent } from '../list/society.component';
import { SocietyDetailComponent } from '../detail/society-detail.component';
import { SocietyUpdateComponent } from '../update/society-update.component';
import { SocietyRoutingResolveService } from './society-routing-resolve.service';

const societyRoute: Routes = [
  {
    path: '',
    component: SocietyComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SocietyDetailComponent,
    resolve: {
      society: SocietyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SocietyUpdateComponent,
    resolve: {
      society: SocietyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SocietyUpdateComponent,
    resolve: {
      society: SocietyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(societyRoute)],
  exports: [RouterModule],
})
export class SocietyRoutingModule {}
