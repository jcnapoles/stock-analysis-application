import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Portfolio from './portfolio';
import PortfolioDetail from './portfolio-detail';
import PortfolioUpdate from './portfolio-update';
import PortfolioDeleteDialog from './portfolio-delete-dialog';

const PortfolioRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Portfolio />} />
    <Route path="new" element={<PortfolioUpdate />} />
    <Route path=":id">
      <Route index element={<PortfolioDetail />} />
      <Route path="edit" element={<PortfolioUpdate />} />
      <Route path="delete" element={<PortfolioDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PortfolioRoutes;
