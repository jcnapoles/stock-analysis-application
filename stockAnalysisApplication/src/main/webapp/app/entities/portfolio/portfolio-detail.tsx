import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './portfolio.reducer';

export const PortfolioDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const portfolioEntity = useAppSelector(state => state.stockanalysisapplication.portfolio.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="portfolioDetailsHeading">
          <Translate contentKey="stockAnalysisApplicationApp.portfolio.detail.title">Portfolio</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="stockAnalysisApplicationApp.portfolio.name">Name</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="stockAnalysisApplicationApp.portfolio.description">Description</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/portfolio" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/portfolio/${portfolioEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PortfolioDetail;
