import dayjs from 'dayjs';
import { IAnalysis } from 'app/shared/model/analysis.model';

export interface IStock {
  id?: number;
  name?: string;
  sector?: string;
  fundation?: string | null;
  description?: string | null;
  icnome?: number | null;
  expenses?: number | null;
  capitalization?: number | null;
  employees?: number | null;
  analyses?: IAnalysis[] | null;
}

export const defaultValue: Readonly<IStock> = {};
