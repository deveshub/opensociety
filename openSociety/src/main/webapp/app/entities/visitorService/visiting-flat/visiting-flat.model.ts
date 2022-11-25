import { IVisitor } from 'app/entities/visitorService/visitor/visitor.model';

export interface IVisitingFlat {
  id?: number;
  flatNo?: string;
  visitors?: IVisitor[] | null;
}

export class VisitingFlat implements IVisitingFlat {
  constructor(public id?: number, public flatNo?: string, public visitors?: IVisitor[] | null) {}
}

export function getVisitingFlatIdentifier(visitingFlat: IVisitingFlat): number | undefined {
  return visitingFlat.id;
}
