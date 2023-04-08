package com.invertimostuyyo.repository;

import com.invertimostuyyo.domain.Analysis;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Analysis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {}
