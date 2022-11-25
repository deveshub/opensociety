import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IVisitingFlat, VisitingFlat } from '../visiting-flat.model';

import { VisitingFlatService } from './visiting-flat.service';

describe('VisitingFlat Service', () => {
  let service: VisitingFlatService;
  let httpMock: HttpTestingController;
  let elemDefault: IVisitingFlat;
  let expectedResult: IVisitingFlat | IVisitingFlat[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VisitingFlatService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      flatNo: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a VisitingFlat', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new VisitingFlat()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a VisitingFlat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          flatNo: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a VisitingFlat', () => {
      const patchObject = Object.assign(
        {
          flatNo: 'BBBBBB',
        },
        new VisitingFlat()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of VisitingFlat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          flatNo: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a VisitingFlat', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addVisitingFlatToCollectionIfMissing', () => {
      it('should add a VisitingFlat to an empty array', () => {
        const visitingFlat: IVisitingFlat = { id: 123 };
        expectedResult = service.addVisitingFlatToCollectionIfMissing([], visitingFlat);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(visitingFlat);
      });

      it('should not add a VisitingFlat to an array that contains it', () => {
        const visitingFlat: IVisitingFlat = { id: 123 };
        const visitingFlatCollection: IVisitingFlat[] = [
          {
            ...visitingFlat,
          },
          { id: 456 },
        ];
        expectedResult = service.addVisitingFlatToCollectionIfMissing(visitingFlatCollection, visitingFlat);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a VisitingFlat to an array that doesn't contain it", () => {
        const visitingFlat: IVisitingFlat = { id: 123 };
        const visitingFlatCollection: IVisitingFlat[] = [{ id: 456 }];
        expectedResult = service.addVisitingFlatToCollectionIfMissing(visitingFlatCollection, visitingFlat);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(visitingFlat);
      });

      it('should add only unique VisitingFlat to an array', () => {
        const visitingFlatArray: IVisitingFlat[] = [{ id: 123 }, { id: 456 }, { id: 73032 }];
        const visitingFlatCollection: IVisitingFlat[] = [{ id: 123 }];
        expectedResult = service.addVisitingFlatToCollectionIfMissing(visitingFlatCollection, ...visitingFlatArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const visitingFlat: IVisitingFlat = { id: 123 };
        const visitingFlat2: IVisitingFlat = { id: 456 };
        expectedResult = service.addVisitingFlatToCollectionIfMissing([], visitingFlat, visitingFlat2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(visitingFlat);
        expect(expectedResult).toContain(visitingFlat2);
      });

      it('should accept null and undefined values', () => {
        const visitingFlat: IVisitingFlat = { id: 123 };
        expectedResult = service.addVisitingFlatToCollectionIfMissing([], null, visitingFlat, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(visitingFlat);
      });

      it('should return initial array if no VisitingFlat is added', () => {
        const visitingFlatCollection: IVisitingFlat[] = [{ id: 123 }];
        expectedResult = service.addVisitingFlatToCollectionIfMissing(visitingFlatCollection, undefined, null);
        expect(expectedResult).toEqual(visitingFlatCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
