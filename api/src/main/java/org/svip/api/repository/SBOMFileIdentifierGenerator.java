package org.svip.api.repository;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.svip.api.services.SBOMFileService;
import org.svip.api.utils.Utils;

import java.io.Serializable;


/**
 * ID generator class implementing JPA's IdentifierGenerator
 *
 * @author Juan Francisco Patino
 */
public class SBOMFileIdentifierGenerator implements IdentifierGenerator {

    public final static String generatorName = "SBOMIdGenerator";

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        // assign new id and name
        return SBOMFileService.generateSBOMFileId();
    }

}
