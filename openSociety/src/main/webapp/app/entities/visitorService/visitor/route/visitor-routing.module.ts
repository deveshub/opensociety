import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { VisitorComponent } from '../list/visitor.component';
import { VisitorDetailComponent } from '../detail/visitor-detail.component';
import { VisitorUpdateComponent } from '../update/visitor-update.component';
import { VisitorRoutingResolveService } from './visitor-routing-resolve.service';

const visitorRoute: Routes = [
  {
    path: '',
    component: VisitorComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VisitorDetailComponent,
    resolve: {
      visitor: VisitorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VisitorUpdateComponent,
    resolve: {
      visitor: VisitorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VisitorUpdateComponent,
    resolve: {
      visitor: VisitorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(visitorRoute)],
  exports: [RouterModule],
})
export class VisitorRoutingModule {}
