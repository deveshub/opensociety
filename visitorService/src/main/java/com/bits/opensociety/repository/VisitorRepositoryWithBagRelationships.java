package com.bits.opensociety.repository;

import com.bits.opensociety.domain.Visitor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface VisitorRepositoryWithBagRelationships {
    Optional<Visitor> fetchBagRelationships(Optional<Visitor> visitor);

    List<Visitor> fetchBagRelationships(List<Visitor> visitors);

    Page<Visitor> fetchBagRelationships(Page<Visitor> visitors);
}
