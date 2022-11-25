import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISociety, Society } from '../society.model';

import { SocietyService } from './society.service';

describe('Society Service', () => {
  let service: SocietyService;
  let httpMock: HttpTestingController;
  let elemDefault: ISociety;
  let expectedResult: ISociety | ISociety[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SocietyService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      description: 'AAAAAAA',
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

    it('should create a Society', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Society()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Society', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Society', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Society()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Society', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a Society', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSocietyToCollectionIfMissing', () => {
      it('should add a Society to an empty array', () => {
        const society: ISociety = { id: 123 };
        expectedResult = service.addSocietyToCollectionIfMissing([], society);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(society);
      });

      it('should not add a Society to an array that contains it', () => {
        const society: ISociety = { id: 123 };
        const societyCollection: ISociety[] = [
          {
            ...society,
          },
          { id: 456 },
        ];
        expectedResult = service.addSocietyToCollectionIfMissing(societyCollection, society);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Society to an array that doesn't contain it", () => {
        const society: ISociety = { id: 123 };
        const societyCollection: ISociety[] = [{ id: 456 }];
        expectedResult = service.addSocietyToCollectionIfMissing(societyCollection, society);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(society);
      });

      it('should add only unique Society to an array', () => {
        const societyArray: ISociety[] = [{ id: 123 }, { id: 456 }, { id: 41851 }];
        const societyCollection: ISociety[] = [{ id: 123 }];
        expectedResult = service.addSocietyToCollectionIfMissing(societyCollection, ...societyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const society: ISociety = { id: 123 };
        const society2: ISociety = { id: 456 };
        expectedResult = service.addSocietyToCollectionIfMissing([], society, society2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(society);
        expect(expectedResult).toContain(society2);
      });

      it('should accept null and undefined values', () => {
        const society: ISociety = { id: 123 };
        expectedResult = service.addSocietyToCollectionIfMissing([], null, society, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(society);
      });

      it('should return initial array if no Society is added', () => {
        const societyCollection: ISociety[] = [{ id: 123 }];
        expectedResult = service.addSocietyToCollectionIfMissing(societyCollection, undefined, null);
        expect(expectedResult).toEqual(societyCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
