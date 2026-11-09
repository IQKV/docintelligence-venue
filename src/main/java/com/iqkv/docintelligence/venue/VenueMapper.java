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
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

/**
 * Tenant venue reads and writes against the {@code venues} table in a tenant schema.
 *
 * <p>Statements are unqualified by schema — the {@code MyBatisSchemaInterceptor} in each
 * consuming service sets {@code search_path} to {@code t_{tenantKey}, public} before every
 * statement, so {@code venues} resolves to the active tenant schema. This mapper must never
 * prefix the table with {@code public.} (that would read the master catalog instead).
 */
@Mapper
public interface VenueMapper {

  void insert(Venue venue);

  Optional<Venue> findById(UUID id);

  void update(Venue venue);

  void deleteById(UUID id);
}
