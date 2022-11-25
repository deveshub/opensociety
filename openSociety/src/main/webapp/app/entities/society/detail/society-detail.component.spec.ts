import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SocietyDetailComponent } from './society-detail.component';

describe('Society Management Detail Component', () => {
  let comp: SocietyDetailComponent;
  let fixture: ComponentFixture<SocietyDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SocietyDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ society: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SocietyDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SocietyDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load society on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.society).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
