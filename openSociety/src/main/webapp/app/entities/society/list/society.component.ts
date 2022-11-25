import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISociety } from '../society.model';
import { SocietyService } from '../service/society.service';
import { SocietyDeleteDialogComponent } from '../delete/society-delete-dialog.component';

@Component({
  selector: 'bits-society',
  templateUrl: './society.component.html',
})
export class SocietyComponent implements OnInit {
  societies?: ISociety[];
  isLoading = false;

  constructor(protected societyService: SocietyService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.societyService.query().subscribe({
      next: (res: HttpResponse<ISociety[]>) => {
        this.isLoading = false;
        this.societies = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ISociety): number {
    return item.id!;
  }

  delete(society: ISociety): void {
    const modalRef = this.modalService.open(SocietyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.society = society;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
