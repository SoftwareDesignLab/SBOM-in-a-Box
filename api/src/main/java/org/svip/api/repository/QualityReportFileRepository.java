package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.entities.QualityReportFile;

/**
 * Repository for storing Quality Report Files
 * CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 *
 * @author Derek Garcia
 **/
public interface QualityReportFileRepository extends CrudRepository<QualityReportFile, Long> {
}
