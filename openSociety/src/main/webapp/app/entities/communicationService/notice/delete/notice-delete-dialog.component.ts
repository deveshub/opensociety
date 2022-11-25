import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INotice } from '../notice.model';
import { NoticeService } from '../service/notice.service';

@Component({
  templateUrl: './notice-delete-dialog.component.html',
})
export class NoticeDeleteDialogComponent {
  notice?: INotice;

  constructor(protected noticeService: NoticeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.noticeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
