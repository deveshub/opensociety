import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VisitorService } from '../service/visitor.service';
import { IVisitor, Visitor } from '../visitor.model';
import { IVisitingFlat } from 'app/entities/visitorService/visiting-flat/visiting-flat.model';
import { VisitingFlatService } from 'app/entities/visitorService/visiting-flat/service/visiting-flat.service';

import { VisitorUpdateComponent } from './visitor-update.component';

describe('Visitor Management Update Component', () => {
  let comp: VisitorUpdateComponent;
  let fixture: ComponentFixture<VisitorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let visitorService: VisitorService;
  let visitingFlatService: VisitingFlatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VisitorUpdateComponent],
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
      .overrideTemplate(VisitorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VisitorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    visitorService = TestBed.inject(VisitorService);
    visitingFlatService = TestBed.inject(VisitingFlatService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call VisitingFlat query and add missing value', () => {
      const visitor: IVisitor = { id: 456 };
      const visitingFlats: IVisitingFlat[] = [{ id: 26603 }];
      visitor.visitingFlats = visitingFlats;

      const visitingFlatCollection: IVisitingFlat[] = [{ id: 90960 }];
      jest.spyOn(visitingFlatService, 'query').mockReturnValue(of(new HttpResponse({ body: visitingFlatCollection })));
      const additionalVisitingFlats = [...visitingFlats];
      const expectedCollection: IVisitingFlat[] = [...additionalVisitingFlats, ...visitingFlatCollection];
      jest.spyOn(visitingFlatService, 'addVisitingFlatToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      expect(visitingFlatService.query).toHaveBeenCalled();
      expect(visitingFlatService.addVisitingFlatToCollectionIfMissing).toHaveBeenCalledWith(
        visitingFlatCollection,
        ...additionalVisitingFlats
      );
      expect(comp.visitingFlatsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const visitor: IVisitor = { id: 456 };
      const visitingFlats: IVisitingFlat = { id: 60165 };
      visitor.visitingFlats = [visitingFlats];

      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(visitor));
      expect(comp.visitingFlatsSharedCollection).toContain(visitingFlats);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Visitor>>();
      const visitor = { id: 123 };
      jest.spyOn(visitorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visitor }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(visitorService.update).toHaveBeenCalledWith(visitor);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Visitor>>();
      const visitor = new Visitor();
      jest.spyOn(visitorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: visitor }));
      saveSubject.complete();

      // THEN
      expect(visitorService.create).toHaveBeenCalledWith(visitor);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Visitor>>();
      const visitor = { id: 123 };
      jest.spyOn(visitorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ visitor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(visitorService.update).toHaveBeenCalledWith(visitor);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackVisitingFlatById', () => {
      it('Should return tracked VisitingFlat primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackVisitingFlatById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedVisitingFlat', () => {
      it('Should return option if no VisitingFlat is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedVisitingFlat(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected VisitingFlat for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedVisitingFlat(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this VisitingFlat is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedVisitingFlat(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
