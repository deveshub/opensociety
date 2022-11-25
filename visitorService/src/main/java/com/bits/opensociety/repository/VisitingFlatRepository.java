package com.bits.opensociety.repository;

import com.bits.opensociety.domain.VisitingFlat;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the VisitingFlat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VisitingFlatRepository extends JpaRepository<VisitingFlat, Long> {}
