import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IVisitingFlat, VisitingFlat } from '../visiting-flat.model';
import { VisitingFlatService } from '../service/visiting-flat.service';

import { VisitingFlatRoutingResolveService } from './visiting-flat-routing-resolve.service';

describe('VisitingFlat routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: VisitingFlatRoutingResolveService;
  let service: VisitingFlatService;
  let resultVisitingFlat: IVisitingFlat | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(VisitingFlatRoutingResolveService);
    service = TestBed.inject(VisitingFlatService);
    resultVisitingFlat = undefined;
  });

  describe('resolve', () => {
    it('should return IVisitingFlat returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultVisitingFlat = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultVisitingFlat).toEqual({ id: 123 });
    });

    it('should return new IVisitingFlat if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultVisitingFlat = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultVisitingFlat).toEqual(new VisitingFlat());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as VisitingFlat })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultVisitingFlat = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultVisitingFlat).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
