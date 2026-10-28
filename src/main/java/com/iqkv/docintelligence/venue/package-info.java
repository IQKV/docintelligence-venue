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

/**
 * Shared venue domain model and persistence for the DocIntelligence suite.
 *
 * <p>One flat package for the whole venue bounded context: platform-owned master catalog types
 * and tenant-scoped types live side by side, and the context is carried by the type name rather
 * than by a sub-package. {@code MasterVenue} is a row of the platform-level
 * {@code public.master_venue} reference dataset; the tenant-scoped types ({@code Venue},
 * {@code VenueAsset}, {@code VenueMetadata}) read and write a tenant schema
 * ({@code t_{tenantKey}}). Today only the master catalog slice is implemented.
 *
 * <h2>Main Components</h2>
 * <ul>
 *   <li>{@code MasterVenue} — one row of {@code public.master_venue}</li>
 *   <li>{@code MasterVenueMapper} and {@code mappers/venue/MasterVenueMapper.xml} — MyBatis reads
 *       and writes for the master catalog tables</li>
 *   <li>{@code MasterVenueAsset} — one asset of a master venue, a row of
 *       {@code public.master_venue_asset}</li>
 *   <li>{@code MasterVenueAssetMapper} and {@code mappers/venue/MasterVenueAssetMapper.xml} —
 *       MyBatis reads and writes for the master venue assets, including the upsert keyed on the
 *       venue and file name that a re-ingested record writes through</li>
 *   <li>{@code MasterVenueExternal} — one provider identity of a master venue, a row of
 *       {@code public.master_venue_external}</li>
 *   <li>{@code MasterVenueExternalMapper} and
 *       {@code mappers/venue/MasterVenueExternalMapper.xml} — MyBatis reads and writes for the
 *       external venue identities, including the upsert that
 *       {@code MasterVenueMapper#upsertWithExternalIdentity} builds on</li>
 * </ul>
 *
 * <h2>What Stays Out</h2>
 * <p>No Spring beans, no REST resources and no business services. The HTTP surface, the DTO
 * payload shapes and the Liquibase changelogs stay in {@code docintelligence-catalog-service},
 * which owns the DDL for {@code public.master_venue}, {@code public.master_venue_external} and
 * {@code public.master_venue_asset}.
 * Write ownership of
 * {@code public.master_venue}, {@code public.master_venue_alias},
 * {@code public.master_venue_external} and {@code public.master_venue_asset} belongs to
 * {@code docintelligence-master-venue-worker}.
 *
 * <h2>Usage</h2>
 * <p>A plain library for {@code docintelligence-catalog-service},
 * {@code docintelligence-catalog-processing-worker} and
 * {@code docintelligence-master-venue-worker}. MyBatis is an optional dependency — every consumer
 * already provides it — and the mapper XML ships in this jar, where the consumers'
 * existing {@code classpath*:mappers/} scan picks it up without further configuration.
 */
package com.iqkv.docintelligence.venue;
