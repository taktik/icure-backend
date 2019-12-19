/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.asynclogic.impl

import org.apache.commons.beanutils.BeanComparator
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.collections4.Predicate
import org.apache.commons.collections4.comparators.ComparableComparator
import org.apache.commons.collections4.comparators.ComparatorChain
import org.apache.commons.collections4.comparators.NullComparator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.taktik.commons.collections.SortDir
import org.taktik.commons.collections.SortOrder
import org.taktik.icure.asynclogic.*
import org.taktik.icure.entities.*
import java.io.Serializable
import java.util.*

@Service
class MainLogicImpl : MainLogic {
    private val entityPersisterInfosList: MutableSet<EntityPersisterInfos<*, *>> = HashSet()
    private var replicationLogic: ReplicationLogic? = null
    private var roleLogic: RoleLogic? = null
    private var userLogic: UserLogic? = null
    private var formLogic: FormLogic? = null
    private var entityTemplateLogic: EntityTemplateLogic? = null

    //	private EntityPersister<Hotfolder, String> hotfolderLogic;
//	private EntityPersister<Form, String> formLogic;
//	private EntityPersister<String, String> templateLogic;
    private class EntityPersisterInfos<E, I>(val persister: EntityPersister<E, I>, val entityClass: Class<E>, val identifierClass: Class<I>)

    protected fun <E, I> registerEntityPersister(persister: EntityPersister<E, I>?, entityClass: Class<E>?, identifierClass: Class<I>?) { // Check parameters
        if (persister == null || entityClass == null || identifierClass == null) {
            return
        }
        entityPersisterInfosList.add(EntityPersisterInfos(persister, entityClass, identifierClass))
    }

    private fun <E, I> getEntityPersister(entityClass: Class<E>): EntityPersister<E, I>? {
        for (entityPersisterInfos in entityPersisterInfosList) {
            if (entityPersisterInfos.entityClass == entityClass) {
                return entityPersisterInfos.persister as EntityPersister<E, I>
            }
        }
        log.error("Could not find an entity persister for " + entityClass.simpleName + " !")
        return null
    }

    private fun <E> getEntityClassName(entityClass: Class<E>, plural: Boolean?): String {
        return entityClass.simpleName.substring(0, 1).toLowerCase() + entityClass.simpleName.substring(1) + if (plural == null) "(s)" else if (plural) "s" else ""
    }

    override suspend fun <E : Serializable?> get(c: Class<E>, id: String): E {
        val entityPersister: EntityPersister<E, *> = getEntityPersister(c)
        return entityPersister.getEntity(id)
    }

    override fun <E> getEntitiesCount(entityClass: Class<E>, predicate: Predicate<E>): Int {
        if (log.isTraceEnabled) {
            log.trace("Counting " + getEntityClassName(entityClass, null) + " using predicate : " + predicate)
        }
        val entityPersister: EntityPersister<E, *> = getEntityPersister(entityClass)
        val entities = entityPersister.allEntities
        CollectionUtils.filter(entities, predicate)
        val entitiesCount = entities.size
        if (log.isTraceEnabled) {
            log.trace("Counted " + entitiesCount + " " + getEntityClassName(entityClass, entitiesCount > 1))
        }
        return entitiesCount
    }

    override fun <E> getEntities(entityClass: Class<E>, predicate: Predicate<E>, offset: Int, limit: Int, sortOrders: List<SortOrder<String>>): List<E> {
        if (log.isTraceEnabled) {
            log.trace("Getting " + getEntityClassName(entityClass, null) + " (offset=" + offset + ", limit=" + limit + ", sortOrders=" + (if (sortOrders != null && !sortOrders.isEmpty()) Arrays.toString(sortOrders.toTypedArray()) else "/") + ")" + " using predicate : " + predicate)
        }
        val entityPersister: EntityPersister<E, *> = getEntityPersister(entityClass)
        var entities: MutableList<E>? = entityPersister.allEntities ?: return emptyList()
        CollectionUtils.filter(entities, predicate)
        sort(entities, sortOrders)
        if (offset != null && limit != null) {
            if (offset < entities.size) {
                entities = entities.subList(offset, Math.min(entities.size, offset + limit))
            } else {
                entities = emptyList()
            }
        }
        if (log.isTraceEnabled) {
            log.trace("Returned " + entities.size + " " + getEntityClassName(entityClass, entities.size > 1))
        }
        return entities
    }

