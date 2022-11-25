import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NoticeDetailComponent } from './notice-detail.component';

describe('Notice Management Detail Component', () => {
  let comp: NoticeDetailComponent;
  let fixture: ComponentFixture<NoticeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NoticeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ notice: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NoticeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NoticeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load notice on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.notice).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
