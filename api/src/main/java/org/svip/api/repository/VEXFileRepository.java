package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.api.entities.VEXFile;

/**
 * File: VEXFileRepository.java
 * Repository for storing VEX Files
 * CRUD methods: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 *
 * @author Derek Garcia
 **/
public interface VEXFileRepository extends CrudRepository<VEXFile, Long> {
}
