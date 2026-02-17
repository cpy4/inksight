package io.inksight.core.`data`.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.inksight.core.`data`.db.`data`.newInstance
import io.inksight.core.`data`.db.`data`.schema
import kotlin.Unit

public interface InkSightDatabase : Transacter {
  public val scanQueries: ScanQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = InkSightDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): InkSightDatabase = InkSightDatabase::class.newInstance(driver)
  }
}
