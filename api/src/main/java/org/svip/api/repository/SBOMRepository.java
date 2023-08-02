package org.svip.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.svip.sbom.model.interfaces.generics.SBOM;

/**
 * @author Derek Garcia
 **/

public interface SBOMRepository extends CrudRepository<SBOM, Long> {

}
