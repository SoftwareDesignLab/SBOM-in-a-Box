package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.model.SBOM;

/**
 * @author Derek Garcia
 **/

public interface SBOMRepository extends CrudRepository<SBOM, Long> {
    // CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
}
