import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './stock.reducer';

export const StockDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const stockEntity = useAppSelector(state => state.stockanalysisapplication.stock.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="stockDetailsHeading">
          <Translate contentKey="stockAnalysisApplicationApp.stock.detail.title">Stock</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{stockEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="stockAnalysisApplicationApp.stock.name">Name</Translate>
            </span>
          </dt>
          <dd>{stockEntity.name}</dd>
          <dt>
            <span id="sector">
              <Translate contentKey="stockAnalysisApplicationApp.stock.sector">Sector</Translate>
            </span>
          </dt>
          <dd>{stockEntity.sector}</dd>
          <dt>
            <span id="fundation">
              <Translate contentKey="stockAnalysisApplicationApp.stock.fundation">Fundation</Translate>
            </span>
          </dt>
          <dd>{stockEntity.fundation ? <TextFormat value={stockEntity.fundation} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="stockAnalysisApplicationApp.stock.description">Description</Translate>
            </span>
          </dt>
          <dd>{stockEntity.description}</dd>
          <dt>
            <span id="icnome">
              <Translate contentKey="stockAnalysisApplicationApp.stock.icnome">Icnome</Translate>
            </span>
          </dt>
          <dd>{stockEntity.icnome}</dd>
          <dt>
            <span id="expenses">
              <Translate contentKey="stockAnalysisApplicationApp.stock.expenses">Expenses</Translate>
            </span>
          </dt>
          <dd>{stockEntity.expenses}</dd>
          <dt>
            <span id="capitalization">
              <Translate contentKey="stockAnalysisApplicationApp.stock.capitalization">Capitalization</Translate>
            </span>
          </dt>
          <dd>{stockEntity.capitalization}</dd>
          <dt>
            <span id="employees">
              <Translate contentKey="stockAnalysisApplicationApp.stock.employees">Employees</Translate>
            </span>
          </dt>
          <dd>{stockEntity.employees}</dd>
        </dl>
        <Button tag={Link} to="/stock" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stock/${stockEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StockDetail;
