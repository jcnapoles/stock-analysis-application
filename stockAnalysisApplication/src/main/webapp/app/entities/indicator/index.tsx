import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Indicator from './indicator';
import IndicatorDetail from './indicator-detail';
import IndicatorUpdate from './indicator-update';
import IndicatorDeleteDialog from './indicator-delete-dialog';

const IndicatorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Indicator />} />
    <Route path="new" element={<IndicatorUpdate />} />
    <Route path=":id">
      <Route index element={<IndicatorDetail />} />
      <Route path="edit" element={<IndicatorUpdate />} />
      <Route path="delete" element={<IndicatorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default IndicatorRoutes;
