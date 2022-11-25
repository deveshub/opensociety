import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFlat } from '../flat.model';

@Component({
  selector: 'bits-flat-detail',
  templateUrl: './flat-detail.component.html',
})
export class FlatDetailComponent implements OnInit {
  flat: IFlat | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ flat }) => {
      this.flat = flat;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
