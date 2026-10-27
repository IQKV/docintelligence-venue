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
 * A platform-owned asset of a master venue — one row of the {@code public.master_venue_asset}
 * reference dataset.
 *
 * <p>Platform-level data, not tenant-scoped: it lives in the {@code public} schema, belongs to a
 * {@link MasterVenue} row and is never copied into a tenant schema. {@code provider} and
 * {@code sourceUrl} keep the provenance of the bytes so one table covers every ingestion
 * provider without joining the external record.
 *
 * <p>The table is created and owned by {@code docintelligence-catalog-service}, which owns every
 * migration; {@code docintelligence-master-venue-worker} is its sole writer. This type carries
 * the columns only and never issues DDL.
 *
 * <p>{@code s3Key} follows the master-catalog prefix convention
 * {@code docintelligence/master-catalog/venues/{venueId}/assets/{assetId}/{fileName}}, where
 * {@code venueId} and {@code assetId} are UUIDs without hyphens.
 */
public class MasterVenueAsset {

  private UUID id;
  private UUID masterVenueId;
  private String assetType;
  private String photoCategory;
  private Integer displayOrder;
  private String label;
  private String fileName;
  private String contentType;
  private Long sizeBytes;
  private String s3Key;
  private String sourceUrl;
  private String provider;
  private Instant createdAt;
  private Instant updatedAt;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public UUID getMasterVenueId() {
    return masterVenueId;
  }

  public void setMasterVenueId(final UUID masterVenueId) {
    this.masterVenueId = masterVenueId;
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

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(final String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(final String provider) {
    this.provider = provider;
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
