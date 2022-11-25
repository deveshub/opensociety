import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { VisitorComponent } from './list/visitor.component';
import { VisitorDetailComponent } from './detail/visitor-detail.component';
import { VisitorUpdateComponent } from './update/visitor-update.component';
import { VisitorDeleteDialogComponent } from './delete/visitor-delete-dialog.component';
import { VisitorRoutingModule } from './route/visitor-routing.module';

@NgModule({
  imports: [SharedModule, VisitorRoutingModule],
  declarations: [VisitorComponent, VisitorDetailComponent, VisitorUpdateComponent, VisitorDeleteDialogComponent],
  entryComponents: [VisitorDeleteDialogComponent],
})
export class VisitorServiceVisitorModule {}
