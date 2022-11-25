import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { VisitingFlatComponent } from './list/visiting-flat.component';
import { VisitingFlatDetailComponent } from './detail/visiting-flat-detail.component';
import { VisitingFlatUpdateComponent } from './update/visiting-flat-update.component';
import { VisitingFlatDeleteDialogComponent } from './delete/visiting-flat-delete-dialog.component';
import { VisitingFlatRoutingModule } from './route/visiting-flat-routing.module';

@NgModule({
  imports: [SharedModule, VisitingFlatRoutingModule],
  declarations: [VisitingFlatComponent, VisitingFlatDetailComponent, VisitingFlatUpdateComponent, VisitingFlatDeleteDialogComponent],
  entryComponents: [VisitingFlatDeleteDialogComponent],
})
export class VisitorServiceVisitingFlatModule {}
