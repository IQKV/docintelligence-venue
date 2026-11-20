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
 * Tenant venue smart-note reads and writes against the {@code venue_smart_notes} table in a
 * tenant schema ({@code t_{tenantKey}}).
 *
 * <p>Statements are unqualified by schema — the {@code MyBatisSchemaInterceptor} in each consuming
 * service sets {@code search_path} to {@code t_{tenantKey}, public} before every statement, so
 * {@code venue_smart_notes} resolves to the active tenant schema.
 * {@code created_at} / {@code updated_at} are maintained by the database.
 *
 * <p>The matching statements live in {@code mappers/venue/VenueSmartNoteMapper.xml}, which ships
 * inside this artifact; consumers pick it up through their existing {@code classpath*:mappers/}
 * scan.
 */
@Mapper
public interface VenueSmartNoteMapper {

  /**
   * Inserts a new row. The caller must set {@code id} on the entity before calling this method.
   *
   * @param note the row to insert
   */
  void insert(VenueSmartNote note);

  /**
   * Returns the row with the given id, or {@link Optional#empty()} when it does not exist.
   *
   * @param id the row id
   * @return the matching row, or empty
   */
  Optional<VenueSmartNote> findById(@Param("id") UUID id);

  /**
   * All smart notes attached to one venue, ordered by {@code sort_order} ascending then
   * {@code created_at} ascending (oldest first within the same order value). Never {@code null};
   * an unknown venue yields an empty list.
   *
   * @param venueId the owning venue
   * @return the venue's smart notes, sorted by sort order then creation time
   */
  List<VenueSmartNote> findByVenueId(@Param("venueId") UUID venueId);

  /**
   * Updates the {@code title} of one note without touching any other field.
   * Maintains {@code updated_at}. A no-op when no such row exists.
   *
   * @param id    the note to update
   * @param title the new title; may be {@code null} to clear it
   */
  void updateTitle(@Param("id") UUID id, @Param("title") String title);

  /**
   * Updates the {@code content} (HTML) of one note without touching any other field.
   * Maintains {@code updated_at}. A no-op when no such row exists.
   *
   * @param id      the note to update
   * @param content the new HTML content; may be {@code null} to clear it
   */
  void updateContent(@Param("id") UUID id, @Param("content") String content);

  /**
   * Updates the {@code color} of one note without touching any other field.
   * Maintains {@code updated_at}. A no-op when no such row exists.
   *
   * @param id    the note to update
   * @param color the new colour value; may be {@code null} to reset to the UI default
   */
  void updateColor(@Param("id") UUID id, @Param("color") String color);

  /**
   * Replaces the {@code tags} array of one note without touching any other field.
   * Maintains {@code updated_at}. A no-op when no such row exists.
   *
   * @param id   the note to update
   * @param tags the new tags; may be {@code null} or empty to clear all tags
   */
  void updateTags(@Param("id") UUID id, @Param("tags") List<String> tags);

  /**
   * Updates the {@code sort_order} of one note without touching any other field.
   * Maintains {@code updated_at}. A no-op when no such row exists.
   *
   * @param id        the note to update
   * @param sortOrder the new sort order value
   */
  void updateSortOrder(@Param("id") UUID id, @Param("sortOrder") int sortOrder);

  /**
   * Permanently deletes the note with the given id. A no-op when no such row exists.
   *
   * @param id the note to delete
   */
  void deleteById(@Param("id") UUID id);

  /**
   * Permanently deletes all notes attached to the given venue. Used to clean up when a venue is
   * deleted; the cascade is enforced at the application layer.
   *
   * @param venueId the venue whose notes to remove
   */
  void deleteByVenueId(@Param("venueId") UUID venueId);

  /**
   * Total number of smart notes attached to one venue. Returns {@code 0} when the venue has no
   * notes or does not exist.
   *
   * @param venueId the owning venue
   * @return smart-note count for the venue
   */
  long countByVenueId(@Param("venueId") UUID venueId);
}
