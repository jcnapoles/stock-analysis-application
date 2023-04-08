import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './position.reducer';

export const PositionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const positionEntity = useAppSelector(state => state.stockanalysisapplication.position.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="positionDetailsHeading">
          <Translate contentKey="stockAnalysisApplicationApp.position.detail.title">Position</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{positionEntity.id}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="stockAnalysisApplicationApp.position.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{positionEntity.amount}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="stockAnalysisApplicationApp.position.price">Price</Translate>
            </span>
          </dt>
          <dd>{positionEntity.price}</dd>
          <dt>
            <Translate contentKey="stockAnalysisApplicationApp.position.portfolio">Portfolio</Translate>
          </dt>
          <dd>{positionEntity.portfolio ? positionEntity.portfolio.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/position" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/position/${positionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PositionDetail;
