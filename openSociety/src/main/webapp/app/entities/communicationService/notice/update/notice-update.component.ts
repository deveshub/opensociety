import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { INotice, Notice } from '../notice.model';
import { NoticeService } from '../service/notice.service';
import { NoticeType } from 'app/entities/enumerations/notice-type.model';

@Component({
  selector: 'bits-notice-update',
  templateUrl: './notice-update.component.html',
})
export class NoticeUpdateComponent implements OnInit {
  isSaving = false;
  noticeTypeValues = Object.keys(NoticeType);

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required]],
    body: [null, [Validators.required]],
    publishDate: [null, [Validators.required]],
    noticeType: [],
    userId: [null, [Validators.required]],
  });

  constructor(protected noticeService: NoticeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notice }) => {
      if (notice.id === undefined) {
        const today = dayjs().startOf('day');
        notice.publishDate = today;
      }

      this.updateForm(notice);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notice = this.createFromForm();
    if (notice.id !== undefined) {
      this.subscribeToSaveResponse(this.noticeService.update(notice));
    } else {
      this.subscribeToSaveResponse(this.noticeService.create(notice));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotice>>): void {
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

  protected updateForm(notice: INotice): void {
    this.editForm.patchValue({
      id: notice.id,
      title: notice.title,
      body: notice.body,
      publishDate: notice.publishDate ? notice.publishDate.format(DATE_TIME_FORMAT) : null,
      noticeType: notice.noticeType,
      userId: notice.userId,
    });
  }

  protected createFromForm(): INotice {
    return {
      ...new Notice(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      body: this.editForm.get(['body'])!.value,
      publishDate: this.editForm.get(['publishDate'])!.value
        ? dayjs(this.editForm.get(['publishDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      noticeType: this.editForm.get(['noticeType'])!.value,
      userId: this.editForm.get(['userId'])!.value,
    };
  }
}
