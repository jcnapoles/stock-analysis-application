import { IAnalysis } from 'app/shared/model/analysis.model';

export interface IIndicator {
  id?: number;
  name?: string;
  description?: string | null;
  analysis?: IAnalysis | null;
}

export const defaultValue: Readonly<IIndicator> = {};
