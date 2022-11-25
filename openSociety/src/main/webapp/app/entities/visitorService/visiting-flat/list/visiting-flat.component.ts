import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IVisitingFlat } from '../visiting-flat.model';
import { VisitingFlatService } from '../service/visiting-flat.service';
import { VisitingFlatDeleteDialogComponent } from '../delete/visiting-flat-delete-dialog.component';

@Component({
  selector: 'bits-visiting-flat',
  templateUrl: './visiting-flat.component.html',
})
export class VisitingFlatComponent implements OnInit {
  visitingFlats?: IVisitingFlat[];
  isLoading = false;

  constructor(protected visitingFlatService: VisitingFlatService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.visitingFlatService.query().subscribe({
      next: (res: HttpResponse<IVisitingFlat[]>) => {
        this.isLoading = false;
        this.visitingFlats = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IVisitingFlat): number {
    return item.id!;
  }

  delete(visitingFlat: IVisitingFlat): void {
    const modalRef = this.modalService.open(VisitingFlatDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.visitingFlat = visitingFlat;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
