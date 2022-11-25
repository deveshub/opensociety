import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { VisitingFlatService } from '../service/visiting-flat.service';

import { VisitingFlatComponent } from './visiting-flat.component';

describe('VisitingFlat Management Component', () => {
  let comp: VisitingFlatComponent;
  let fixture: ComponentFixture<VisitingFlatComponent>;
  let service: VisitingFlatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [VisitingFlatComponent],
    })
      .overrideTemplate(VisitingFlatComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VisitingFlatComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(VisitingFlatService);

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
    expect(comp.visitingFlats?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
