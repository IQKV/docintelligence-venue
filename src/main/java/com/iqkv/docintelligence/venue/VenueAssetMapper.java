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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Tenant venue asset reads and writes against the {@code venue_assets} table in a tenant schema.
 *
 * <p>Statements are unqualified by schema — the {@code MyBatisSchemaInterceptor} in each consuming
 * service sets {@code search_path} to {@code t_{tenantKey}, public} before every statement, so
 * {@code venue_assets} resolves to the active tenant schema.
 * {@code created_at} / {@code updated_at} are maintained by the database.
 *
 * <p>Deleting a {@link Venue} row cascades to its assets, so no statement here removes assets of a
 * venue in bulk.
 */
@Mapper
public interface VenueAssetMapper {

  void insert(VenueAsset asset);

  Optional<VenueAsset> findById(@Param("id") UUID id);

  /**
   * All assets of one venue, ordered by {@code display_order} then id — the order the gallery
   * renders in. Never {@code null}; an unknown venue yields an empty list.
   *
   * @param venueId the owning venue
   * @return the venue's assets, ordered by display order
   */
  List<VenueAsset> findByVenueId(@Param("venueId") UUID venueId);

  /**
   * The photos of one venue — the subset of {@link #findByVenueId(UUID)} whose {@code asset_type}
   * is {@code PHOTO}, in the same display order. Never {@code null}; an unknown venue or a venue
   * without photos yields an empty list.
   *
   * @param venueId the owning venue
   * @return the venue's photos, ordered by display order
   */
  List<VenueAsset> findPhotosByVenueId(@Param("venueId") UUID venueId);

  /**
   * Permanently deletes the asset row with the given id. A no-op when no such row exists.
   *
   * @param id the asset to delete
   */
  void deleteById(@Param("id") UUID id);
}
