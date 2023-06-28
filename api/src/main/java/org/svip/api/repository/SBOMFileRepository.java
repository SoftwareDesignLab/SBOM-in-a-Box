package org.svip.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.svip.api.model.SBOMFile;

import java.util.List;
import java.util.Optional;

/**
 * An interface extending the default JPA interface to add more specific methods in regards to data manipulation.
 */
public interface SBOMFileRepository extends JpaRepository<SBOMFile, Long> {
    Optional<SBOMFile> findById(long id);

    List<SBOMFile> findByFileName(String fileName);
}
