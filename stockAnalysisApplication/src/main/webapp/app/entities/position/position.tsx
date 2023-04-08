import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPosition } from 'app/shared/model/position.model';
import { getEntities } from './position.reducer';

export const Position = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const positionList = useAppSelector(state => state.stockanalysisapplication.position.entities);
  const loading = useAppSelector(state => state.stockanalysisapplication.position.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="position-heading" data-cy="PositionHeading">
        <Translate contentKey="stockAnalysisApplicationApp.position.home.title">Positions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="stockAnalysisApplicationApp.position.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/position/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="stockAnalysisApplicationApp.position.home.createLabel">Create new Position</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {positionList && positionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.position.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.position.amount">Amount</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.position.price">Price</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.position.portfolio">Portfolio</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {positionList.map((position, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/position/${position.id}`} color="link" size="sm">
                      {position.id}
                    </Button>
                  </td>
                  <td>{position.amount}</td>
                  <td>{position.price}</td>
                  <td>{position.portfolio ? <Link to={`/portfolio/${position.portfolio.id}`}>{position.portfolio.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/position/${position.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/position/${position.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/position/${position.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="stockAnalysisApplicationApp.position.home.notFound">No Positions found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Position;
