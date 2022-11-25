import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IVisitor, Visitor } from '../visitor.model';
import { VisitorService } from '../service/visitor.service';
import { IVisitingFlat } from 'app/entities/visitorService/visiting-flat/visiting-flat.model';
import { VisitingFlatService } from 'app/entities/visitorService/visiting-flat/service/visiting-flat.service';
import { VisitorType } from 'app/entities/enumerations/visitor-type.model';

@Component({
  selector: 'bits-visitor-update',
  templateUrl: './visitor-update.component.html',
})
export class VisitorUpdateComponent implements OnInit {
  isSaving = false;
  visitorTypeValues = Object.keys(VisitorType);

  visitingFlatsSharedCollection: IVisitingFlat[] = [];

  editForm = this.fb.group({
    id: [],
    visitorType: [null, [Validators.required]],
    mobile: [],
    vehicleNumber: [],
    address: [],
    inTime: [],
    outTime: [],
    visitingFlats: [],
  });

  constructor(
    protected visitorService: VisitorService,
    protected visitingFlatService: VisitingFlatService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visitor }) => {
      if (visitor.id === undefined) {
        const today = dayjs().startOf('day');
        visitor.inTime = today;
        visitor.outTime = today;
      }

      this.updateForm(visitor);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const visitor = this.createFromForm();
    if (visitor.id !== undefined) {
      this.subscribeToSaveResponse(this.visitorService.update(visitor));
    } else {
      this.subscribeToSaveResponse(this.visitorService.create(visitor));
    }
  }

  trackVisitingFlatById(_index: number, item: IVisitingFlat): number {
    return item.id!;
  }

  getSelectedVisitingFlat(option: IVisitingFlat, selectedVals?: IVisitingFlat[]): IVisitingFlat {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVisitor>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(visitor: IVisitor): void {
    this.editForm.patchValue({
      id: visitor.id,
      visitorType: visitor.visitorType,
      mobile: visitor.mobile,
      vehicleNumber: visitor.vehicleNumber,
      address: visitor.address,
      inTime: visitor.inTime ? visitor.inTime.format(DATE_TIME_FORMAT) : null,
      outTime: visitor.outTime ? visitor.outTime.format(DATE_TIME_FORMAT) : null,
      visitingFlats: visitor.visitingFlats,
    });

    this.visitingFlatsSharedCollection = this.visitingFlatService.addVisitingFlatToCollectionIfMissing(
      this.visitingFlatsSharedCollection,
      ...(visitor.visitingFlats ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.visitingFlatService
      .query()
      .pipe(map((res: HttpResponse<IVisitingFlat[]>) => res.body ?? []))
      .pipe(
        map((visitingFlats: IVisitingFlat[]) =>
          this.visitingFlatService.addVisitingFlatToCollectionIfMissing(visitingFlats, ...(this.editForm.get('visitingFlats')!.value ?? []))
        )
      )
      .subscribe((visitingFlats: IVisitingFlat[]) => (this.visitingFlatsSharedCollection = visitingFlats));
  }

  protected createFromForm(): IVisitor {
    return {
      ...new Visitor(),
      id: this.editForm.get(['id'])!.value,
      visitorType: this.editForm.get(['visitorType'])!.value,
      mobile: this.editForm.get(['mobile'])!.value,
      vehicleNumber: this.editForm.get(['vehicleNumber'])!.value,
      address: this.editForm.get(['address'])!.value,
      inTime: this.editForm.get(['inTime'])!.value ? dayjs(this.editForm.get(['inTime'])!.value, DATE_TIME_FORMAT) : undefined,
      outTime: this.editForm.get(['outTime'])!.value ? dayjs(this.editForm.get(['outTime'])!.value, DATE_TIME_FORMAT) : undefined,
      visitingFlats: this.editForm.get(['visitingFlats'])!.value,
    };
  }
}
