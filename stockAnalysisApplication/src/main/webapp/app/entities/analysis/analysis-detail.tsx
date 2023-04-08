import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './analysis.reducer';

export const AnalysisDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const analysisEntity = useAppSelector(state => state.stockanalysisapplication.analysis.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="analysisDetailsHeading">
          <Translate contentKey="stockAnalysisApplicationApp.analysis.detail.title">Analysis</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{analysisEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="stockAnalysisApplicationApp.analysis.date">Date</Translate>
            </span>
          </dt>
          <dd>{analysisEntity.date ? <TextFormat value={analysisEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="stockAnalysisApplicationApp.analysis.description">Description</Translate>
            </span>
          </dt>
          <dd>{analysisEntity.description}</dd>
          <dt>
            <Translate contentKey="stockAnalysisApplicationApp.analysis.stock">Stock</Translate>
          </dt>
          <dd>{analysisEntity.stock ? analysisEntity.stock.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/analysis" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/analysis/${analysisEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AnalysisDetail;
