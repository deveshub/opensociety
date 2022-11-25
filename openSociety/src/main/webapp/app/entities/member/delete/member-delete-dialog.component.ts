import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMember } from '../member.model';
import { MemberService } from '../service/member.service';

@Component({
  templateUrl: './member-delete-dialog.component.html',
})
export class MemberDeleteDialogComponent {
  member?: IMember;

  constructor(protected memberService: MemberService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.memberService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
