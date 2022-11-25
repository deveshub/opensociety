import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { VisitorDetailComponent } from './visitor-detail.component';

describe('Visitor Management Detail Component', () => {
  let comp: VisitorDetailComponent;
  let fixture: ComponentFixture<VisitorDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VisitorDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ visitor: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(VisitorDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(VisitorDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load visitor on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.visitor).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
