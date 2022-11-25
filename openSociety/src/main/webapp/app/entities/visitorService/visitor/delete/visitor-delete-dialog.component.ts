import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVisitor } from '../visitor.model';
import { VisitorService } from '../service/visitor.service';

@Component({
  templateUrl: './visitor-delete-dialog.component.html',
})
export class VisitorDeleteDialogComponent {
  visitor?: IVisitor;

  constructor(protected visitorService: VisitorService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.visitorService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
