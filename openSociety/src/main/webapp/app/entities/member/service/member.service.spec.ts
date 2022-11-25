import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { MemberType } from 'app/entities/enumerations/member-type.model';
import { IMember, Member } from '../member.model';

import { MemberService } from './member.service';

describe('Member Service', () => {
  let service: MemberService;
  let httpMock: HttpTestingController;
  let elemDefault: IMember;
  let expectedResult: IMember | IMember[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MemberService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      mobile: 'AAAAAAA',
      email: 'AAAAAAA',
      memberType: MemberType.TENANT,
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

    it('should create a Member', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Member()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Member', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          mobile: 'BBBBBB',
          email: 'BBBBBB',
          memberType: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Member', () => {
      const patchObject = Object.assign(
        {
          email: 'BBBBBB',
        },
        new Member()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Member', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          mobile: 'BBBBBB',
          email: 'BBBBBB',
          memberType: 'BBBBBB',
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

    it('should delete a Member', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMemberToCollectionIfMissing', () => {
      it('should add a Member to an empty array', () => {
        const member: IMember = { id: 123 };
        expectedResult = service.addMemberToCollectionIfMissing([], member);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(member);
      });

      it('should not add a Member to an array that contains it', () => {
        const member: IMember = { id: 123 };
        const memberCollection: IMember[] = [
          {
            ...member,
          },
          { id: 456 },
        ];
        expectedResult = service.addMemberToCollectionIfMissing(memberCollection, member);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Member to an array that doesn't contain it", () => {
        const member: IMember = { id: 123 };
        const memberCollection: IMember[] = [{ id: 456 }];
        expectedResult = service.addMemberToCollectionIfMissing(memberCollection, member);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(member);
      });

      it('should add only unique Member to an array', () => {
        const memberArray: IMember[] = [{ id: 123 }, { id: 456 }, { id: 61957 }];
        const memberCollection: IMember[] = [{ id: 123 }];
        expectedResult = service.addMemberToCollectionIfMissing(memberCollection, ...memberArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const member: IMember = { id: 123 };
        const member2: IMember = { id: 456 };
        expectedResult = service.addMemberToCollectionIfMissing([], member, member2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(member);
        expect(expectedResult).toContain(member2);
      });

      it('should accept null and undefined values', () => {
        const member: IMember = { id: 123 };
        expectedResult = service.addMemberToCollectionIfMissing([], null, member, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(member);
      });

      it('should return initial array if no Member is added', () => {
        const memberCollection: IMember[] = [{ id: 123 }];
        expectedResult = service.addMemberToCollectionIfMissing(memberCollection, undefined, null);
        expect(expectedResult).toEqual(memberCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
