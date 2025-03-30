package org.babyfish.jimmer.sql.kt.ast.mutation

import org.babyfish.jimmer.Input
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import java.sql.Connection

interface KDeprecatedSaveOptions : KSaveOptions {

    /**
     * Insert an entity object
     * @param entity The inserted entity object.
     *
     * **Note: The jimmer entity is <b>not POJO**,
     * it can easily express data structures of arbitrary shape,
     * you can use it to save data structures of arbitrary shape.
     *
     * Unlike most JVM ORMs, Jimmer does not specified the shape
     * of the saved data structure by using configuration such as
     * `insertable`, `updatable` or `cascade`; instead,
     * it uses the dynamic nature of entity object itself to describe
     * the shape of saved data structure, **without prior design**
     *
     * Unspecified properties will be ignored,
     * only the specified properties *(whether null or not)* will be saved.
     * In addition to objects with only id property, any associated objects
     * will result in deeper recursive saves.
     *
     * The [SaveMode] of aggregate-root is [SaveMode.INSERT_ONLY]
     *
     * @param associatedMode The save mode of associated-objects.
     * This parameter is optional and its default value [AssociatedSaveMode.APPEND]
     *
     * @param con An optional JDBC connection. If it is null,
     * a connection will be brown from [org.babyfish.jimmer.sql.runtime.ConnectionManager]
     *
     * @param block An optional lambda to add additional configuration.
     * In addition to setting the [SaveMode] of aggregate root
     * and the default value of the [AssociatedSaveMode] of associated objects,
     * you can add any other configuration.
     *
     * @param [E] The type of saved entity
     *
     * @return The saved result for single object
     */
    @Deprecated("Will be removed")
    fun <E : Any> insert(
        entity: E,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.APPEND,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KSimpleSaveResult<E> =
        save(entity, con) {
            setMode(SaveMode.INSERT_ONLY)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    /**
     * Insert an input DTO
     * @param input The inserted input DTOs.
     *
     * In terms of internal mechanisms, any type of Input DTO is
     * automatically converted into an entity object of the same type
     *
     * **Note: The jimmer entity is <b>not POJO**,
     * it can easily express data structures of arbitrary shape,
     * you can use it to save data structures of arbitrary shape.
     *
     * Unlike most JVM ORMs, Jimmer does not specified the shape
     * of the saved data structure by using configuration such as
     * `insertable`, `updatable` or `cascade`; instead,
     * it uses the dynamic nature of entity object itself to describe
     * the shape of saved data structure, **without prior design**
     *
     * Unspecified properties will be ignored,
     * only the specified properties *(whether null or not)* will be saved.
     * In addition to objects with only id property, any associated objects
     * will result in deeper recursive saves.
     *
     * The [SaveMode] of aggregate-root is [SaveMode.INSERT_ONLY]
     *
     * @param associatedMode The save mode of associated-objects.
     * This parameter is optional and its default value [AssociatedSaveMode.APPEND]
     *
     * @param con An optional JDBC connection. If it is null,
     * a connection will be brown from [org.babyfish.jimmer.sql.runtime.ConnectionManager]
     *
     * @param block An optional lambda to add additional configuration.
     * In addition to setting the [SaveMode] of aggregate root
     * and the default value of the [AssociatedSaveMode] of associated objects,
     * you can add any other configuration.
     *
     * @param [E] The type of saved entity
     *
     * @return The saved result for single object
     */
    @Deprecated("Will be removed")
    fun <E : Any> insert(
        input: Input<E>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.APPEND,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KSimpleSaveResult<E> =
        save(input.toEntity(), con) {
            setMode(SaveMode.INSERT_ONLY)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    /**
     * Insert an entity object if necessary,
     * if the entity object exists in database, ignore it.
     * - If the value of id property decorated by [Id] is specified,
     *   use id value to check whether the entity object exists in database</li>
     * - otherwise, if the values of key properties decorated by [Key] is specified
     *   use key values to check whether the entity object exists in database</li>
     * - If neither value of id property nor values of key properties is specified,
     *   exception will be raised.
     *
     * @param entity The inserted entity object.
     *
     * **Note: The jimmer entity is <b>not POJO**,
     * it can easily express data structures of arbitrary shape,
     * you can use it to save data structures of arbitrary shape.
     *
     * Unlike most JVM ORMs, Jimmer does not specified the shape
     * of the saved data structure by using configuration such as
     * `insertable`, `updatable` or `cascade`; instead,
     * it uses the dynamic nature of entity object itself to describe
     * the shape of saved data structure, **without prior design**
     *
     * Unspecified properties will be ignored,
     * only the specified properties *(whether null or not)* will be saved.
     * In addition to objects with only id property, any associated objects
     * will result in deeper recursive saves.
     *
     * The [SaveMode] of aggregate-root is [SaveMode.INSERT_IF_ABSENT]
     *
     * @param associatedMode The save mode of associated-objects.
     * This parameter is optional and its default value [AssociatedSaveMode.APPEND_IF_ABSENT]
     *
     * @param con An optional JDBC connection. If it is null,
     * a connection will be brown from [org.babyfish.jimmer.sql.runtime.ConnectionManager]
     *
     * @param block An optional lambda to add additional configuration.
     * In addition to setting the [SaveMode] of aggregate root
     * and the default value of the [AssociatedSaveMode] of associated objects,
     * you can add any other configuration.
     *
     * @param [E] The type of saved entity
     *
     * @return The saved result for single object
     */
    @Deprecated("Will be removed")
    fun <E : Any> insertIfAbsent(
        entity: E,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.APPEND_IF_ABSENT,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KSimpleSaveResult<E> =
        save(entity, con) {
            setMode(SaveMode.INSERT_IF_ABSENT)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    /**
     * Insert an input DTO if necessary, that means to convert
     * the input DTO to entity object and save it.
     * If the entity object exists in database, ignore it.
     *
     * - If the value of id property decorated by [Id] is specified,
     *   use id value to check whether the entity object exists in database</li>
     * - otherwise, if the values of key properties decorated by [Key] is specified
     *   use key values to check whether the entity object exists in database</li>
     * - If neither value of id property nor values of key properties is specified,
     *   exception will be raised.
     *
     * @param input The inserted input DTOs.
     *
     * In terms of internal mechanisms, any type of Input DTO is
     * automatically converted into an entity object of the same type
     *
     * **Note: The jimmer entity is <b>not POJO**,
     * it can easily express data structures of arbitrary shape,
     * you can use it to save data structures of arbitrary shape.
     *
     * Unlike most JVM ORMs, Jimmer does not specified the shape
     * of the saved data structure by using configuration such as
     * `insertable`, `updatable` or `cascade`; instead,
     * it uses the dynamic nature of entity object itself to describe
     * the shape of saved data structure, **without prior design**
     *
     * Unspecified properties will be ignored,
     * only the specified properties *(whether null or not)* will be saved.
     * In addition to objects with only id property, any associated objects
     * will result in deeper recursive saves.
     *
     * The [SaveMode] of aggregate-root is [SaveMode.INSERT_IF_ABSENT]
     *
     * @param associatedMode The save mode of associated-objects.
     * This parameter is optional and its default value [AssociatedSaveMode.APPEND_IF_ABSENT]
     *
     * @param con An optional JDBC connection. If it is null,
     * a connection will be brown from [org.babyfish.jimmer.sql.runtime.ConnectionManager]
     *
     * @param block An optional lambda to add additional configuration.
     * In addition to setting the [SaveMode] of aggregate root
     * and the default value of the [AssociatedSaveMode] of associated objects,
     * you can add any other configuration.
     *
     * @param [E] The type of saved entity
     *
     * @return The saved result for single object
     */
    @Deprecated("Will be removed")
    fun <E : Any> insertIfAbsent(
        input: Input<E>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.APPEND_IF_ABSENT,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KSimpleSaveResult<E> =
        save(input.toEntity(), con) {
            setMode(SaveMode.INSERT_IF_ABSENT)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    /**
     * Update an entity object
     * @param entity The updated entity object.
     *
     * **Note: The jimmer entity is <b>not POJO**,
     * it can easily express data structures of arbitrary shape,
     * you can use it to save data structures of arbitrary shape.
     *
     * Unlike most JVM ORMs, Jimmer does not specified the shape
     * of the saved data structure by using configuration such as
     * `insertable`, `updatable` or `cascade`; instead,
     * it uses the dynamic nature of entity object itself to describe
     * the shape of saved data structure, **without prior design**
     *
     * Unspecified properties will be ignored,
     * only the specified properties *(whether null or not)* will be saved.
     * In addition to objects with only id property, any associated objects
     * will result in deeper recursive saves.
     *
     * The [SaveMode] of aggregate-root is [SaveMode.UPDATE_ONLY]
     *
     * @param associatedMode The save mode of associated-objects.
     * This parameter is optional and its default value [AssociatedSaveMode.UPDATE]
     *
     * @param con An optional JDBC connection. If it is null,
     * a connection will be brown from [org.babyfish.jimmer.sql.runtime.ConnectionManager]
     *
     * @param block An optional lambda to add additional configuration.
     * In addition to setting the [SaveMode] of aggregate root
     * and the default value of the [AssociatedSaveMode] of associated objects,
     * you can add any other configuration.
     *
     * @param [E] The type of saved entity
     *
     * @return The saved result for single object
     */
    @Deprecated("Will be removed")
    fun <E : Any> update(
        entity: E,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.UPDATE,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KSimpleSaveResult<E> =
        save(entity, con) {
            setMode(SaveMode.UPDATE_ONLY)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    /**
     * Update an input DTO
     * @param input The updated input DTOs.
     *
     * In terms of internal mechanisms, any type of Input DTO is
     * automatically converted into an entity object of the same type
     *
     * **Note: The jimmer entity is <b>not POJO**,
     * it can easily express data structures of arbitrary shape,
     * you can use it to save data structures of arbitrary shape.
     *
     * Unlike most JVM ORMs, Jimmer does not specified the shape
     * of the saved data structure by using configuration such as
     * `insertable`, `updatable` or `cascade`; instead,
     * it uses the dynamic nature of entity object itself to describe
     * the shape of saved data structure, **without prior design**
     *
     * Unspecified properties will be ignored,
     * only the specified properties *(whether null or not)* will be saved.
     * In addition to objects with only id property, any associated objects
     * will result in deeper recursive saves.
     *
     * The [SaveMode] of aggregate-root is [SaveMode.UPDATE_ONLY]
     *
     * @param associatedMode The save mode of associated-objects.
     * This parameter is optional and its default value [AssociatedSaveMode.UPDATE]
     *
     * @param con An optional JDBC connection. If it is null,
     * a connection will be brown from [org.babyfish.jimmer.sql.runtime.ConnectionManager]
     *
     * @param block An optional lambda to add additional configuration.
     * In addition to setting the [SaveMode] of aggregate root
     * and the default value of the [AssociatedSaveMode] of associated objects,
     * you can add any other configuration.
     *
     * @param [E] The type of saved entity
     *
     * @return The saved result for single object
     */
    @Deprecated("Will be removed")
    fun <E : Any> update(
        input: Input<E>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.UPDATE,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KSimpleSaveResult<E> =
        save(input.toEntity(), con) {
            setMode(SaveMode.UPDATE_ONLY)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    /**
     * Merge an entity object
     * @param entity The merged entity object.
     *
     * **Note: The jimmer entity is <b>not POJO**,
     * it can easily express data structures of arbitrary shape,
     * you can use it to save data structures of arbitrary shape.
     *
     * Unlike most JVM ORMs, Jimmer does not specified the shape
     * of the saved data structure by using configuration such as
     * `insertable`, `updatable` or `cascade`; instead,
     * it uses the dynamic nature of entity object itself to describe
     * the shape of saved data structure, **without prior design**
     *
     * Unspecified properties will be ignored,
     * only the specified properties *(whether null or not)* will be saved.
     * In addition to objects with only id property, any associated objects
     * will result in deeper recursive saves.
     *
     * The [SaveMode] of aggregate-root is [SaveMode.UPSERT]
     *
     * @param associatedMode The save mode of associated-objects.
     * This parameter is optional and its default value [AssociatedSaveMode.UPDATE]
     *
     * @param con An optional JDBC connection. If it is null,
     * a connection will be brown from [org.babyfish.jimmer.sql.runtime.ConnectionManager]
     *
     * @param block An optional lambda to add additional configuration.
     * In addition to setting the [SaveMode] of aggregate root
     * and the default value of the [AssociatedSaveMode] of associated objects,
     * you can add any other configuration.
     *
     * @param [E] The type of saved entity
     *
     * @return The saved result for single object
     */
    @Deprecated("Will be removed")
    fun <E : Any> merge(
        entity: E,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KSimpleSaveResult<E> =
        save(entity, con) {
            setMode(SaveMode.UPSERT)
            setAssociatedModeAll(AssociatedSaveMode.MERGE)
            block?.invoke(this)
        }

    /**
     * Merge an input DTO
     * @param input The merged input DTOs.
     *
     * In terms of internal mechanisms, any type of Input DTO is
     * automatically converted into an entity object of the same type
     *
     * **Note: The jimmer entity is <b>not POJO**,
     * it can easily express data structures of arbitrary shape,
     * you can use it to save data structures of arbitrary shape.
     *
     * Unlike most JVM ORMs, Jimmer does not specified the shape
     * of the saved data structure by using configuration such as
     * `insertable`, `updatable` or `cascade`; instead,
     * it uses the dynamic nature of entity object itself to describe
     * the shape of saved data structure, **without prior design**
     *
     * Unspecified properties will be ignored,
     * only the specified properties *(whether null or not)* will be saved.
     * In addition to objects with only id property, any associated objects
     * will result in deeper recursive saves.
     *
     * The [SaveMode] of aggregate-root is [SaveMode.UPSERT]
     *
     * @param associatedMode The save mode of associated-objects.
     * This parameter is optional and its default value [AssociatedSaveMode.MERGE]
     *
     * @param con An optional JDBC connection. If it is null,
     * a connection will be brown from [org.babyfish.jimmer.sql.runtime.ConnectionManager]
     *
     * @param block An optional lambda to add additional configuration.
     * In addition to setting the [SaveMode] of aggregate root
     * and the default value of the [AssociatedSaveMode] of associated objects,
     * you can add any other configuration.
     *
     * @param [E] The type of saved entity
     *
     * @return The saved result for single object
     */
    @Deprecated("Will be removed")
    fun <E : Any> merge(
        input: Input<E>,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KSimpleSaveResult<E> =
        save(input.toEntity(), con) {
            setMode(SaveMode.UPSERT)
            setAssociatedModeAll(AssociatedSaveMode.MERGE)
            block?.invoke(this)
        }

    @Deprecated("Will be removed")
    fun <E: Any> insertEntities(
        entities: Iterable<E>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.APPEND,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KBatchSaveResult<E> =
        this.saveEntities(entities, con) {
            setMode(SaveMode.INSERT_ONLY)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    @Deprecated("Will be removed")
    fun <E: Any> insertInputs(
        inputs: Iterable<Input<E>>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.APPEND,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KBatchSaveResult<E> =
        this.saveInputs(inputs, con) {
            setMode(SaveMode.INSERT_ONLY)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    @Deprecated("Will be removed")
    fun <E: Any> insertEntitiesIfAbsent(
        entities: Iterable<E>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.APPEND_IF_ABSENT,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KBatchSaveResult<E> =
        this.saveEntities(entities, con) {
            setMode(SaveMode.INSERT_IF_ABSENT)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    @Deprecated("Will be removed")
    fun <E: Any> insertInputsIfAbsent(
        inputs: Iterable<Input<E>>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.APPEND_IF_ABSENT,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KBatchSaveResult<E> =
        this.saveInputs(inputs, con) {
            setMode(SaveMode.INSERT_IF_ABSENT)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    @Deprecated("Will be removed")
    fun <E: Any> updateEntities(
        entities: Iterable<E>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.UPDATE,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KBatchSaveResult<E> =
        this.saveEntities(entities, con) {
            setMode(SaveMode.UPDATE_ONLY)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    @Deprecated("Will be removed")
    fun <E: Any> updateInputs(
        inputs: Iterable<Input<E>>,
        associatedMode: AssociatedSaveMode = AssociatedSaveMode.UPDATE,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KBatchSaveResult<E> =
        this.saveInputs(inputs, con) {
            setMode(SaveMode.UPDATE_ONLY)
            setAssociatedModeAll(associatedMode)
            block?.invoke(this)
        }

    @Deprecated("Will be removed")
    fun <E: Any> mergeEntities(
        entities: Iterable<E>,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KBatchSaveResult<E> =
        this.saveEntities(entities, con) {
            setMode(SaveMode.UPSERT)
            setAssociatedModeAll(AssociatedSaveMode.MERGE)
            block?.invoke(this)
        }

    @Deprecated("Will be removed")
    fun <E: Any> mergeInputs(
        inputs: Iterable<Input<E>>,
        con: Connection? = null,
        block: (KSaveCommandPartialDsl.() -> Unit)? = null
    ): KBatchSaveResult<E> =
        this.saveInputs(inputs, con) {
            setMode(SaveMode.UPSERT)
            setAssociatedModeAll(AssociatedSaveMode.MERGE)
            block?.invoke(this)
        }
}
