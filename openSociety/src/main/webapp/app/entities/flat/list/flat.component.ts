import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFlat } from '../flat.model';
import { FlatService } from '../service/flat.service';
import { FlatDeleteDialogComponent } from '../delete/flat-delete-dialog.component';

@Component({
  selector: 'bits-flat',
  templateUrl: './flat.component.html',
})
export class FlatComponent implements OnInit {
  flats?: IFlat[];
  isLoading = false;

  constructor(protected flatService: FlatService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.flatService.query().subscribe({
      next: (res: HttpResponse<IFlat[]>) => {
        this.isLoading = false;
        this.flats = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IFlat): number {
    return item.id!;
  }

  delete(flat: IFlat): void {
    const modalRef = this.modalService.open(FlatDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.flat = flat;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
