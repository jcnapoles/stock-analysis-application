import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './indicator.reducer';

export const IndicatorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const indicatorEntity = useAppSelector(state => state.stockanalysisapplication.indicator.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="indicatorDetailsHeading">
          <Translate contentKey="stockAnalysisApplicationApp.indicator.detail.title">Indicator</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{indicatorEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="stockAnalysisApplicationApp.indicator.name">Name</Translate>
            </span>
          </dt>
          <dd>{indicatorEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="stockAnalysisApplicationApp.indicator.description">Description</Translate>
            </span>
          </dt>
          <dd>{indicatorEntity.description}</dd>
          <dt>
            <Translate contentKey="stockAnalysisApplicationApp.indicator.analysis">Analysis</Translate>
          </dt>
          <dd>{indicatorEntity.analysis ? indicatorEntity.analysis.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/indicator" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/indicator/${indicatorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IndicatorDetail;
