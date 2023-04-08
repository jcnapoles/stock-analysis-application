import { IPortfolio } from 'app/shared/model/portfolio.model';

export interface IPosition {
  id?: number;
  amount?: number;
  price?: number;
  portfolio?: IPortfolio | null;
}

export const defaultValue: Readonly<IPosition> = {};
