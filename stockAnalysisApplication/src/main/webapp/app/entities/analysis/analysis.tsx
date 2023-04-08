import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IAnalysis } from 'app/shared/model/analysis.model';
import { getEntities } from './analysis.reducer';

export const Analysis = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const analysisList = useAppSelector(state => state.stockanalysisapplication.analysis.entities);
  const loading = useAppSelector(state => state.stockanalysisapplication.analysis.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="analysis-heading" data-cy="AnalysisHeading">
        <Translate contentKey="stockAnalysisApplicationApp.analysis.home.title">Analyses</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="stockAnalysisApplicationApp.analysis.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/analysis/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="stockAnalysisApplicationApp.analysis.home.createLabel">Create new Analysis</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {analysisList && analysisList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.analysis.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.analysis.date">Date</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.analysis.description">Description</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.analysis.stock">Stock</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {analysisList.map((analysis, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/analysis/${analysis.id}`} color="link" size="sm">
                      {analysis.id}
                    </Button>
                  </td>
                  <td>{analysis.date ? <TextFormat type="date" value={analysis.date} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{analysis.description}</td>
                  <td>{analysis.stock ? <Link to={`/stock/${analysis.stock.id}`}>{analysis.stock.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/analysis/${analysis.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/analysis/${analysis.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/analysis/${analysis.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="stockAnalysisApplicationApp.analysis.home.notFound">No Analyses found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Analysis;
