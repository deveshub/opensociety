import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FlatDetailComponent } from './flat-detail.component';

describe('Flat Management Detail Component', () => {
  let comp: FlatDetailComponent;
  let fixture: ComponentFixture<FlatDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FlatDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ flat: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FlatDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FlatDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load flat on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.flat).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
