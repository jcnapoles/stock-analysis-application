import analysis from 'app/entities/analysis/analysis.reducer';
import indicator from 'app/entities/indicator/indicator.reducer';
import portfolio from 'app/entities/portfolio/portfolio.reducer';
import position from 'app/entities/position/position.reducer';
import stock from 'app/entities/stock/stock.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  analysis,
  indicator,
  portfolio,
  position,
  stock,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
