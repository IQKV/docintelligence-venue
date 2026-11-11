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
 * A tenant-owned asset of a tenant venue — one row of the {@code venue_assets} table in a tenant
 * schema ({@code t_{tenantKey}}).
 *
 * <p>The tenant counterpart of {@link MasterVenueAsset}: it carries the same asset taxonomy
 * ({@code assetType}, {@code photoCategory}, {@code displayOrder}, {@code label}) and the same
 * stored-file columns, keyed on the tenant {@link Venue} instead of a master venue. The master
 * provenance columns ({@code sourceUrl}, {@code provider}) have no tenant meaning — a tenant asset
 * is uploaded through the two-phase presigned flow, so the row carries {@code uploadedBy} instead.
 *
 * <p>Tenant-scoped data: it lives in the tenant schema, not in {@code public}. The table is created
 * and owned by {@code docintelligence-catalog-service}, which owns every migration.
 * {@code venue_id} references {@code venues(id)} with {@code ON DELETE CASCADE}, so removing a
 * tenant venue removes its assets.
 *
 * <p>{@code venueId} is optional: a PDF/CSV/image is uploaded first and the venue record is derived
 * from it by {@code docintelligence-catalog-processing-worker}, so an asset may exist before any
 * venue and carries {@code null} until the worker links it.
 *
 * <p>{@code status} tracks that pipeline: {@code UPLOADED} (confirmed and awaiting the worker),
 * {@code PARSING} (worker is reading the file), then {@code EXTRACTED} (terminal — the venue is
 * available, or the asset needs no parsing) or {@code FAILED} (with {@code failureReason} set).
 *
 * <p>{@code s3Key} is the tenant-scoped S3/MinIO object key. A venue-less import follows
 * {@code docintelligence/tenant/{tenantKey}/assets/{assetId}/{fileName}}; an asset attached
 * directly to a venue follows
 * {@code docintelligence/tenant/{tenantKey}/venues/{venueId}/assets/{assetId}/{fileName}}.
 */
public class VenueAsset {

  /** Confirmed and awaiting the processing worker. Set by the upload flow. */
  public static final String STATUS_UPLOADED = "UPLOADED";
  /** The processing worker is reading the file. */
  public static final String STATUS_PARSING = "PARSING";
  /** Terminal success — the venue derived from the asset is available. */
  public static final String STATUS_EXTRACTED = "EXTRACTED";
  /**
   * Terminal failure — {@code failureReason} carries the cause.
   */
  public static final String STATUS_FAILED = "FAILED";

  private UUID id;
  private UUID venueId;
  private String assetType;
  private String photoCategory;
  private Integer displayOrder;
  private String label;
  private String fileName;
  private String contentType;
  private Long sizeBytes;
  private String s3Key;
  private UUID uploadedBy;
  private String status;
  private String failureReason;
  private Instant createdAt;
  private Instant updatedAt;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public UUID getVenueId() {
    return venueId;
  }

  public void setVenueId(final UUID venueId) {
    this.venueId = venueId;
  }

  public String getAssetType() {
    return assetType;
  }

  public void setAssetType(final String assetType) {
    this.assetType = assetType;
  }

  public String getPhotoCategory() {
    return photoCategory;
  }

  public void setPhotoCategory(final String photoCategory) {
    this.photoCategory = photoCategory;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(final Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(final String label) {
    this.label = label;
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

  public String getS3Key() {
    return s3Key;
  }

  public void setS3Key(final String s3Key) {
    this.s3Key = s3Key;
  }

  public UUID getUploadedBy() {
    return uploadedBy;
  }

  public void setUploadedBy(final UUID uploadedBy) {
    this.uploadedBy = uploadedBy;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public String getFailureReason() {
    return failureReason;
  }

  public void setFailureReason(final String failureReason) {
    this.failureReason = failureReason;
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
