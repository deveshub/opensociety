import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISociety } from '../society.model';
import { SocietyService } from '../service/society.service';

@Component({
  templateUrl: './society-delete-dialog.component.html',
})
export class SocietyDeleteDialogComponent {
  society?: ISociety;

  constructor(protected societyService: SocietyService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.societyService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
