import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SocietyService } from '../service/society.service';
import { ISociety, Society } from '../society.model';

import { SocietyUpdateComponent } from './society-update.component';

describe('Society Management Update Component', () => {
  let comp: SocietyUpdateComponent;
  let fixture: ComponentFixture<SocietyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let societyService: SocietyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SocietyUpdateComponent],
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
      .overrideTemplate(SocietyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SocietyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    societyService = TestBed.inject(SocietyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const society: ISociety = { id: 456 };

      activatedRoute.data = of({ society });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(society));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Society>>();
      const society = { id: 123 };
      jest.spyOn(societyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ society });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: society }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(societyService.update).toHaveBeenCalledWith(society);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Society>>();
      const society = new Society();
      jest.spyOn(societyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ society });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: society }));
      saveSubject.complete();

      // THEN
      expect(societyService.create).toHaveBeenCalledWith(society);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Society>>();
      const society = { id: 123 };
      jest.spyOn(societyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ society });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(societyService.update).toHaveBeenCalledWith(society);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
