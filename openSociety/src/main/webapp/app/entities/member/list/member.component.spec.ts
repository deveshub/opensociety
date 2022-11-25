import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { MemberService } from '../service/member.service';

import { MemberComponent } from './member.component';

describe('Member Management Component', () => {
  let comp: MemberComponent;
  let fixture: ComponentFixture<MemberComponent>;
  let service: MemberService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [MemberComponent],
    })
      .overrideTemplate(MemberComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MemberComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MemberService);

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
    expect(comp.members?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
