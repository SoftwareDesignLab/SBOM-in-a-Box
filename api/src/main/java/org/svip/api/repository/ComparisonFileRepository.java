package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.entities.diff.ComparisonFile;

import java.util.List;

/**
 * File: DiffReportFileRepository.java
 * Repository for storing Diff Report Files
 * CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 *
 * @author Derek Garcia
 **/
public interface ComparisonFileRepository extends CrudRepository<ComparisonFile, Long> {

    /**
     * Select Conflict files for a target and other sbom
     *
     * SQL: SELECT * FROM comparison WHERE target_id = targetID AND other_id = otherID;
     * @param targetID Target SBOM ID
     * @param otherID  Other SBOM ID
     *
     * @return ComparisonFile for the target and other sbom
     */
    ComparisonFile findByTargetIDAndOtherID(Long targetID, Long otherID);
}
