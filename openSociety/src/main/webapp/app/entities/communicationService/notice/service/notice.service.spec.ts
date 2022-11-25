import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { NoticeType } from 'app/entities/enumerations/notice-type.model';
import { INotice, Notice } from '../notice.model';

import { NoticeService } from './notice.service';

describe('Notice Service', () => {
  let service: NoticeService;
  let httpMock: HttpTestingController;
  let elemDefault: INotice;
  let expectedResult: INotice | INotice[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NoticeService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      title: 'AAAAAAA',
      body: 'AAAAAAA',
      publishDate: currentDate,
      noticeType: NoticeType.SOCIETY,
      userId: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          publishDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Notice', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          publishDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          publishDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Notice()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Notice', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          body: 'BBBBBB',
          publishDate: currentDate.format(DATE_TIME_FORMAT),
          noticeType: 'BBBBBB',
          userId: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          publishDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Notice', () => {
      const patchObject = Object.assign({}, new Notice());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          publishDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Notice', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          body: 'BBBBBB',
          publishDate: currentDate.format(DATE_TIME_FORMAT),
          noticeType: 'BBBBBB',
          userId: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          publishDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Notice', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNoticeToCollectionIfMissing', () => {
      it('should add a Notice to an empty array', () => {
        const notice: INotice = { id: 123 };
        expectedResult = service.addNoticeToCollectionIfMissing([], notice);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notice);
      });

      it('should not add a Notice to an array that contains it', () => {
        const notice: INotice = { id: 123 };
        const noticeCollection: INotice[] = [
          {
            ...notice,
          },
          { id: 456 },
        ];
        expectedResult = service.addNoticeToCollectionIfMissing(noticeCollection, notice);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Notice to an array that doesn't contain it", () => {
        const notice: INotice = { id: 123 };
        const noticeCollection: INotice[] = [{ id: 456 }];
        expectedResult = service.addNoticeToCollectionIfMissing(noticeCollection, notice);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notice);
      });

      it('should add only unique Notice to an array', () => {
        const noticeArray: INotice[] = [{ id: 123 }, { id: 456 }, { id: 50567 }];
        const noticeCollection: INotice[] = [{ id: 123 }];
        expectedResult = service.addNoticeToCollectionIfMissing(noticeCollection, ...noticeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const notice: INotice = { id: 123 };
        const notice2: INotice = { id: 456 };
        expectedResult = service.addNoticeToCollectionIfMissing([], notice, notice2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notice);
        expect(expectedResult).toContain(notice2);
      });

      it('should accept null and undefined values', () => {
        const notice: INotice = { id: 123 };
        expectedResult = service.addNoticeToCollectionIfMissing([], null, notice, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notice);
      });

      it('should return initial array if no Notice is added', () => {
        const noticeCollection: INotice[] = [{ id: 123 }];
        expectedResult = service.addNoticeToCollectionIfMissing(noticeCollection, undefined, null);
        expect(expectedResult).toEqual(noticeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
