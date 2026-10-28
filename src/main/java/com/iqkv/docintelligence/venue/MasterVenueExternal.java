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
 * One external identity of a master venue — a row of {@code public.master_venue_external}.
 *
 * <p>Platform-level data, not tenant-scoped: it lives in the {@code public} schema and is never
 * copied into a tenant schema. It maps the provider's own identifier for a venue
 * ({@code provider} + {@code providerExternalId}) to the master venue of record, and keeps the
 * raw provider payload the match was made from.
 *
 * <p>{@code (provider, providerExternalId)} is unique — {@code uq_master_venue_external_identity} —
 * so re-ingesting the same provider venue resolves to the same row instead of a second master
 * venue. {@code providerExternalId} follows the suite-wide format
 * {@code <provider>:venue:<provider venue id>}.
 *
 * <p>The table is created and owned by {@code docintelligence-catalog-service}, which owns every
 * migration; {@code docintelligence-master-venue-worker} is its sole writer. This type carries
 * the columns only and never issues DDL.
 */
public class MasterVenueExternal {

  private UUID id;
  private UUID masterVenueId;
  private String provider;
  private String providerExternalId;
  private String rawPayload;
  private BigDecimal confidence;
  private Instant crawledAt;

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

  public String getProvider() {
    return provider;
  }

  public void setProvider(final String provider) {
    this.provider = provider;
  }

  public String getProviderExternalId() {
    return providerExternalId;
  }

  public void setProviderExternalId(final String providerExternalId) {
    this.providerExternalId = providerExternalId;
  }

  public String getRawPayload() {
    return rawPayload;
  }

  public void setRawPayload(final String rawPayload) {
    this.rawPayload = rawPayload;
  }

  public BigDecimal getConfidence() {
    return confidence;
  }

  public void setConfidence(final BigDecimal confidence) {
    this.confidence = confidence;
  }

  public Instant getCrawledAt() {
    return crawledAt;
  }

  public void setCrawledAt(final Instant crawledAt) {
    this.crawledAt = crawledAt;
  }
}
