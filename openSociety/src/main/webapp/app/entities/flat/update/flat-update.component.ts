import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IFlat, Flat } from '../flat.model';
import { FlatService } from '../service/flat.service';
import { ISociety } from 'app/entities/society/society.model';
import { SocietyService } from 'app/entities/society/service/society.service';
import { ResidentialStatus } from 'app/entities/enumerations/residential-status.model';

@Component({
  selector: 'bits-flat-update',
  templateUrl: './flat-update.component.html',
})
export class FlatUpdateComponent implements OnInit {
  isSaving = false;
  residentialStatusValues = Object.keys(ResidentialStatus);

  societiesSharedCollection: ISociety[] = [];

  editForm = this.fb.group({
    id: [],
    flatNo: [null, [Validators.required]],
    residentialStatus: [],
    flatArea: [],
    flat: [],
  });

  constructor(
    protected flatService: FlatService,
    protected societyService: SocietyService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ flat }) => {
      this.updateForm(flat);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const flat = this.createFromForm();
    if (flat.id !== undefined) {
      this.subscribeToSaveResponse(this.flatService.update(flat));
    } else {
      this.subscribeToSaveResponse(this.flatService.create(flat));
    }
  }

  trackSocietyById(_index: number, item: ISociety): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFlat>>): void {
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

  protected updateForm(flat: IFlat): void {
    this.editForm.patchValue({
      id: flat.id,
      flatNo: flat.flatNo,
      residentialStatus: flat.residentialStatus,
      flatArea: flat.flatArea,
      flat: flat.flat,
    });

    this.societiesSharedCollection = this.societyService.addSocietyToCollectionIfMissing(this.societiesSharedCollection, flat.flat);
  }

  protected loadRelationshipsOptions(): void {
    this.societyService
      .query()
      .pipe(map((res: HttpResponse<ISociety[]>) => res.body ?? []))
      .pipe(
        map((societies: ISociety[]) => this.societyService.addSocietyToCollectionIfMissing(societies, this.editForm.get('flat')!.value))
      )
      .subscribe((societies: ISociety[]) => (this.societiesSharedCollection = societies));
  }

  protected createFromForm(): IFlat {
    return {
      ...new Flat(),
      id: this.editForm.get(['id'])!.value,
      flatNo: this.editForm.get(['flatNo'])!.value,
      residentialStatus: this.editForm.get(['residentialStatus'])!.value,
      flatArea: this.editForm.get(['flatArea'])!.value,
      flat: this.editForm.get(['flat'])!.value,
    };
  }
}
