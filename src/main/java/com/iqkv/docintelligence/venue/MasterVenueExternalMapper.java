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

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Master venue external identity reads and writes against {@code public.master_venue_external}.
 *
 * <p>This is one of the few mappers allowed to name the {@code public} schema explicitly —
 * external identities are platform-owned reference data, never tenant-scoped.
 *
 * <p>{@code (provider, provider_external_id)} is the natural key of the table
 * ({@code uq_master_venue_external_identity}), so this mapper has no bare {@code insert} /
 * {@code update} pair: {@link #upsert(MasterVenueExternal)} records and refreshes one provider
 * observation in a single statement.
 *
 * <p>The matching statements live in {@code mappers/venue/MasterVenueExternalMapper.xml}, which
 * ships inside this artifact; consumers pick it up through their existing
 * {@code classpath*:mappers/} scan. Write ownership of the table belongs to
 * {@code docintelligence-master-venue-worker} — a consumer that only reads must not call
 * {@link #upsert(MasterVenueExternal)}.
 */
@Mapper
public interface MasterVenueExternalMapper {

  /**
   * Records the provider observation, or refreshes it when the identity is already known.
   *
   * <p>{@code master_venue_id} is taken only on the first insert: an identity that already
   * resolves to a master venue keeps resolving there, so a re-delivered or out-of-order scrape
   * can never re-point it. {@code raw_payload}, {@code confidence} and {@code crawled_at} are
   * refreshed on every call.
   *
   * @param external the identity to record; {@code provider} and {@code providerExternalId} are
   *                 the conflict target, {@code masterVenueId} the venue to bind it to
   */
  void upsert(MasterVenueExternal external);

  /**
   * Resolves a provider identity to its master venue mapping.
   *
   * @param provider           the provider the identifier belongs to, e.g. {@code tagvenue}
   * @param providerExternalId the provider's own venue identifier
   * @return the mapping, or empty when this identity has never been ingested
   */
  Optional<MasterVenueExternal> findByProviderExternalId(@Param("provider") String provider,
                                                         @Param("providerExternalId")
                                                         String providerExternalId);
}
