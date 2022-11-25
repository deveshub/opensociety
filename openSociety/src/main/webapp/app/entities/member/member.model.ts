import { IUser } from 'app/entities/user/user.model';
import { IFlat } from 'app/entities/flat/flat.model';
import { MemberType } from 'app/entities/enumerations/member-type.model';

export interface IMember {
  id?: number;
  name?: string;
  mobile?: string;
  email?: string;
  memberType?: MemberType;
  user?: IUser;
  flat?: IFlat;
}

export class Member implements IMember {
  constructor(
    public id?: number,
    public name?: string,
    public mobile?: string,
    public email?: string,
    public memberType?: MemberType,
    public user?: IUser,
    public flat?: IFlat
  ) {}
}

export function getMemberIdentifier(member: IMember): number | undefined {
  return member.id;
}
