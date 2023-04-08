import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Analysis from './analysis';
import AnalysisDetail from './analysis-detail';
import AnalysisUpdate from './analysis-update';
import AnalysisDeleteDialog from './analysis-delete-dialog';

const AnalysisRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Analysis />} />
    <Route path="new" element={<AnalysisUpdate />} />
    <Route path=":id">
      <Route index element={<AnalysisDetail />} />
      <Route path="edit" element={<AnalysisUpdate />} />
      <Route path="delete" element={<AnalysisDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AnalysisRoutes;
