package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.entities.diff.ComparisonFile;

/**
 * File: DiffReportFileRepository.java
 * Repository for storing Diff Report Files
 * CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 *
 * @author Derek Garcia
 **/
public interface DiffReportFileRepository extends CrudRepository<ComparisonFile, Long> {
}
