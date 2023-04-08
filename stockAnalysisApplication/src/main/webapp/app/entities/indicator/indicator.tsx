import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IIndicator } from 'app/shared/model/indicator.model';
import { getEntities } from './indicator.reducer';

export const Indicator = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const indicatorList = useAppSelector(state => state.stockanalysisapplication.indicator.entities);
  const loading = useAppSelector(state => state.stockanalysisapplication.indicator.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="indicator-heading" data-cy="IndicatorHeading">
        <Translate contentKey="stockAnalysisApplicationApp.indicator.home.title">Indicators</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="stockAnalysisApplicationApp.indicator.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/indicator/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="stockAnalysisApplicationApp.indicator.home.createLabel">Create new Indicator</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {indicatorList && indicatorList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.indicator.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.indicator.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.indicator.description">Description</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.indicator.analysis">Analysis</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {indicatorList.map((indicator, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/indicator/${indicator.id}`} color="link" size="sm">
                      {indicator.id}
                    </Button>
                  </td>
                  <td>{indicator.name}</td>
                  <td>{indicator.description}</td>
                  <td>{indicator.analysis ? <Link to={`/analysis/${indicator.analysis.id}`}>{indicator.analysis.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/indicator/${indicator.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/indicator/${indicator.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/indicator/${indicator.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="stockAnalysisApplicationApp.indicator.home.notFound">No Indicators found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Indicator;
