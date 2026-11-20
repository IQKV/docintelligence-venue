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
import java.util.List;
import java.util.UUID;

/**
 * A user-authored sticky note attached to a tenant venue — one row of the
 * {@code venue_smart_notes} table in a tenant schema ({@code t_{tenantKey}}).
 *
 * <p>Tenant-scoped data: it lives in the tenant schema, not in {@code public}. The table is
 * created and owned by {@code docintelligence-catalog-service}, which owns every migration.
 * {@code venue_id} references {@code venues(id)} — the relationship is many notes to one venue.
 * No DB-level FK is declared; the application enforces the constraint.
 *
 * <p>{@code content} is an HTML string produced by the Mantine rich-text editor and stored as-is.
 * Consumers must sanitise before rendering.
 *
 * <p>{@code color} is a free-form string (hex code or design-token name) that controls the note
 * card background in the UI — analogous to Google Keep note colours. {@code null} means the
 * default colour applies.
 *
 * <p>{@code tags} is a PostgreSQL {@code text[]} array; {@code null} and an empty array are both
 * valid and mean "no tags". Mapped by
 * {@code com.iqkv.foundation.mybatis.util.PgTextArrayTypeHandler}.
 *
 * <p>{@code authorId} is the UUID of the user who created the note. It is set at insert time and
 * never changed.
 *
 * <p>{@code linkedAssetId} is an optional app-level reference to a {@code venue_assets} row.
 * No DB-level FK — the asset may be deleted independently. {@code null} when the note has no
 * asset link.
 *
 * <p>{@code sortOrder} controls the display order of notes within a venue. Lower values appear
 * first. Defaults to {@code 0}; ties are broken by {@code created_at} ascending.
 */
public class VenueSmartNote {

  private UUID id;
  /**
   * App-level FK to the tenant {@code venues} row; required.
   */
  private UUID venueId;
  /**
   * Note title; {@code null} when the note has no title.
   */
  private String title;
  /**
   * HTML content produced by the Mantine rich-text editor; {@code null} when the note has no
   * body.
   */
  private String content;
  /**
   * Note card background colour — a hex code (e.g. {@code #fff9c4}) or a design-token name.
   * {@code null} means the UI default applies.
   */
  private String color;
  /**
   * User-defined tags stored as a PostgreSQL {@code text[]} array, mapped to a {@code List}.
   * {@code null} and empty mean the same: no tags.
   */
  private List<String> tags;
  /**
   * UUID of the user who created the note. Set at insert time, never updated.
   */
  private UUID authorId;
  /**
   * Optional app-level reference to a {@code venue_assets} row. {@code null} when the note is
   * not linked to an asset.
   */
  private UUID linkedAssetId;
  /**
   * Display order within the venue. Lower values appear first; ties broken by
   * {@code created_at} ascending. Defaults to {@code 0}.
   */
  private int sortOrder;
  /**
   * Whether the note is pinned. Pinned notes are displayed prominently in the UI regardless of
   * sort order. Defaults to {@code false}.
   */
  private boolean pinned;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(final String content) {
    this.content = content;
  }

  public String getColor() {
    return color;
  }

  public void setColor(final String color) {
    this.color = color;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(final List<String> tags) {
    this.tags = tags;
  }

  public UUID getAuthorId() {
    return authorId;
  }

  public void setAuthorId(final UUID authorId) {
    this.authorId = authorId;
  }

  public UUID getLinkedAssetId() {
    return linkedAssetId;
  }

  public void setLinkedAssetId(final UUID linkedAssetId) {
    this.linkedAssetId = linkedAssetId;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(final int sortOrder) {
    this.sortOrder = sortOrder;
  }

  public boolean isPinned() {
    return pinned;
  }

  public void setPinned(final boolean pinned) {
    this.pinned = pinned;
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
