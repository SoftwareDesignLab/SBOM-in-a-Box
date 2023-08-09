package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.diff.ComparisonFile;

import java.util.List;

/**
 * File: ComparisonFileRepository.java
 * Repository for storing Comparisons used for Diff Report Files
 * CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 *
 * @author Derek Garcia
 **/
public interface ComparisonFileRepository extends CrudRepository<ComparisonFile, Long> {

    /**
     * Select Conflict files for a target and other sbom
     *
     * SQL: SELECT * FROM comparison WHERE target = targetSBOM AND other = otherSBOM;
     *
     * @return ComparisonFile for the target and other sbom
     */
    ComparisonFile findByTargetSBOMAndOtherSBOM(SBOM targetSBOM, SBOM otherSBOM);
}