import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INotice } from '../notice.model';
import { NoticeService } from '../service/notice.service';
import { NoticeDeleteDialogComponent } from '../delete/notice-delete-dialog.component';

@Component({
  selector: 'bits-notice',
  templateUrl: './notice.component.html',
})
export class NoticeComponent implements OnInit {
  notices?: INotice[];
  isLoading = false;

  constructor(protected noticeService: NoticeService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.noticeService.query().subscribe({
      next: (res: HttpResponse<INotice[]>) => {
        this.isLoading = false;
        this.notices = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: INotice): number {
    return item.id!;
  }

  delete(notice: INotice): void {
    const modalRef = this.modalService.open(NoticeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.notice = notice;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
