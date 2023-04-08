import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IAnalysis } from 'app/shared/model/analysis.model';
import { getEntities as getAnalyses } from 'app/entities/analysis/analysis.reducer';
import { IIndicator } from 'app/shared/model/indicator.model';
import { getEntity, updateEntity, createEntity, reset } from './indicator.reducer';

export const IndicatorUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const analyses = useAppSelector(state => state.stockanalysisapplication.analysis.entities);
  const indicatorEntity = useAppSelector(state => state.stockanalysisapplication.indicator.entity);
  const loading = useAppSelector(state => state.stockanalysisapplication.indicator.loading);
  const updating = useAppSelector(state => state.stockanalysisapplication.indicator.updating);
  const updateSuccess = useAppSelector(state => state.stockanalysisapplication.indicator.updateSuccess);

  const handleClose = () => {
    navigate('/indicator');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAnalyses({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...indicatorEntity,
      ...values,
      analysis: analyses.find(it => it.id.toString() === values.analysis.toString()),
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
          ...indicatorEntity,
          analysis: indicatorEntity?.analysis?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="stockAnalysisApplicationApp.indicator.home.createOrEditLabel" data-cy="IndicatorCreateUpdateHeading">
            <Translate contentKey="stockAnalysisApplicationApp.indicator.home.createOrEditLabel">Create or edit a Indicator</Translate>
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
                  id="indicator-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.indicator.name')}
                id="indicator-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.indicator.description')}
                id="indicator-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                id="indicator-analysis"
                name="analysis"
                data-cy="analysis"
                label={translate('stockAnalysisApplicationApp.indicator.analysis')}
                type="select"
              >
                <option value="" key="0" />
                {analyses
                  ? analyses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/indicator" replace color="info">
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

export default IndicatorUpdate;
