/*
 * Copyright 2018 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
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
package io.github.dotstart.helios.api.split;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.net.URI;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.UUID;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * <p>Represents a single segment within a run.</p>
 *
 * <p>Each respective segment may keep track of multiple times from different timers. The set of
 * times depends on the respective game integration which is used to manage the timer. Note,
 * however, that {@link io.github.dotstart.helios.api.time.Timer#REALTIME} is assumed to always be
 * present.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Segment {

  private final UUID id;

  private final StringProperty displayName = new SimpleStringProperty();
  private final ObservableMap<URI, Long> times = FXCollections.observableHashMap();
  private final ObservableMap<URI, Long> bestTimes = FXCollections.observableHashMap();
  private final ObservableList<Long> timeHistory = FXCollections.observableArrayList();

  private final ObservableMap<URI, Long> timesView = FXCollections
      .unmodifiableObservableMap(this.times);
  private final ObservableMap<URI, Long> bestTimesView = FXCollections
      .unmodifiableObservableMap(this.bestTimes);

  public Segment(@NonNull String displayName) {
    this.id = UUID.randomUUID();
    this.displayName.setValue(displayName);
  }

  /**
   * <p>Retrieves a globally unique identifier for this split.</p>
   *
   * <p>This value is provided for consistent referencing within extensions and third party
   * services.</p>
   *
   * <p>Note that due to the nature of UUIDs, these identifiers are theoretically unique (due to
   * the fact that they consist of 122 bits of randomness, they are unlikely to collide but still
   * have an incredibly small chance to do so).</p>
   *
   * @return a globally unique identifier.
   */
  @NonNull
  public UUID getId() {
    return this.id;
  }

  /**
   * <p>Retrieves a human readable name for this split.</p>
   *
   * <p>This value is used for logging and display purposes only (and may additionally be used by
   * third party services to display the splits).</p>
   *
   * @return a display name.
   */
  public String getDisplayName() {
    return this.displayName.get();
  }

  @NonNull
  public StringProperty displayNameProperty() {
    return this.displayName;
  }

  public void setDisplayName(@NonNull String displayName) {
    this.displayName.set(displayName);
  }

  /**
   * <p>Retrieves a map between timers (or rather their identifiers) and the respective registered
   * time.</p>
   *
   * <p>This method returns an immutable view of the actual collection. In order to alter the
   * contents, please use the {@link #clearTime()} and {@link #setTime(URI, long)} methods (or their
   * respective overloads).</p>
   *
   * @return a time map.
   */
  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableMap<URI, Long> getTimes() {
    return this.timesView;
  }

  /**
   * Clears all stored times from this segment.
   */
  public void clearTime() {
    this.times.clear();
  }

  /**
   * Clears a single time from this segment.
   *
   * @param timerId a timer identifier.
   */
  public void clearTime(@NonNull URI timerId) {
    this.times.remove(timerId);
  }

  /**
   * <p>Retrieves the current time for a given timer within this split.</p>
   *
   * <p>When no time has been registered for the given timerId, an empty optional will be returned
   * instead. Handling of this fact is up to the caller.</p>
   *
   * @param timerId a timer identifier.
   * @return a split duration or an empty optional.
   */
  @NonNull
  public OptionalLong getTime(@NonNull URI timerId) {
    var value = this.times.get(timerId);
    return value != null ? OptionalLong.of(value) : OptionalLong.empty();
  }

  /**
   * <p>Updates the duration of this segment for a specified timer.</p>
   *
   * <p>If a previous time exists, it will simply be overridden with the new time. If the new time
   * is lower than the current personal best, the personal best will also be updated along with this
   * method call.</p>
   *
   * @param timerId a timer identifier.
   * @param duration a split duration.
   */
  public void setTime(@NonNull URI timerId, long duration) {
    this.times.put(timerId, duration);

    var best = this.bestTimes.get(timerId);
    if (best == null || best > duration) {
      this.bestTimes.put(timerId, duration);
    }
  }

  /**
   * <p>Retrieves a map which associated timers (or rather their identifiers) with their respective
   * best segment times.</p>
   *
   * <p>This method returns an immutable view of the actual map. To modify them, please use the
   * {@link #clearBest()} and {@link #setBest(URI, long)} methods (or their respective
   * overloads).</p>
   *
   * @return a map of best times.
   */
  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableMap<URI, Long> getBestTimes() {
    return this.bestTimesView;
  }

  /**
   * Clears all best segment times.
   */
  public void clearBest() {
    this.bestTimes.clear();
  }

  /**
   * Clears a specific timer's best segment time.
   *
   * @param timerId a timer identifier.
   */
  public void clearBest(@NonNull URI timerId) {
    this.bestTimes.remove(timerId);
  }

  /**
   * <p>Retrieves the best possible segment time for the specified timer.</p>
   *
   * <p>When no best has been registered for the specified timer yet, an empty optional will be
   * returned instead. It is up to the caller to handle this case.</p>
   *
   * @param timerId a timer identifier.
   * @return a split duration or an empty optional.
   */
  @NonNull
  public OptionalLong getBest(@NonNull URI timerId) {
    var value = this.bestTimes.get(timerId);
    return value != null ? OptionalLong.of(value) : OptionalLong.empty();
  }

  /**
   * <p>Sets the best time for the given timer.</p>
   *
   * <p>If a previous time exists, it will simply be overridden with the new time.</p>
   *
   * @param timerId a timerId.
   * @param duration a segment duration.
   */
  public void setBest(@NonNull URI timerId, long duration) {
    this.bestTimes.put(timerId, duration);
  }

  /**
   * <p>Evaluates whether this split is considered a "gold" split (e.g. whether it is currently the
   * best possible known time for the represented segment).</p>
   *
   * <p>For the purposes of this definition, a split is considered a gold split when its known best
   * is equal to its current time.</p>
   *
   * @return true if gold, false otherwise.
   */
  public boolean isGold(@NonNull URI timerId) {
    var best = this.bestTimes.get(timerId);
    var current = this.times.get(timerId);

    return best != null && current != null && current <= best;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Segment)) {
      return false;
    }
    Segment segment = (Segment) o;
    return Objects.equals(this.id, segment.id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
