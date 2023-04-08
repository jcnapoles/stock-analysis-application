import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IStock } from 'app/shared/model/stock.model';
import { getEntity, updateEntity, createEntity, reset } from './stock.reducer';

export const StockUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const stockEntity = useAppSelector(state => state.stockanalysisapplication.stock.entity);
  const loading = useAppSelector(state => state.stockanalysisapplication.stock.loading);
  const updating = useAppSelector(state => state.stockanalysisapplication.stock.updating);
  const updateSuccess = useAppSelector(state => state.stockanalysisapplication.stock.updateSuccess);

  const handleClose = () => {
    navigate('/stock');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...stockEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...stockEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="stockAnalysisApplicationApp.stock.home.createOrEditLabel" data-cy="StockCreateUpdateHeading">
            <Translate contentKey="stockAnalysisApplicationApp.stock.home.createOrEditLabel">Create or edit a Stock</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="stock-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.stock.name')}
                id="stock-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.stock.sector')}
                id="stock-sector"
                name="sector"
                data-cy="sector"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.stock.fundation')}
                id="stock-fundation"
                name="fundation"
                data-cy="fundation"
                type="date"
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.stock.description')}
                id="stock-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.stock.icnome')}
                id="stock-icnome"
                name="icnome"
                data-cy="icnome"
                type="text"
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.stock.expenses')}
                id="stock-expenses"
                name="expenses"
                data-cy="expenses"
                type="text"
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.stock.capitalization')}
                id="stock-capitalization"
                name="capitalization"
                data-cy="capitalization"
                type="text"
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.stock.employees')}
                id="stock-employees"
                name="employees"
                data-cy="employees"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/stock" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default StockUpdate;
