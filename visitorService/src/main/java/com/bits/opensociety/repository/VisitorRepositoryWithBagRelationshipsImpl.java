package com.bits.opensociety.repository;

import com.bits.opensociety.domain.Visitor;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class VisitorRepositoryWithBagRelationshipsImpl implements VisitorRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Visitor> fetchBagRelationships(Optional<Visitor> visitor) {
        return visitor.map(this::fetchVisitingFlats);
    }

    @Override
    public Page<Visitor> fetchBagRelationships(Page<Visitor> visitors) {
        return new PageImpl<>(fetchBagRelationships(visitors.getContent()), visitors.getPageable(), visitors.getTotalElements());
    }

    @Override
    public List<Visitor> fetchBagRelationships(List<Visitor> visitors) {
        return Optional.of(visitors).map(this::fetchVisitingFlats).orElse(Collections.emptyList());
    }

    Visitor fetchVisitingFlats(Visitor result) {
        return entityManager
            .createQuery(
                "select visitor from Visitor visitor left join fetch visitor.visitingFlats where visitor is :visitor",
                Visitor.class
            )
            .setParameter("visitor", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Visitor> fetchVisitingFlats(List<Visitor> visitors) {
        return entityManager
            .createQuery(
                "select distinct visitor from Visitor visitor left join fetch visitor.visitingFlats where visitor in :visitors",
                Visitor.class
            )
            .setParameter("visitors", visitors)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
