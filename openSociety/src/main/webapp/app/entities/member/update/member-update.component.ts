import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMember, Member } from '../member.model';
import { MemberService } from '../service/member.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IFlat } from 'app/entities/flat/flat.model';
import { FlatService } from 'app/entities/flat/service/flat.service';
import { MemberType } from 'app/entities/enumerations/member-type.model';

@Component({
  selector: 'bits-member-update',
  templateUrl: './member-update.component.html',
})
export class MemberUpdateComponent implements OnInit {
  isSaving = false;
  memberTypeValues = Object.keys(MemberType);

  usersSharedCollection: IUser[] = [];
  flatsSharedCollection: IFlat[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    mobile: [null, [Validators.required]],
    email: [null, [Validators.required, Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')]],
    memberType: [null, [Validators.required]],
    user: [null, Validators.required],
    flat: [null, Validators.required],
  });

  constructor(
    protected memberService: MemberService,
    protected userService: UserService,
    protected flatService: FlatService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ member }) => {
      this.updateForm(member);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const member = this.createFromForm();
    if (member.id !== undefined) {
      this.subscribeToSaveResponse(this.memberService.update(member));
    } else {
      this.subscribeToSaveResponse(this.memberService.create(member));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  trackFlatById(_index: number, item: IFlat): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMember>>): void {
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

  protected updateForm(member: IMember): void {
    this.editForm.patchValue({
      id: member.id,
      name: member.name,
      mobile: member.mobile,
      email: member.email,
      memberType: member.memberType,
      user: member.user,
      flat: member.flat,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, member.user);
    this.flatsSharedCollection = this.flatService.addFlatToCollectionIfMissing(this.flatsSharedCollection, member.flat);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.flatService
      .query()
      .pipe(map((res: HttpResponse<IFlat[]>) => res.body ?? []))
      .pipe(map((flats: IFlat[]) => this.flatService.addFlatToCollectionIfMissing(flats, this.editForm.get('flat')!.value)))
      .subscribe((flats: IFlat[]) => (this.flatsSharedCollection = flats));
  }

  protected createFromForm(): IMember {
    return {
      ...new Member(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      mobile: this.editForm.get(['mobile'])!.value,
      email: this.editForm.get(['email'])!.value,
      memberType: this.editForm.get(['memberType'])!.value,
      user: this.editForm.get(['user'])!.value,
      flat: this.editForm.get(['flat'])!.value,
    };
  }
}
