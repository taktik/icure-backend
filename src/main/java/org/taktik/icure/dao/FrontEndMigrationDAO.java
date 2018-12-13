package org.taktik.icure.dao;

import org.taktik.icure.entities.FrontEndMigration;

import java.util.List;


public interface FrontEndMigrationDAO extends GenericDAO<FrontEndMigration> {
    List<FrontEndMigration> getByUserIdName(String userId, String name);
}
