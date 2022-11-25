import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVisitingFlat } from '../visiting-flat.model';
import { VisitingFlatService } from '../service/visiting-flat.service';

@Component({
  templateUrl: './visiting-flat-delete-dialog.component.html',
})
export class VisitingFlatDeleteDialogComponent {
  visitingFlat?: IVisitingFlat;

  constructor(protected visitingFlatService: VisitingFlatService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.visitingFlatService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
