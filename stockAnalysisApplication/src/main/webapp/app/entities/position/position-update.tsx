import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPortfolio } from 'app/shared/model/portfolio.model';
import { getEntities as getPortfolios } from 'app/entities/portfolio/portfolio.reducer';
import { IPosition } from 'app/shared/model/position.model';
import { getEntity, updateEntity, createEntity, reset } from './position.reducer';

export const PositionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const portfolios = useAppSelector(state => state.stockanalysisapplication.portfolio.entities);
  const positionEntity = useAppSelector(state => state.stockanalysisapplication.position.entity);
  const loading = useAppSelector(state => state.stockanalysisapplication.position.loading);
  const updating = useAppSelector(state => state.stockanalysisapplication.position.updating);
  const updateSuccess = useAppSelector(state => state.stockanalysisapplication.position.updateSuccess);

  const handleClose = () => {
    navigate('/position');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPortfolios({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...positionEntity,
      ...values,
      portfolio: portfolios.find(it => it.id.toString() === values.portfolio.toString()),
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
          ...positionEntity,
          portfolio: positionEntity?.portfolio?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="stockAnalysisApplicationApp.position.home.createOrEditLabel" data-cy="PositionCreateUpdateHeading">
            <Translate contentKey="stockAnalysisApplicationApp.position.home.createOrEditLabel">Create or edit a Position</Translate>
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
                  id="position-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.position.amount')}
                id="position-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('stockAnalysisApplicationApp.position.price')}
                id="position-price"
                name="price"
                data-cy="price"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="position-portfolio"
                name="portfolio"
                data-cy="portfolio"
                label={translate('stockAnalysisApplicationApp.position.portfolio')}
                type="select"
              >
                <option value="" key="0" />
                {portfolios
                  ? portfolios.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/position" replace color="info">
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

export default PositionUpdate;
