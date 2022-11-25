import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VisitingFlatService } from '../service/visiting-flat.service';
import { IVisitingFlat, VisitingFlat } from '../visiting-flat.model';

import { VisitingFlatUpdateComponent } from './visiting-flat-update.component';

describe('VisitingFlat Management Update Component', () => {
  let comp: VisitingFlatUpdateComponent;
  let fixture: ComponentFixture<VisitingFlatUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let visitingFlatService: VisitingFlatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VisitingFlatUpdateComponent],
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
      .overrideTemplate(VisitingFlatUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VisitingFlatUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    visitingFlatService = TestBed.inject(VisitingFlatService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const visitingFlat: IVisitingFlat = { id: 456 };

      activatedRoute.data = of({ visitingFlat });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(visitingFlat));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<VisitingFlat>>();
      const visitingFlat = { id: 123 };
      jest.spyOn(visitingFlatService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitingFlat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visitingFlat }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(visitingFlatService.update).toHaveBeenCalledWith(visitingFlat);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<VisitingFlat>>();
      const visitingFlat = new VisitingFlat();
      jest.spyOn(visitingFlatService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitingFlat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visitingFlat }));
      saveSubject.complete();

      // THEN
      expect(visitingFlatService.create).toHaveBeenCalledWith(visitingFlat);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<VisitingFlat>>();
      const visitingFlat = { id: 123 };
      jest.spyOn(visitingFlatService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitingFlat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(visitingFlatService.update).toHaveBeenCalledWith(visitingFlat);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
