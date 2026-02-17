package io.inksight.core.`data`.db

import kotlin.Long
import kotlin.String

public data class ScanEntity(
  public val id: String,
  public val imagePath: String,
  public val transcribedText: String,
  public val createdAt: Long,
  public val updatedAt: Long,
  public val status: String,
  public val imageWidth: Long?,
  public val imageHeight: Long?,
)
