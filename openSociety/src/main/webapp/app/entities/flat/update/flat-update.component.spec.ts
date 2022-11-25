import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FlatService } from '../service/flat.service';
import { IFlat, Flat } from '../flat.model';
import { ISociety } from 'app/entities/society/society.model';
import { SocietyService } from 'app/entities/society/service/society.service';

import { FlatUpdateComponent } from './flat-update.component';

describe('Flat Management Update Component', () => {
  let comp: FlatUpdateComponent;
  let fixture: ComponentFixture<FlatUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let flatService: FlatService;
  let societyService: SocietyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FlatUpdateComponent],
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
      .overrideTemplate(FlatUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FlatUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    flatService = TestBed.inject(FlatService);
    societyService = TestBed.inject(SocietyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Society query and add missing value', () => {
      const flat: IFlat = { id: 456 };
      const flat: ISociety = { id: 52699 };
      flat.flat = flat;

      const societyCollection: ISociety[] = [{ id: 28820 }];
      jest.spyOn(societyService, 'query').mockReturnValue(of(new HttpResponse({ body: societyCollection })));
      const additionalSocieties = [flat];
      const expectedCollection: ISociety[] = [...additionalSocieties, ...societyCollection];
      jest.spyOn(societyService, 'addSocietyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ flat });
      comp.ngOnInit();

      expect(societyService.query).toHaveBeenCalled();
      expect(societyService.addSocietyToCollectionIfMissing).toHaveBeenCalledWith(societyCollection, ...additionalSocieties);
      expect(comp.societiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const flat: IFlat = { id: 456 };
      const flat: ISociety = { id: 106 };
      flat.flat = flat;

      activatedRoute.data = of({ flat });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(flat));
      expect(comp.societiesSharedCollection).toContain(flat);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Flat>>();
      const flat = { id: 123 };
      jest.spyOn(flatService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ flat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: flat }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(flatService.update).toHaveBeenCalledWith(flat);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Flat>>();
      const flat = new Flat();
      jest.spyOn(flatService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ flat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: flat }));
      saveSubject.complete();

      // THEN
      expect(flatService.create).toHaveBeenCalledWith(flat);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Flat>>();
      const flat = { id: 123 };
      jest.spyOn(flatService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ flat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(flatService.update).toHaveBeenCalledWith(flat);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackSocietyById', () => {
      it('Should return tracked Society primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackSocietyById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
