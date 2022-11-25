import dayjs from 'dayjs/esm';
import { IVisitingFlat } from 'app/entities/visitorService/visiting-flat/visiting-flat.model';
import { VisitorType } from 'app/entities/enumerations/visitor-type.model';

export interface IVisitor {
  id?: number;
  visitorType?: VisitorType;
  mobile?: string | null;
  vehicleNumber?: string | null;
  address?: string | null;
  inTime?: dayjs.Dayjs | null;
  outTime?: dayjs.Dayjs | null;
  visitingFlats?: IVisitingFlat[] | null;
}

export class Visitor implements IVisitor {
  constructor(
    public id?: number,
    public visitorType?: VisitorType,
    public mobile?: string | null,
    public vehicleNumber?: string | null,
    public address?: string | null,
    public inTime?: dayjs.Dayjs | null,
    public outTime?: dayjs.Dayjs | null,
    public visitingFlats?: IVisitingFlat[] | null
  ) {}
}

export function getVisitorIdentifier(visitor: IVisitor): number | undefined {
  return visitor.id;
}
