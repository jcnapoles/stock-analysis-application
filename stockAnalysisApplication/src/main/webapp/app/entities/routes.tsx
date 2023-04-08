import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import { ReducersMapObject, combineReducers } from '@reduxjs/toolkit';

import getStore from 'app/config/store';

import entitiesReducers from './reducers';

import Analysis from './analysis';
import Indicator from './indicator';
import Portfolio from './portfolio';
import Position from './position';
import Stock from './stock';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  const store = getStore();
  store.injectReducer('stockanalysisapplication', combineReducers(entitiesReducers as ReducersMapObject));
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="analysis/*" element={<Analysis />} />
        <Route path="indicator/*" element={<Indicator />} />
        <Route path="portfolio/*" element={<Portfolio />} />
        <Route path="position/*" element={<Position />} />
        <Route path="stock/*" element={<Stock />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
