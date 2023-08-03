package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.entities.SBOM;

/**
 * Repository for storing SBOM Files
 * CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 * todo rename SBOMFileRepository
 * @author Derek Garcia
 **/
public interface SBOMRepository extends CrudRepository<SBOM, Long> {
}
