package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.entities.diff.ConflictFile;

/**
 * File: ConflictFileRepository.java
 * Repository for storing Conflicts used for Comparison Files
 * CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 *
 * @author Derek Garcia
 **/
<<<<<<<< HEAD:api/src/main/java/org/svip/api/repository/ConflictFileRepository.java
public interface ConflictFileRepository extends CrudRepository<ConflictFile, Long> {
========
public interface ComparisonFileRepository extends CrudRepository<ComparisonFile, Long> {
>>>>>>>> 7cb48d129 (Change DiffReport name to Comparison):api/src/main/java/org/svip/api/repository/ComparisonFileRepository.java
}
