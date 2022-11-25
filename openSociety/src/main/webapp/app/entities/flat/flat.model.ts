import { IMember } from 'app/entities/member/member.model';
import { ISociety } from 'app/entities/society/society.model';
import { ResidentialStatus } from 'app/entities/enumerations/residential-status.model';

export interface IFlat {
  id?: number;
  flatNo?: string;
  residentialStatus?: ResidentialStatus | null;
  flatArea?: number | null;
  flats?: IMember[] | null;
  flat?: ISociety | null;
}

export class Flat implements IFlat {
  constructor(
    public id?: number,
    public flatNo?: string,
    public residentialStatus?: ResidentialStatus | null,
    public flatArea?: number | null,
    public flats?: IMember[] | null,
    public flat?: ISociety | null
  ) {}
}

export function getFlatIdentifier(flat: IFlat): number | undefined {
  return flat.id;
}
