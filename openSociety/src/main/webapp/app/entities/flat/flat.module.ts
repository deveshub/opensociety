import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FlatComponent } from './list/flat.component';
import { FlatDetailComponent } from './detail/flat-detail.component';
import { FlatUpdateComponent } from './update/flat-update.component';
import { FlatDeleteDialogComponent } from './delete/flat-delete-dialog.component';
import { FlatRoutingModule } from './route/flat-routing.module';

@NgModule({
  imports: [SharedModule, FlatRoutingModule],
  declarations: [FlatComponent, FlatDetailComponent, FlatUpdateComponent, FlatDeleteDialogComponent],
  entryComponents: [FlatDeleteDialogComponent],
})
export class FlatModule {}
