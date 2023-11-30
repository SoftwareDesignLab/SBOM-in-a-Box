package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.entities.SBOMFile;

/**
 * File: SBOMRepository.java
 * Repository for storing SBOM Files
 * CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 * @author Derek Garcia
 **/
public interface SBOMFileRepository extends CrudRepository<SBOMFile, Long> {
}
