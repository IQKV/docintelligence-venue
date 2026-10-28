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
 * Master venue asset reads and writes against {@code public.master_venue_asset}.
 *
 * <p>This is one of the few mappers allowed to name the {@code public} schema explicitly —
 * master venue assets are platform-owned reference data, never tenant-scoped.
 * {@code created_at} / {@code updated_at} are maintained by the database.
 *
 * <p>The matching statements live in {@code mappers/venue/MasterVenueAssetMapper.xml}, which
 * ships inside this artifact; consumers pick it up through their existing
 * {@code classpath*:mappers/} scan. Write ownership of the table belongs to
 * {@code docintelligence-master-venue-worker} — a consumer that only reads must not call
 * {@link #insert(MasterVenueAsset)}, {@link #update(MasterVenueAsset)},
 * {@link #deleteById(UUID)} or {@link #deleteByMasterVenueId(UUID)}.
 */
@Mapper
public interface MasterVenueAssetMapper {

  void insert(MasterVenueAsset asset);

  void update(MasterVenueAsset asset);

  void deleteById(@Param("id") UUID id);

  /**
   * Removes every asset of a venue in one statement.
   *
   * <p>Usually unnecessary: deleting the {@link MasterVenue} row cascades to its assets.
   *
   * @param masterVenueId the owning venue whose assets are deleted
   */
  void deleteByMasterVenueId(@Param("masterVenueId") UUID masterVenueId);

  Optional<MasterVenueAsset> findById(@Param("id") UUID id);

  /**
   * All assets of one venue, ordered by {@code display_order} then id — the order the gallery
   * renders in. Never {@code null}; an unknown venue yields an empty list.
   *
   * @param masterVenueId the owning venue
   * @return the venue's assets, ordered by display order
   */
  List<MasterVenueAsset> findByMasterVenueId(@Param("masterVenueId") UUID masterVenueId);

  /**
   * The photos of one venue — the subset of {@link #findByMasterVenueId(UUID)} whose
   * {@code asset_type} is {@code PHOTO}, in the same display order. Never {@code null}; an
   * unknown venue or a venue without photos yields an empty list.
   *
   * @param masterVenueId the owning venue
   * @return the venue's photos, ordered by display order
   */
  List<MasterVenueAsset> findPhotosByMasterVenueId(@Param("masterVenueId") UUID masterVenueId);
}
