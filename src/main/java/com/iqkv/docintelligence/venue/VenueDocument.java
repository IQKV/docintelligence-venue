/*
 * Copyright 2026 iQKV Foundation Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iqkv.docintelligence.venue;

import java.time.Instant;
import java.util.UUID;

/**
 * A tenant-owned venue document (venue deck) — one row of the {@code venue_documents} table in a
 * tenant schema ({@code t_{tenantKey}}).
 *
 * <p>Stores all file info captured during the two-phase presigned upload. A row is created at
 * confirm time, after the client has PUT the file to MinIO, with {@code status}
 * {@code UPLOADED}. The status later transitions {@code PARSING} -&gt; {@code EXTRACTED}, or
 * {@code FAILED}, as the processing worker picks the document up. {@code objectKey} is the
 * tenant-scoped S3/MinIO key (it embeds the tenant key and document id, so confirm can validate
 * ownership by prefix).
 *
 * <p>The table is created and owned by {@code docintelligence-catalog-service}, which owns every
 * migration. This type carries the columns only and never issues DDL.
 */
public class VenueDocument {

  private UUID id;
  private String fileName;
  private String contentType;
  private Long sizeBytes;
  private String objectKey;
  private String status;
  private UUID uploadedBy;
  private Instant createdAt;
  private Instant updatedAt;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(final String contentType) {
    this.contentType = contentType;
  }

  public Long getSizeBytes() {
    return sizeBytes;
  }

  public void setSizeBytes(final Long sizeBytes) {
    this.sizeBytes = sizeBytes;
  }

  public String getObjectKey() {
    return objectKey;
  }

  public void setObjectKey(final String objectKey) {
    this.objectKey = objectKey;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public UUID getUploadedBy() {
    return uploadedBy;
  }

  public void setUploadedBy(final UUID uploadedBy) {
    this.uploadedBy = uploadedBy;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(final Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
