package org.svip.api.repository;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

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
        Random rand = new Random();
        long id = rand.nextLong();
        id += (rand.nextLong()) % ((id < 0) ? id : Long.MAX_VALUE);
        return Math.abs(id);
    }
}
