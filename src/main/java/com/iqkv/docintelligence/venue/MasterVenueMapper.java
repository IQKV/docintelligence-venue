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
 * Master venue reads and writes against {@code public.master_venue}.
 *
 * <p>This is one of the few mappers allowed to name the {@code public} schema explicitly —
 * master venue rows are platform-owned reference data, never tenant-scoped.
 * {@code created_at} / {@code updated_at} are maintained by the database.
 *
 * <p>The matching statements live in {@code mappers/venue/MasterVenueMapper.xml}, which ships
 * inside this artifact; consumers pick it up through their existing
 * {@code classpath*:mappers/} scan. Write ownership of the table belongs to
 * {@code docintelligence-master-venue-worker} — a consumer that only reads must not call
 * {@link #insert(MasterVenue)}, {@link #update(MasterVenue)}, {@link #deleteById(UUID)} or
 * {@link #upsertWithExternalIdentity(MasterVenue, MasterVenueExternal)}.
 */
@Mapper
public interface MasterVenueMapper {

  void insert(MasterVenue venue);

  void update(MasterVenue venue);

  void deleteById(@Param("id") UUID id);

  /**
   * Upserts the venue together with the external identity it arrives under, in one statement.
   *
   * <p>The identity decides which row is written: on a known
   * {@code (provider, providerExternalId)} pair the upsert lands on the venue that identity
   * already resolves to, so {@code venue.getId()} is only honoured when the identity is new.
   * The venue columns and {@code updated_at} are refreshed either way, and the row that was
   * written — with the ids and timestamps as stored — comes back, so a caller that resolved an
   * existing venue through this call learns its id.
   *
   * <p>Executed as a query rather than an insert because PostgreSQL returns the row
   * ({@code INSERT … RETURNING}); both tables are written in a single statement, so a
   * re-delivered scrape cannot leave the identity and the venue row disagreeing.
   *
   * @param venue    the venue fields to write; {@code id} is the candidate id for a new identity
   * @param external the provider identity to bind the venue to, with its raw payload
   * @return the master venue of record after the upsert
   */
  MasterVenue upsertWithExternalIdentity(@Param("venue") MasterVenue venue,
                                         @Param("external") MasterVenueExternal external);

  Optional<MasterVenue> findById(@Param("id") UUID id);

  List<MasterVenue> findAll(@Param("limit") int limit, @Param("offset") int offset);

  long countAll();

  /**
   * Exact-name lookup ignoring case — the deduplication primitive available before the
   * pg_trgm similarity slice lands.
   *
   * @param name the venue name to match, compared case-insensitively
   * @return all master venues whose name matches, ordered by name
   */
  List<MasterVenue> findByNameIgnoreCase(@Param("name") String name);

  /**
   * Keyword and city search over the master venue reference dataset.
   *
   * <p>Both filters are optional: a {@code null} or blank value drops that predicate.
   * The keyword is matched case-insensitively against name, address and city.
   *
   * @param search free-text keyword matched against name, address and city; nullable
   * @param city   exact city filter, compared case-insensitively; nullable
   * @param limit  maximum number of rows to return
   * @param offset zero-based row offset
   * @return matching venues ordered by name
   */
  List<MasterVenue> search(@Param("search") String search,
                           @Param("city") String city,
                           @Param("limit") int limit,
                           @Param("offset") int offset);

  /**
   * Counts the rows {@link #search(String, String, int, int)} would return for the same filters.
   *
   * @param search free-text keyword; nullable
   * @param city   exact city filter; nullable
   * @return total number of matching venues
   */
  long countSearch(@Param("search") String search, @Param("city") String city);
}
