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
package io.github.dotstart.helios.api.time.split;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.time.TimerGroup;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * <p>Represents a pre-configured set of splits, their respective display names and various
 * times.</p>
 *
 * <p>The available times within each respective split depend on the game integration used while
 * running and will thus differ from game to game or even installation to installation. Note,
 * however that splits are guaranteed to contain times for the {@link
 * io.github.dotstart.helios.api.time.Timer#REALTIME} category at all times.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Timeline {

  private short segmentIndex = -1;
  private final ObservableList<Segment> segments = FXCollections.observableArrayList();

  private final ObservableList<Segment> segmentsView = FXCollections
      .unmodifiableObservableList(this.segments);

  public Timeline() {
    this.add("");
  }

  /**
   * Creates a new segment with the given display name.
   *
   * @param displayName a display name.
   * @return a segment.
   */
  @NonNull
  public Segment add(@NonNull String displayName) {
    var segment = new Segment(displayName);
    this.segments.add(segment);
    return segment;
  }

  /**
   * Removes a segment from this timeline.
   *
   * @param id a segment identifier.
   */
  public void remove(@NonNull UUID id) {
    this.segments.removeIf((s) -> s.getId().equals(id));
  }

  /**
   * Resets the entire timeline back to its original state.
   */
  public void clear() {
    this.segments.forEach(Segment::clearTime);
    this.segmentIndex = -1;
  }

  /**
   * Resets the entire set of known best times within this timeline.
   */
  public void clearBest() {
    this.segments.forEach(Segment::clearBest);
  }

  /**
   * Records all current times and moves the timeline to the next segment within the queue (given
   * that there is any left).
   *
   * @param group a timer group to retrieve the segment times from.
   */
  public void split(@NonNull TimerGroup group) {
    if (!this.hasStarted() || this.hasFinished()) {
      throw new IllegalStateException("Cannot split: Timeline is not running");
    }

    var active = this.segments.get(this.segmentIndex);
    group.getTimers().forEach((id, t) -> active.setTime(id, t.getElapsedNanos()));

    ++this.segmentIndex;
  }

  /**
   * <p>Retrieves an observable listing of all segments within this timeline.</p>
   *
   * <p>This method returns an immutable view of the actual segment list. In order to create new
   * splits, use the {@link #add(String)} and {@link #remove(UUID)} methods and their respective
   * overloads.</p>
   *
   * @return a list of segments.
   */
  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableList<Segment> getSegments() {
    return this.segmentsView;
  }

  /**
   * <p>Retrieves the currently active segment (e.g. the segment for which no time has been taken
   * yet).</p>
   *
   * <p>When no segment is active (e.g. when the timeline has been finished), an empty optional
   * will be returned instead.</p>
   *
   * @return a segment.
   */
  @NonNull
  public Optional<Segment> getActiveSegment() {
    if (this.segmentIndex < 0 || this.segmentIndex >= this.segments.size()) {
      return Optional.empty();
    }

    return Optional.ofNullable(this.segments.get(this.segmentIndex));
  }

  /**
   * Evaluates whether this timeline has been started.
   *
   * @return true if started, false otherwise.
   */
  public boolean hasStarted() {
    return this.segmentIndex != -1;
  }

  /**
   * Evaluates whether this timeline has been finished (e.g. whether the last segment has been
   * reached and completed).
   *
   * @return true if finished, false otherwise.
   */
  public boolean hasFinished() {
    return this.segmentIndex >= this.segments.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Timeline)) {
      return false;
    }
    Timeline timeline = (Timeline) o;
    return Objects.equals(this.segments, timeline.segments);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.segments);
  }
}
