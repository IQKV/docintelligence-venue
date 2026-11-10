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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * A tenant-owned venue — one row of the {@code venues} table in a tenant schema
 * ({@code t_{tenantKey}}).
 *
 * <p>Tenant-scoped data: it lives in the tenant schema, not in {@code public}. It mirrors the
 * platform-owned {@link MasterVenue} columns and adds an optional {@code masterVenueId} relation
 * key to {@code public.master_venue}. That key is app-level only — there is no DB-level foreign
 * key, because cross-schema FKs are not supported across a tenant schema and {@code public}.
 * {@code null} means the tenant venue is not linked to a master record.
 *
 * <p>The table is created and owned by {@code docintelligence-catalog-service}, which owns every
 * migration. {@code metadata} is the raw JSONB canonical metadata document and carries a
 * mandatory {@code _schema_version} key. This type carries the columns only and never issues DDL.
 */
public class Venue {

  private UUID id;
  private String name;
  private String address;
  private String city;
  private BigDecimal locationLat;
  private BigDecimal locationLng;
  private String metadata;
  private BigDecimal confidence;
  private String source;
  /**
   * Optional app-level FK to {@code public.master_venue.id}; {@code null} when not linked.
   */
  private UUID masterVenueId;
  /**
   * Denormalised URL of the venue's primary photo; {@code null} when the venue has no photos.
   */
  private String primaryPhotoUrl;
  /**
   * pgvector embedding of the venue's searchable text fields; {@code null} when not yet embedded.
   * Dimension must match the configured Ollama embedding model (nomic-embed-text → 768).
   */
  private float[] embedding;
  private Instant createdAt;
  private Instant updatedAt;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(final String city) {
    this.city = city;
  }

  public BigDecimal getLocationLat() {
    return locationLat;
  }

  public void setLocationLat(final BigDecimal locationLat) {
    this.locationLat = locationLat;
  }

  public BigDecimal getLocationLng() {
    return locationLng;
  }

  public void setLocationLng(final BigDecimal locationLng) {
    this.locationLng = locationLng;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(final String metadata) {
    this.metadata = metadata;
  }

  public BigDecimal getConfidence() {
    return confidence;
  }

  public void setConfidence(final BigDecimal confidence) {
    this.confidence = confidence;
  }

  public String getSource() {
    return source;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  public UUID getMasterVenueId() {
    return masterVenueId;
  }

  public void setMasterVenueId(final UUID masterVenueId) {
    this.masterVenueId = masterVenueId;
  }

  public String getPrimaryPhotoUrl() {
    return primaryPhotoUrl;
  }

  public void setPrimaryPhotoUrl(final String primaryPhotoUrl) {
    this.primaryPhotoUrl = primaryPhotoUrl;
  }

  public float[] getEmbedding() {
    return embedding;
  }

  public void setEmbedding(final float[] embedding) {
    this.embedding = embedding;
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
