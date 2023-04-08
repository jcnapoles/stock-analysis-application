import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPortfolio } from 'app/shared/model/portfolio.model';
import { getEntities } from './portfolio.reducer';

export const Portfolio = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const portfolioList = useAppSelector(state => state.stockanalysisapplication.portfolio.entities);
  const loading = useAppSelector(state => state.stockanalysisapplication.portfolio.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="portfolio-heading" data-cy="PortfolioHeading">
        <Translate contentKey="stockAnalysisApplicationApp.portfolio.home.title">Portfolios</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="stockAnalysisApplicationApp.portfolio.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/portfolio/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="stockAnalysisApplicationApp.portfolio.home.createLabel">Create new Portfolio</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {portfolioList && portfolioList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.portfolio.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.portfolio.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="stockAnalysisApplicationApp.portfolio.description">Description</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {portfolioList.map((portfolio, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/portfolio/${portfolio.id}`} color="link" size="sm">
                      {portfolio.id}
                    </Button>
                  </td>
                  <td>{portfolio.name}</td>
                  <td>{portfolio.description}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/portfolio/${portfolio.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/portfolio/${portfolio.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/portfolio/${portfolio.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="stockAnalysisApplicationApp.portfolio.home.notFound">No Portfolios found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Portfolio;
