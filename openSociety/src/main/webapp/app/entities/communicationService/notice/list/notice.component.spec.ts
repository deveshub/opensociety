import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { NoticeService } from '../service/notice.service';

import { NoticeComponent } from './notice.component';

describe('Notice Management Component', () => {
  let comp: NoticeComponent;
  let fixture: ComponentFixture<NoticeComponent>;
  let service: NoticeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [NoticeComponent],
    })
      .overrideTemplate(NoticeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NoticeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(NoticeService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.notices?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
