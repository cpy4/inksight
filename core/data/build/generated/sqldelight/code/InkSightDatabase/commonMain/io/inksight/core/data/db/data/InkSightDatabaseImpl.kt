package io.inksight.core.`data`.db.`data`

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.inksight.core.`data`.db.InkSightDatabase
import io.inksight.core.`data`.db.ScanQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<InkSightDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = InkSightDatabaseImpl.Schema

internal fun KClass<InkSightDatabase>.newInstance(driver: SqlDriver): InkSightDatabase = InkSightDatabaseImpl(driver)

private class InkSightDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver),
    InkSightDatabase {
  override val scanQueries: ScanQueries = ScanQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE ScanEntity (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    imagePath TEXT NOT NULL,
          |    transcribedText TEXT NOT NULL DEFAULT '',
          |    createdAt INTEGER NOT NULL,
          |    updatedAt INTEGER NOT NULL,
          |    status TEXT NOT NULL DEFAULT 'PENDING',
          |    imageWidth INTEGER,
          |    imageHeight INTEGER
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
