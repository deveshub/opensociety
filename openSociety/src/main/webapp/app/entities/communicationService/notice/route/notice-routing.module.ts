import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NoticeComponent } from '../list/notice.component';
import { NoticeDetailComponent } from '../detail/notice-detail.component';
import { NoticeUpdateComponent } from '../update/notice-update.component';
import { NoticeRoutingResolveService } from './notice-routing-resolve.service';

const noticeRoute: Routes = [
  {
    path: '',
    component: NoticeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NoticeDetailComponent,
    resolve: {
      notice: NoticeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NoticeUpdateComponent,
    resolve: {
      notice: NoticeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NoticeUpdateComponent,
    resolve: {
      notice: NoticeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(noticeRoute)],
  exports: [RouterModule],
})
export class NoticeRoutingModule {}
