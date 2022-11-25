export interface ISociety {
  id?: number;
  name?: string;
  description?: string | null;
}

export class Society implements ISociety {
  constructor(public id?: number, public name?: string, public description?: string | null) {}
}

export function getSocietyIdentifier(society: ISociety): number | undefined {
  return society.id;
}
