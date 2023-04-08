import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IStock } from 'app/shared/model/stock.model';
import { getEntities as getStocks } from 'app/entities/stock/stock.reducer';
import { IAnalysis } from 'app/shared/model/analysis.model';
import { getEntity, updateEntity, createEntity, reset } from './analysis.reducer';

export const AnalysisUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const stocks = useAppSelector(state => state.stockanalysisapplication.stock.entities);
  const analysisEntity = useAppSelector(state => state.stockanalysisapplication.analysis.entity);
  const loading = useAppSelector(state => state.stockanalysisapplication.analysis.loading);
  const updating = useAppSelector(state => state.stockanalysisapplication.analysis.updating);
  const updateSuccess = useAppSelector(state => state.stockanalysisapplication.analysis.updateSuccess);

  const handleClose = () => {
    navigate('/analysis');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStocks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...analysisEntity,
      ...values,
      stock: stocks.find(it => it.id.toString() === values.stock.toString()),
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
          ...analysisEntity,
          stock: analysisEntity?.stock?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="stockAnalysisApplicationApp.analysis.home.createOrEditLabel" data-cy="AnalysisCreateUpdateHeading">
            <Translate contentKey="stockAnalysisApplicationApp.analysis.home.createOrEditLabel">Create or edit a Analysis</Translate>
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
                  id="analysis-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.analysis.date')}
                id="analysis-date"
                name="date"
                data-cy="date"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.analysis.description')}
                id="analysis-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                id="analysis-stock"
                name="stock"
                data-cy="stock"
                label={translate('stockAnalysisApplicationApp.analysis.stock')}
                type="select"
              >
                <option value="" key="0" />
                {stocks
                  ? stocks.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/analysis" replace color="info">
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

export default AnalysisUpdate;
