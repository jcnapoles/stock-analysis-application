package com.invertimostuyyo.repository;

import com.invertimostuyyo.domain.Indicator;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Indicator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IndicatorRepository extends JpaRepository<Indicator, Long> {}