    private fun <E> sort(entities: List<E>, sortOrders: List<SortOrder<String>>?) {
        if (sortOrders != null && sortOrders.size > 0) {
            val comparatorChain = ComparatorChain<E>()
            for (sortOrder in sortOrders) {
                comparatorChain.addComparator(BeanComparator(sortOrder.key, NullComparator<E>(ComparableComparator.comparableComparator(), true)), sortOrder.direction === SortDir.DESC)
            }
            Collections.sort(entities, comparatorChain)
        }
    }

    @Throws(Exception::class)
    override fun <E> createEntities(entityClass: Class<E>, entities: List<E>): List<E> { // Check parameters
        if (entityClass == null || entities == null) {
            return null
        }
        // Get entity persister
        val entityPersister: EntityPersister<E, *> = getEntityPersister(entityClass) ?: return null
        // Create entities
        val createdEntities: List<E> = ArrayList()
        entityPersister.createEntities(entities, createdEntities)
        if (log.isTraceEnabled) {
            log.trace("Created " + createdEntities.size + " " + getEntityClassName(entityClass, createdEntities.size > 1))
        }
        return createdEntities
    }

    @Throws(Exception::class)
    override fun <E> updateEntities(entityClass: Class<E>, entities: Set<E>) { // Check parameters
        if (entityClass == null || entities == null) {
            return
        }
        // Get entity persister
        val entityPersister: EntityPersister<E, *> = getEntityPersister(entityClass) ?: return
        // Update entities
        entityPersister.updateEntities(entities)
        if (log.isTraceEnabled) {
            log.trace("Updated " + entities.size + " " + getEntityClassName(entityClass, entities.size > 1))
        }
    }

    @Throws(Exception::class)
    override fun <E, I> deleteEntities(entityClass: Class<E>, entityIdentifierClass: Class<I>, entityIdentifiers: Set<I>) { // Check parameters
        if (entityClass == null || entityIdentifierClass == null || entityIdentifiers == null) {
            return
        }
        // Get entity persister
        val entityPersister: EntityPersister<E, I> = getEntityPersister(entityClass) ?: return
        // Delete entities
        entityPersister.deleteEntities(entityIdentifiers)
        if (log.isTraceEnabled) {
            log.trace("Deleted " + entityIdentifiers.size + " " + getEntityClassName(entityClass, entityIdentifiers.size > 1))
        }
    }

    override fun <E> getReplicatedObjects(objects: List<E>): List<E> {
        return objects
    }

    fun init() {
        registerEntityPersister(roleLogic, Role::class.java, String::class.java)
        registerEntityPersister(userLogic, User::class.java, String::class.java)
        registerEntityPersister(formLogic, Form::class.java, String::class.java)
        registerEntityPersister(replicationLogic, Replication::class.java, String::class.java)
        registerEntityPersister(entityTemplateLogic, EntityTemplate::class.java, String::class.java)
    }

    @Autowired
    fun setReplicationLogic(replicationLogic: ReplicationLogic?) {
        this.replicationLogic = replicationLogic
    }

    @Autowired
    fun setFormLogic(formLogic: FormLogic?) {
        this.formLogic = formLogic
    }

    @Autowired
    fun setEntityTemplateLogic(entityTemplateLogic: EntityTemplateLogic?) {
        this.entityTemplateLogic = entityTemplateLogic
    }

    @Autowired
    fun setRoleLogic(roleLogic: RoleLogic?) {
        this.roleLogic = roleLogic
    }

    @Autowired
    fun setUserLogic(userLogic: UserLogic?) {
        this.userLogic = userLogic
    }

    companion object {
        private val log = LoggerFactory.getLogger(MainLogicImpl::class.java)
    }
}
