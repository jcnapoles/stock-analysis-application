import { IPosition } from 'app/shared/model/position.model';

export interface IPortfolio {
  id?: number;
  name?: string;
  description?: string | null;
  positions?: IPosition[] | null;
}

export const defaultValue: Readonly<IPortfolio> = {};
