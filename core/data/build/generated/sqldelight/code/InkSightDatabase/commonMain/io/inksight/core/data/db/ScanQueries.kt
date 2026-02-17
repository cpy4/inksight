package io.inksight.core.`data`.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class ScanQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> getAllScans(mapper: (
    id: String,
    imagePath: String,
    transcribedText: String,
    createdAt: Long,
    updatedAt: Long,
    status: String,
    imageWidth: Long?,
    imageHeight: Long?,
  ) -> T): Query<T> = Query(1_840_277_768, arrayOf("ScanEntity"), driver, "Scan.sq", "getAllScans", "SELECT ScanEntity.id, ScanEntity.imagePath, ScanEntity.transcribedText, ScanEntity.createdAt, ScanEntity.updatedAt, ScanEntity.status, ScanEntity.imageWidth, ScanEntity.imageHeight FROM ScanEntity ORDER BY createdAt DESC") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6),
      cursor.getLong(7)
    )
  }

  public fun getAllScans(): Query<ScanEntity> = getAllScans(::ScanEntity)

  public fun <T : Any> getScanById(id: String, mapper: (
    id: String,
    imagePath: String,
    transcribedText: String,
    createdAt: Long,
    updatedAt: Long,
    status: String,
    imageWidth: Long?,
    imageHeight: Long?,
  ) -> T): Query<T> = GetScanByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6),
      cursor.getLong(7)
    )
  }

  public fun getScanById(id: String): Query<ScanEntity> = getScanById(id, ::ScanEntity)

  public fun <T : Any> searchScans(`value`: String, mapper: (
    id: String,
    imagePath: String,
    transcribedText: String,
    createdAt: Long,
    updatedAt: Long,
    status: String,
    imageWidth: Long?,
    imageHeight: Long?,
  ) -> T): Query<T> = SearchScansQuery(value) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6),
      cursor.getLong(7)
    )
  }

  public fun searchScans(value_: String): Query<ScanEntity> = searchScans(value_, ::ScanEntity)

  /**
   * @return The number of rows updated.
   */
  public fun insertScan(
    id: String,
    imagePath: String,
    transcribedText: String,
    createdAt: Long,
    updatedAt: Long,
    status: String,
    imageWidth: Long?,
    imageHeight: Long?,
  ): QueryResult<Long> {
    val result = driver.execute(623_735_321, """
        |INSERT OR REPLACE INTO ScanEntity(id, imagePath, transcribedText, createdAt, updatedAt, status, imageWidth, imageHeight)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 8) {
          var parameterIndex = 0
          bindString(parameterIndex++, id)
          bindString(parameterIndex++, imagePath)
          bindString(parameterIndex++, transcribedText)
          bindLong(parameterIndex++, createdAt)
          bindLong(parameterIndex++, updatedAt)
          bindString(parameterIndex++, status)
          bindLong(parameterIndex++, imageWidth)
          bindLong(parameterIndex++, imageHeight)
        }
    notifyQueries(623_735_321) { emit ->
      emit("ScanEntity")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun updateScan(
    transcribedText: String,
    updatedAt: Long,
    status: String,
    id: String,
  ): QueryResult<Long> {
    val result = driver.execute(-638_361_559, """
        |UPDATE ScanEntity SET
        |    transcribedText = ?,
        |    updatedAt = ?,
        |    status = ?
        |WHERE id = ?
        """.trimMargin(), 4) {
          var parameterIndex = 0
          bindString(parameterIndex++, transcribedText)
          bindLong(parameterIndex++, updatedAt)
          bindString(parameterIndex++, status)
          bindString(parameterIndex++, id)
        }
    notifyQueries(-638_361_559) { emit ->
      emit("ScanEntity")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public fun deleteScan(id: String): QueryResult<Long> {
    val result = driver.execute(1_422_158_859, """DELETE FROM ScanEntity WHERE id = ?""", 1) {
          var parameterIndex = 0
          bindString(parameterIndex++, id)
        }
    notifyQueries(1_422_158_859) { emit ->
      emit("ScanEntity")
    }
    return result
  }

  private inner class GetScanByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("ScanEntity", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("ScanEntity", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> = driver.executeQuery(-837_419_902, """SELECT ScanEntity.id, ScanEntity.imagePath, ScanEntity.transcribedText, ScanEntity.createdAt, ScanEntity.updatedAt, ScanEntity.status, ScanEntity.imageWidth, ScanEntity.imageHeight FROM ScanEntity WHERE id = ?""", mapper, 1) {
      var parameterIndex = 0
      bindString(parameterIndex++, id)
    }

    override fun toString(): String = "Scan.sq:getScanById"
  }

  private inner class SearchScansQuery<out T : Any>(
    public val `value`: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("ScanEntity", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("ScanEntity", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> = driver.executeQuery(1_719_839_723, """
    |SELECT ScanEntity.id, ScanEntity.imagePath, ScanEntity.transcribedText, ScanEntity.createdAt, ScanEntity.updatedAt, ScanEntity.status, ScanEntity.imageWidth, ScanEntity.imageHeight FROM ScanEntity
    |WHERE transcribedText LIKE '%' || ? || '%'
    |ORDER BY createdAt DESC
    """.trimMargin(), mapper, 1) {
      var parameterIndex = 0
      bindString(parameterIndex++, value)
    }

    override fun toString(): String = "Scan.sq:searchScans"
  }
}
