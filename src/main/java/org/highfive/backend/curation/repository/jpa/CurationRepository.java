package org.highfive.backend.curation.repository.jpa;

import org.highfive.backend.curation.entity.Curation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurationRepository extends JpaRepository<Curation, Long> {

}
