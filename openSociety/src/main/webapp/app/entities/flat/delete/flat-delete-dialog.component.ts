import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFlat } from '../flat.model';
import { FlatService } from '../service/flat.service';

@Component({
  templateUrl: './flat-delete-dialog.component.html',
})
export class FlatDeleteDialogComponent {
  flat?: IFlat;

  constructor(protected flatService: FlatService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.flatService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
