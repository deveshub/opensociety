import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { VisitingFlatDetailComponent } from './visiting-flat-detail.component';

describe('VisitingFlat Management Detail Component', () => {
  let comp: VisitingFlatDetailComponent;
  let fixture: ComponentFixture<VisitingFlatDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VisitingFlatDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ visitingFlat: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(VisitingFlatDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(VisitingFlatDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load visitingFlat on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.visitingFlat).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
