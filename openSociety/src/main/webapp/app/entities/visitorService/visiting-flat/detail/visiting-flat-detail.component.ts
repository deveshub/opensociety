import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IVisitingFlat } from '../visiting-flat.model';

@Component({
  selector: 'bits-visiting-flat-detail',
  templateUrl: './visiting-flat-detail.component.html',
})
export class VisitingFlatDetailComponent implements OnInit {
  visitingFlat: IVisitingFlat | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visitingFlat }) => {
      this.visitingFlat = visitingFlat;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
