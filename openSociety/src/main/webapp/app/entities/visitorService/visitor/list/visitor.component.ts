import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IVisitor } from '../visitor.model';
import { VisitorService } from '../service/visitor.service';
import { VisitorDeleteDialogComponent } from '../delete/visitor-delete-dialog.component';

@Component({
  selector: 'bits-visitor',
  templateUrl: './visitor.component.html',
})
export class VisitorComponent implements OnInit {
  visitors?: IVisitor[];
  isLoading = false;

  constructor(protected visitorService: VisitorService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.visitorService.query().subscribe({
      next: (res: HttpResponse<IVisitor[]>) => {
        this.isLoading = false;
        this.visitors = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IVisitor): number {
    return item.id!;
  }

  delete(visitor: IVisitor): void {
    const modalRef = this.modalService.open(VisitorDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.visitor = visitor;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
