import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'member',
        data: { pageTitle: 'openSocietyApp.member.home.title' },
        loadChildren: () => import('./member/member.module').then(m => m.MemberModule),
      },
      {
        path: 'notice',
        data: { pageTitle: 'openSocietyApp.communicationServiceNotice.home.title' },
        loadChildren: () => import('./communicationService/notice/notice.module').then(m => m.CommunicationServiceNoticeModule),
      },
      {
        path: 'flat',
        data: { pageTitle: 'openSocietyApp.flat.home.title' },
        loadChildren: () => import('./flat/flat.module').then(m => m.FlatModule),
      },
      {
        path: 'visitor',
        data: { pageTitle: 'openSocietyApp.visitorServiceVisitor.home.title' },
        loadChildren: () => import('./visitorService/visitor/visitor.module').then(m => m.VisitorServiceVisitorModule),
      },
      {
        path: 'society',
        data: { pageTitle: 'openSocietyApp.society.home.title' },
        loadChildren: () => import('./society/society.module').then(m => m.SocietyModule),
      },
      {
        path: 'visiting-flat',
        data: { pageTitle: 'openSocietyApp.visitorServiceVisitingFlat.home.title' },
        loadChildren: () => import('./visitorService/visiting-flat/visiting-flat.module').then(m => m.VisitorServiceVisitingFlatModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
