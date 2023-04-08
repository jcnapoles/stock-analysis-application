import dayjs from 'dayjs';
import { IIndicator } from 'app/shared/model/indicator.model';
import { IStock } from 'app/shared/model/stock.model';

export interface IAnalysis {
  id?: number;
  date?: string;
  description?: string | null;
  indicators?: IIndicator[] | null;
  stock?: IStock | null;
}

export const defaultValue: Readonly<IAnalysis> = {};
