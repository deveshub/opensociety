import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SocietyComponent } from './list/society.component';
import { SocietyDetailComponent } from './detail/society-detail.component';
import { SocietyUpdateComponent } from './update/society-update.component';
import { SocietyDeleteDialogComponent } from './delete/society-delete-dialog.component';
import { SocietyRoutingModule } from './route/society-routing.module';

@NgModule({
  imports: [SharedModule, SocietyRoutingModule],
  declarations: [SocietyComponent, SocietyDetailComponent, SocietyUpdateComponent, SocietyDeleteDialogComponent],
  entryComponents: [SocietyDeleteDialogComponent],
})
export class SocietyModule {}
