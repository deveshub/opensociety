import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IVisitor } from '../visitor.model';

@Component({
  selector: 'bits-visitor-detail',
  templateUrl: './visitor-detail.component.html',
})
export class VisitorDetailComponent implements OnInit {
  visitor: IVisitor | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visitor }) => {
      this.visitor = visitor;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
