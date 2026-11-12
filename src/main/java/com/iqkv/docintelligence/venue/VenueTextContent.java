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
 * A tenant-owned chunk of raw, unstructured text derived from a source document (PDF deck,
 * notebook, CSV, etc.) and associated with a tenant venue — one row of the
 * {@code venue_text_contents} table in a tenant schema ({@code t_{tenantKey}}).
 *
 * <p>Tenant-scoped data: it lives in the tenant schema, not in {@code public}. The table is created
 * and owned by {@code docintelligence-catalog-service}, which owns every migration.
 *
 * <p>{@code venueId} is optional — a document is uploaded and processed before the venue record
 * is derived from it, so a row may carry {@code null} until the processing worker links it to a
 * venue. Once linked, the relationship is {@code 1 Venue → many VenueTextContent}.
 *
 * <p>{@code content} is the raw or markdown text extracted from the source document; it is the
 * single searchable payload of the row and feeds the semantic embedding pipeline.
 *
 * <p>{@code sourceAssetId} is an optional app-level reference to the {@code venue_assets} row that
 * the text was extracted from. No DB-level FK is declared because the asset row may be deleted
 * independently; {@code null} when the content was entered directly.
 *
 * <p>{@code embedding} holds the pgvector representation of {@code content} for semantic search.
 * {@code null} until the embedding pipeline runs. Dimension must match the configured Ollama
 * embedding model (nomic-embed-text → 768).
 */
public class VenueTextContent {

  private UUID id;
  /**
   * Optional app-level FK to the tenant {@code venues} row; {@code null} when not yet linked.
   */
  private UUID venueId;
  /**
   * Optional app-level reference to the {@code venue_assets} row this text was extracted from.
   * {@code null} when the content was entered directly or the asset has been deleted.
   */
  private UUID sourceAssetId;
  /**
   * Raw or markdown text content extracted from the source document.
   */
  private String content;
  /**
   * pgvector embedding of {@code content}; {@code null} when not yet embedded.
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

  public UUID getVenueId() {
    return venueId;
  }

  public void setVenueId(final UUID venueId) {
    this.venueId = venueId;
  }

  public UUID getSourceAssetId() {
    return sourceAssetId;
  }

  public void setSourceAssetId(final UUID sourceAssetId) {
    this.sourceAssetId = sourceAssetId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(final String content) {
    this.content = content;
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
