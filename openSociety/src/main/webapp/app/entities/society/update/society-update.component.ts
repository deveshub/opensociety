import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISociety, Society } from '../society.model';
import { SocietyService } from '../service/society.service';

@Component({
  selector: 'bits-society-update',
  templateUrl: './society-update.component.html',
})
export class SocietyUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [],
  });

  constructor(protected societyService: SocietyService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ society }) => {
      this.updateForm(society);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const society = this.createFromForm();
    if (society.id !== undefined) {
      this.subscribeToSaveResponse(this.societyService.update(society));
    } else {
      this.subscribeToSaveResponse(this.societyService.create(society));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISociety>>): void {
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

  protected updateForm(society: ISociety): void {
    this.editForm.patchValue({
      id: society.id,
      name: society.name,
      description: society.description,
    });
  }

  protected createFromForm(): ISociety {
    return {
      ...new Society(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
