import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NoticeComponent } from './list/notice.component';
import { NoticeDetailComponent } from './detail/notice-detail.component';
import { NoticeUpdateComponent } from './update/notice-update.component';
import { NoticeDeleteDialogComponent } from './delete/notice-delete-dialog.component';
import { NoticeRoutingModule } from './route/notice-routing.module';

@NgModule({
  imports: [SharedModule, NoticeRoutingModule],
  declarations: [NoticeComponent, NoticeDetailComponent, NoticeUpdateComponent, NoticeDeleteDialogComponent],
  entryComponents: [NoticeDeleteDialogComponent],
})
export class CommunicationServiceNoticeModule {}
