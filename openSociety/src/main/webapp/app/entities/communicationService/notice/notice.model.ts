import dayjs from 'dayjs/esm';
import { NoticeType } from 'app/entities/enumerations/notice-type.model';

export interface INotice {
  id?: number;
  title?: string;
  body?: string;
  publishDate?: dayjs.Dayjs;
  noticeType?: NoticeType | null;
  userId?: number;
}

export class Notice implements INotice {
  constructor(
    public id?: number,
    public title?: string,
    public body?: string,
    public publishDate?: dayjs.Dayjs,
    public noticeType?: NoticeType | null,
    public userId?: number
  ) {}
}

export function getNoticeIdentifier(notice: INotice): number | undefined {
  return notice.id;
}
