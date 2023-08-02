package org.svip.api.repository;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.Random;

/**
 * ID generator class implementing JPA's IdentifierGenerator
 *
 * @author Juan Francisco Patino
 */
public class SBOMFileIdentifierGenerator implements IdentifierGenerator {
    @Override
    public Long generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        // assign new id and name
        Random rand = new Random();
        long id = rand.nextLong();
        id += (Math.abs(rand.nextLong())) % ((id < 0) ? id : Long.MAX_VALUE);
        return id;
    }
}
