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

/**
 * Builds the text fed to the embedding model from a venue's searchable fields — the name and
 * address sentences followed by the raw JSONB metadata document.
 *
 * <p>This is the single canonical projection of {@link Venue} and {@link MasterVenue} into
 * embedder input. Keeping it here means every producer — the catalog service refreshing a venue
 * on write, the catalog processing worker's backfill and the master venue worker's ingest — feeds
 * the model identical text, so the same venue can never end up with two different vectors
 * depending on which path wrote it.
 *
 * <p>Pure and stateless: no Spring beans, no services, no persistence wiring.
 */
public final class VenueEmbeddingText {

  private VenueEmbeddingText() {
  }

  /**
   * Builds the embedding text of a tenant venue.
   *
   * @param venue the venue to project; individual fields may be {@code null}
   * @return the name, address and city sentences followed by the metadata document
   */
  public static String build(final Venue venue) {
    return build(venue.getName(), venue.getAddress(), venue.getCity(), venue.getMetadata());
  }

  /**
   * Builds the embedding text of a master venue.
   *
   * @param venue the venue to project; individual fields may be {@code null}
   * @return the name, address and city sentences followed by the metadata document
   */
  public static String build(final MasterVenue venue) {
    return build(venue.getName(), venue.getAddress(), venue.getCity(), venue.getMetadata());
  }

  private static String build(final String name, final String address, final String city,
                              final String metadata) {
    final var sb = new StringBuilder();
    if (name != null) {
      sb.append(name).append(". ");
    }
    if (address != null) {
      sb.append(address).append(". ");
    }
    if (city != null) {
      sb.append(city).append(". ");
    }
    if (metadata != null && !metadata.isBlank()) {
      sb.append(metadata);
    }
    return sb.toString().strip();
  }
}
