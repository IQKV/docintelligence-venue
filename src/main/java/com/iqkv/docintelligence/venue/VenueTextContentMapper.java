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
 * Tenant venue text-content reads and writes against the {@code venue_text_contents} table in a
 * tenant schema ({@code t_{tenantKey}}).
 *
 * <p>Statements are unqualified by schema — the {@code MyBatisSchemaInterceptor} in each consuming
 * service sets {@code search_path} to {@code t_{tenantKey}, public} before every statement, so
 * {@code venue_text_contents} resolves to the active tenant schema.
 * {@code created_at} / {@code updated_at} are maintained by the database.
 *
 * <p>The matching statements live in {@code mappers/venue/VenueTextContentMapper.xml}, which ships
 * inside this artifact; consumers pick it up through their existing {@code classpath*:mappers/}
 * scan.
 */
@Mapper
public interface VenueTextContentMapper {

  /**
   * Inserts a new row. The caller must set {@code id} on the entity before calling this method.
   *
   * @param content the row to insert
   */
  void insert(VenueTextContent content);

  /**
   * Returns the row with the given id, or {@link Optional#empty()} when it does not exist.
   *
   * @param id the row id
   * @return the matching row, or empty
   */
  Optional<VenueTextContent> findById(@Param("id") UUID id);

  /**
   * All text-content rows linked to one venue, ordered by {@code created_at} ascending (oldest
   * first). Never {@code null}; an unknown venue yields an empty list.
   *
   * @param venueId the owning venue
   * @return the venue's text-content rows, oldest first
   */
  List<VenueTextContent> findByVenueId(@Param("venueId") UUID venueId);

  /**
   * All text-content rows linked to one source asset, ordered by {@code created_at} ascending.
   * Never {@code null}; an unknown asset yields an empty list.
   *
   * @param sourceAssetId the source asset
   * @return the text-content rows extracted from the given asset, oldest first
   */
  List<VenueTextContent> findBySourceAssetId(@Param("sourceAssetId") UUID sourceAssetId);

  /**
   * All text-content rows whose {@code venueId} is still {@code NULL}, ordered by
   * {@code created_at} ascending, up to {@code limit} rows. Used by the processing worker to
   * link rows to venues.
   *
   * @param limit maximum number of rows to return
   * @return unlinked rows, oldest first
   */
  List<VenueTextContent> findUnlinked(@Param("limit") int limit);

  /**
   * Full-text keyword search over {@code content}, optionally filtered by venue.
   * Both filters are optional: {@code null} or blank values drop that predicate.
   *
   * @param venueId optional venue filter
   * @param search  free-text keyword matched case-insensitively against {@code content}; nullable
   * @param limit   maximum number of rows to return
   * @param offset  zero-based row offset
   * @return matching rows ordered by {@code created_at} descending
   */
  List<VenueTextContent> search(@Param("venueId") UUID venueId,
                                @Param("search") String search,
                                @Param("limit") int limit,
                                @Param("offset") int offset);

  /**
   * Counts the rows {@link #search} would return for the same filters.
   *
   * @param venueId optional venue filter
   * @param search  free-text keyword; nullable
   * @return total number of matching rows
   */
  long countSearch(@Param("venueId") UUID venueId, @Param("search") String search);

  /**
   * Updates the {@code content} of one row without touching any other field.
   * Maintains {@code updated_at}. A no-op when no such row exists.
   *
   * @param id      the row to update
   * @param content the new content
   */
  void updateContent(@Param("id") UUID id, @Param("content") String content);

  /**
   * Links one text-content row to a venue. Used by the processing worker after deriving the venue.
   * Maintains {@code updated_at}. A no-op when no such row exists.
   *
   * @param id      the row to link
   * @param venueId the venue to link to
   */
  void linkVenue(@Param("id") UUID id, @Param("venueId") UUID venueId);

  /**
   * Sets the {@code embedding} column for a single row without touching any other field.
   * Intended for use by the embedding service and the backfill scheduler.
   *
   * @param id        the row id
   * @param embedding the embedding vector produced by the model
   */
  void updateEmbedding(@Param("id") UUID id, @Param("embedding") float[] embedding);

  /**
   * Returns the ids of rows whose {@code embedding} column is still {@code NULL}, ordered by
   * {@code created_at} ascending (oldest-first), up to {@code limit} rows. Used by the backfill
   * scheduler.
   *
   * @param limit maximum number of ids to return
   * @return list of unembedded row ids, never {@code null}
   */
  List<UUID> findUnembeddedIds(@Param("limit") int limit);

  /**
   * Permanently deletes the row with the given id. A no-op when no such row exists.
   *
   * @param id the row to delete
   */
  void deleteById(@Param("id") UUID id);

  /**
   * Permanently deletes all rows linked to the given venue. Used to clean up when a venue is
   * deleted; the cascade is enforced at the application layer since the FK is optional.
   *
   * @param venueId the venue whose rows to remove
   */
  void deleteByVenueId(@Param("venueId") UUID venueId);
}
