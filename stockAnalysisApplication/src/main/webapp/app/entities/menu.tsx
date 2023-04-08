import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/analysis">
        <Translate contentKey="global.menu.entities.analysis" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/indicator">
        <Translate contentKey="global.menu.entities.indicator" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/portfolio">
        <Translate contentKey="global.menu.entities.portfolio" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/position">
        <Translate contentKey="global.menu.entities.position" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/stock">
        <Translate contentKey="global.menu.entities.stock" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
