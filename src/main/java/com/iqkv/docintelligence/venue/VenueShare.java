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
 * A collaboration grant that shares a tenant venue with a specific user — one row of the
 * {@code venue_shares} table in a tenant schema ({@code t_{tenantKey}}).
 *
 * <p>Tenant-scoped data: it lives in the tenant schema, not in {@code public}. The table is
 * created and owned by {@code docintelligence-catalog-service}, which owns every migration.
 * {@code venue_id} is an app-level FK to {@code venues(id)}; there is no DB-level FK because
 * the application enforces the constraint and handles cascade deletion via
 * {@link VenueShareMapper#deleteByVenueId(UUID)}.
 *
 * <p>This record is meaningful only in personal workspaces ({@code t_platform} schema), where
 * {@link Venue#getCreatedBy()} is the owner and access is otherwise private. In org-tenant
 * schemas all members already see {@link Venue#VISIBILITY_PUBLIC} venues; share grants there
 * exist for granting elevated edit or admin access to a specific user beyond the default read.
 *
 * <p>{@code role} controls what the grantee can do with the venue. Use the {@code ROLE_*}
 * constants:
 * <ul>
 *   <li>{@link #ROLE_ADMIN} — can edit, delete, and re-share the venue; cannot transfer
 *       ownership.</li>
 *   <li>{@link #ROLE_EDITOR} — can edit the venue and its subordinate records (assets, notes,
 *       text content); cannot delete or re-share.</li>
 * </ul>
 * Delete permission belongs to the venue owner ({@link Venue#getCreatedBy()}) and to any
 * user holding an {@link #ROLE_ADMIN} grant on that venue.
 *
 * <p>A share is replaced rather than updated: to change a grantee's role, delete the existing
 * row and insert a new one, or use {@link VenueShareMapper#updateRole(UUID, UUID, String)}.
 * There is therefore no {@code updated_at} column.
 *
 * <p>{@code grantedBy} records which user issued the grant. Only the venue owner or an
 * {@link #ROLE_ADMIN} grantee may create or revoke shares.
 */
public class VenueShare {

  /**
   * Grantee can edit, delete, and re-share the venue. Cannot transfer ownership.
   */
  public static final String ROLE_ADMIN = "ADMIN";
  /**
   * Grantee can edit the venue and its subordinate records. Cannot delete or re-share.
   */
  public static final String ROLE_EDITOR = "EDITOR";

  private UUID id;
  /**
   * App-level FK to the tenant {@code venues} row; required.
   */
  private UUID venueId;
  /**
   * UUID of the user receiving the grant.
   */
  private UUID sharedWithUserId;
  /**
   * Collaboration role — one of {@link #ROLE_ADMIN} or {@link #ROLE_EDITOR}.
   */
  private String role;
  /**
   * UUID of the user who issued the grant. Set at insert time.
   */
  private UUID grantedBy;
  private Instant createdAt;

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

  public UUID getSharedWithUserId() {
    return sharedWithUserId;
  }

  public void setSharedWithUserId(final UUID sharedWithUserId) {
    this.sharedWithUserId = sharedWithUserId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(final String role) {
    this.role = role;
  }

  public UUID getGrantedBy() {
    return grantedBy;
  }

  public void setGrantedBy(final UUID grantedBy) {
    this.grantedBy = grantedBy;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final Instant createdAt) {
    this.createdAt = createdAt;
  }
}
