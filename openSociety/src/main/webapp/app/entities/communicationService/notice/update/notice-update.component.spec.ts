import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NoticeService } from '../service/notice.service';
import { INotice, Notice } from '../notice.model';

import { NoticeUpdateComponent } from './notice-update.component';

describe('Notice Management Update Component', () => {
  let comp: NoticeUpdateComponent;
  let fixture: ComponentFixture<NoticeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let noticeService: NoticeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NoticeUpdateComponent],
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
      .overrideTemplate(NoticeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NoticeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    noticeService = TestBed.inject(NoticeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const notice: INotice = { id: 456 };

      activatedRoute.data = of({ notice });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(notice));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notice>>();
      const notice = { id: 123 };
      jest.spyOn(noticeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notice }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(noticeService.update).toHaveBeenCalledWith(notice);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notice>>();
      const notice = new Notice();
      jest.spyOn(noticeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notice }));
      saveSubject.complete();

      // THEN
      expect(noticeService.create).toHaveBeenCalledWith(notice);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notice>>();
      const notice = { id: 123 };
      jest.spyOn(noticeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(noticeService.update).toHaveBeenCalledWith(notice);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
