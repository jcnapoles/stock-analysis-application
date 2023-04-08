import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Stock from './stock';
import StockDetail from './stock-detail';
import StockUpdate from './stock-update';
import StockDeleteDialog from './stock-delete-dialog';

const StockRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Stock />} />
    <Route path="new" element={<StockUpdate />} />
    <Route path=":id">
      <Route index element={<StockDetail />} />
      <Route path="edit" element={<StockUpdate />} />
      <Route path="delete" element={<StockDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StockRoutes;
