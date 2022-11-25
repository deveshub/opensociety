import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IVisitingFlat, VisitingFlat } from '../visiting-flat.model';
import { VisitingFlatService } from '../service/visiting-flat.service';

@Component({
  selector: 'bits-visiting-flat-update',
  templateUrl: './visiting-flat-update.component.html',
})
export class VisitingFlatUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    flatNo: [null, [Validators.required]],
  });

  constructor(protected visitingFlatService: VisitingFlatService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visitingFlat }) => {
      this.updateForm(visitingFlat);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const visitingFlat = this.createFromForm();
    if (visitingFlat.id !== undefined) {
      this.subscribeToSaveResponse(this.visitingFlatService.update(visitingFlat));
    } else {
      this.subscribeToSaveResponse(this.visitingFlatService.create(visitingFlat));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVisitingFlat>>): void {
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

  protected updateForm(visitingFlat: IVisitingFlat): void {
    this.editForm.patchValue({
      id: visitingFlat.id,
      flatNo: visitingFlat.flatNo,
    });
  }

  protected createFromForm(): IVisitingFlat {
    return {
      ...new VisitingFlat(),
      id: this.editForm.get(['id'])!.value,
      flatNo: this.editForm.get(['flatNo'])!.value,
    };
  }
}
