import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMember } from '../member.model';
import { MemberService } from '../service/member.service';
import { MemberDeleteDialogComponent } from '../delete/member-delete-dialog.component';

@Component({
  selector: 'bits-member',
  templateUrl: './member.component.html',
})
export class MemberComponent implements OnInit {
  members?: IMember[];
  isLoading = false;

  constructor(protected memberService: MemberService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.memberService.query().subscribe({
      next: (res: HttpResponse<IMember[]>) => {
        this.isLoading = false;
        this.members = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IMember): number {
    return item.id!;
  }

  delete(member: IMember): void {
    const modalRef = this.modalService.open(MemberDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.member = member;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
