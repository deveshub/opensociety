import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ResidentialStatus } from 'app/entities/enumerations/residential-status.model';
import { IFlat, Flat } from '../flat.model';

import { FlatService } from './flat.service';

describe('Flat Service', () => {
  let service: FlatService;
  let httpMock: HttpTestingController;
  let elemDefault: IFlat;
  let expectedResult: IFlat | IFlat[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FlatService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      flatNo: 'AAAAAAA',
      residentialStatus: ResidentialStatus.OWNED,
      flatArea: 0,
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

    it('should create a Flat', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Flat()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Flat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          flatNo: 'BBBBBB',
          residentialStatus: 'BBBBBB',
          flatArea: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Flat', () => {
      const patchObject = Object.assign({}, new Flat());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Flat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          flatNo: 'BBBBBB',
          residentialStatus: 'BBBBBB',
          flatArea: 1,
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

    it('should delete a Flat', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFlatToCollectionIfMissing', () => {
      it('should add a Flat to an empty array', () => {
        const flat: IFlat = { id: 123 };
        expectedResult = service.addFlatToCollectionIfMissing([], flat);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(flat);
      });

      it('should not add a Flat to an array that contains it', () => {
        const flat: IFlat = { id: 123 };
        const flatCollection: IFlat[] = [
          {
            ...flat,
          },
          { id: 456 },
        ];
        expectedResult = service.addFlatToCollectionIfMissing(flatCollection, flat);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Flat to an array that doesn't contain it", () => {
        const flat: IFlat = { id: 123 };
        const flatCollection: IFlat[] = [{ id: 456 }];
        expectedResult = service.addFlatToCollectionIfMissing(flatCollection, flat);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(flat);
      });

      it('should add only unique Flat to an array', () => {
        const flatArray: IFlat[] = [{ id: 123 }, { id: 456 }, { id: 34984 }];
        const flatCollection: IFlat[] = [{ id: 123 }];
        expectedResult = service.addFlatToCollectionIfMissing(flatCollection, ...flatArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const flat: IFlat = { id: 123 };
        const flat2: IFlat = { id: 456 };
        expectedResult = service.addFlatToCollectionIfMissing([], flat, flat2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(flat);
        expect(expectedResult).toContain(flat2);
      });

      it('should accept null and undefined values', () => {
        const flat: IFlat = { id: 123 };
        expectedResult = service.addFlatToCollectionIfMissing([], null, flat, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(flat);
      });

      it('should return initial array if no Flat is added', () => {
        const flatCollection: IFlat[] = [{ id: 123 }];
        expectedResult = service.addFlatToCollectionIfMissing(flatCollection, undefined, null);
        expect(expectedResult).toEqual(flatCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
