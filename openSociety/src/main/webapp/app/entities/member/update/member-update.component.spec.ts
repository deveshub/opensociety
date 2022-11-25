import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MemberService } from '../service/member.service';
import { IMember, Member } from '../member.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IFlat } from 'app/entities/flat/flat.model';
import { FlatService } from 'app/entities/flat/service/flat.service';

import { MemberUpdateComponent } from './member-update.component';

describe('Member Management Update Component', () => {
  let comp: MemberUpdateComponent;
  let fixture: ComponentFixture<MemberUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let memberService: MemberService;
  let userService: UserService;
  let flatService: FlatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MemberUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(MemberUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MemberUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    memberService = TestBed.inject(MemberService);
    userService = TestBed.inject(UserService);
    flatService = TestBed.inject(FlatService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const member: IMember = { id: 456 };
      const user: IUser = { id: 50363 };
      member.user = user;

      const userCollection: IUser[] = [{ id: 6822 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ member });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Flat query and add missing value', () => {
      const member: IMember = { id: 456 };
      const flat: IFlat = { id: 95892 };
      member.flat = flat;

      const flatCollection: IFlat[] = [{ id: 94296 }];
      jest.spyOn(flatService, 'query').mockReturnValue(of(new HttpResponse({ body: flatCollection })));
      const additionalFlats = [flat];
      const expectedCollection: IFlat[] = [...additionalFlats, ...flatCollection];
      jest.spyOn(flatService, 'addFlatToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ member });
      comp.ngOnInit();

      expect(flatService.query).toHaveBeenCalled();
      expect(flatService.addFlatToCollectionIfMissing).toHaveBeenCalledWith(flatCollection, ...additionalFlats);
      expect(comp.flatsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const member: IMember = { id: 456 };
      const user: IUser = { id: 61378 };
      member.user = user;
      const flat: IFlat = { id: 66314 };
      member.flat = flat;

      activatedRoute.data = of({ member });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(member));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.flatsSharedCollection).toContain(flat);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Member>>();
      const member = { id: 123 };
      jest.spyOn(memberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ member });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: member }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(memberService.update).toHaveBeenCalledWith(member);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Member>>();
      const member = new Member();
      jest.spyOn(memberService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ member });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: member }));
      saveSubject.complete();

      // THEN
      expect(memberService.create).toHaveBeenCalledWith(member);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Member>>();
      const member = { id: 123 };
      jest.spyOn(memberService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ member });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(memberService.update).toHaveBeenCalledWith(member);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackFlatById', () => {
      it('Should return tracked Flat primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackFlatById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
