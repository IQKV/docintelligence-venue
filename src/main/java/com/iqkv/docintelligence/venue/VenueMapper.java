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
 * Tenant venue reads and writes against the {@code venues} table in a tenant schema.
 *
 * <p>Statements are unqualified by schema — the {@code MyBatisSchemaInterceptor} in each
 * consuming service sets {@code search_path} to {@code t_{tenantKey}, public} before every
 * statement, so {@code venues} resolves to the active tenant schema. This mapper must never
 * prefix the table with {@code public.} (that would read the master catalog instead).
 *
 * <p>The matching statements live in {@code mappers/venue/VenueMapper.xml}, which ships inside
 * this artifact; consumers pick it up through their existing {@code classpath*:mappers/} scan.
 * {@code created_at} / {@code updated_at} are maintained by the database.
 */
@Mapper
public interface VenueMapper {

  void insert(Venue venue);

  Optional<Venue> findById(@Param("id") UUID id);

  void update(Venue venue);

  void deleteById(@Param("id") UUID id);

  /**
   * Keyword and city search over the tenant venues.
   *
   * <p>Both filters are optional: a {@code null} or blank value drops that predicate.
   * The keyword is matched case-insensitively against name, address and city.
   * When {@code sortBy} is {@code "name"} the results are ordered by name in the direction
   * given by {@code sortDir} ({@code "asc"} or {@code "desc"}); any other value, including
   * {@code null}, falls back to {@code created_at DESC} (recently added first).
   *
   * @param search  free-text keyword matched against name, address and city; nullable
   * @param city    exact city filter, compared case-insensitively; nullable
   * @param sortBy  optional sort field — {@code "name"} or {@code null} for default
   * @param sortDir optional sort direction — {@code "asc"} or {@code "desc"}; nullable
   * @param limit   maximum number of rows to return
   * @param offset  zero-based row offset
   * @return matching tenant venues ordered by the requested sort, falling back to recently added
   */
  List<Venue> search(@Param("search") String search,
                     @Param("city") String city,
                     @Param("sortBy") String sortBy,
                     @Param("sortDir") String sortDir,
                     @Param("limit") int limit,
                     @Param("offset") int offset);

  /**
   * Counts the rows {@link #search(String, String, String, String, int, int)} would return
   * for the same filters.
   *
   * @param search free-text keyword; nullable
   * @param city   exact city filter; nullable
   * @return total number of matching tenant venues
   */
  long countSearch(@Param("search") String search, @Param("city") String city);

  /**
   * Sets the {@code embedding} column for a single venue without touching any other field.
   * Intended for use by the embedding service and the backfill scheduler.
   *
   * @param id        the venue UUID
   * @param embedding the embedding vector produced by the model
   */
  void updateEmbedding(@Param("id") UUID id, @Param("embedding") float[] embedding);

  /**
   * Returns the UUIDs of venues whose {@code embedding} column is still {@code NULL},
   * ordered by {@code created_at} ascending (oldest-first), up to {@code limit} rows.
   * Used by the backfill scheduler.
   *
   * @param limit maximum number of IDs to return
   * @return list of unembedded venue UUIDs, never {@code null}
   */
  List<UUID> findUnembeddedIds(@Param("limit") int limit);
}
