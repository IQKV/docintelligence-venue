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
 * Tenant venue-share reads and writes against the {@code venue_shares} table in a tenant schema
 * ({@code t_{tenantKey}}).
 *
 * <p>Statements are unqualified by schema — the {@code MyBatisSchemaInterceptor} in each consuming
 * service sets {@code search_path} to {@code t_{tenantKey}, public} before every statement, so
 * {@code venue_shares} resolves to the active tenant schema.
 * {@code created_at} is maintained by the database.
 *
 * <p>The matching statements live in {@code mappers/venue/VenueShareMapper.xml}, which ships
 * inside this artifact; consumers pick it up through their existing {@code classpath*:mappers/}
 * scan.
 */
@Mapper
public interface VenueShareMapper {

  /**
   * Inserts a new share grant. The caller must set {@code id} on the entity before calling
   * this method.
   *
   * @param share the grant to insert
   */
  void insert(VenueShare share);

  /**
   * All share grants on one venue, ordered by {@code created_at} ascending (oldest first).
   * Never {@code null}; an unknown or unshared venue yields an empty list.
   *
   * @param venueId the venue whose grants to return
   * @return all grants for the venue, oldest first
   */
  List<VenueShare> findByVenueId(@Param("venueId") UUID venueId);

  /**
   * The grant for a specific (venue, user) pair, or {@link Optional#empty()} when no such
   * grant exists. Used for access-control checks.
   *
   * @param venueId the venue
   * @param userId  the grantee
   * @return the matching grant, or empty
   */
  Optional<VenueShare> findByVenueIdAndUserId(@Param("venueId") UUID venueId,
                                              @Param("userId") UUID userId);

  /**
   * All venues shared with one user — the grantee's view. Ordered by {@code created_at}
   * descending (most recently granted first). Never {@code null}; a user with no grants yields
   * an empty list.
   *
   * @param userId the grantee
   * @param limit  maximum number of rows to return
   * @param offset zero-based row offset
   * @return share grants for the user, most recently granted first
   */
  List<VenueShare> findBySharedWithUserId(@Param("userId") UUID userId,
                                          @Param("limit") int limit,
                                          @Param("offset") int offset);

  /**
   * Counts the rows {@link #findBySharedWithUserId(UUID, int, int)} would return for the same
   * user.
   *
   * @param userId the grantee
   * @return total number of venues shared with the user
   */
  long countBySharedWithUserId(@Param("userId") UUID userId);

  /**
   * Updates the {@code role} of an existing (venue, user) grant without deleting and
   * re-inserting it. A no-op when no such grant exists.
   *
   * @param venueId the venue
   * @param userId  the grantee
   * @param role    the new role — one of {@link VenueShare#ROLE_ADMIN} or
   *                {@link VenueShare#ROLE_EDITOR}
   */
  void updateRole(@Param("venueId") UUID venueId,
                  @Param("userId") UUID userId,
                  @Param("role") String role);

  /**
   * Permanently removes the grant for one (venue, user) pair. A no-op when no such grant
   * exists.
   *
   * @param venueId the venue
   * @param userId  the grantee to revoke
   */
  void deleteByVenueIdAndUserId(@Param("venueId") UUID venueId, @Param("userId") UUID userId);

  /**
   * Permanently removes all grants on one venue. Called when the venue itself is deleted so
   * no orphan grant rows remain.
   *
   * @param venueId the venue whose grants to remove
   */
  void deleteByVenueId(@Param("venueId") UUID venueId);
}
