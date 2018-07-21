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
package io.github.dotstart.helios.api.time;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * <p>Provides a logical group of timers which may be started, paused, un-paused and stopped in
 * sync.</p>
 *
 * <p>Every timer within a group is identified by a globally unique URI which is used for the
 * purposes of serialization. While these URIs are globally unique, they are typically statically
 * defined within each respective game integration and will not change between runs. URIs are
 * expected to follow the format {@code helios+timer://&lt;package&gt;/&lt;timer&gt;}. For instance:
 * {@code helios+timer://org.example.timer/myTimer}</p>
 *
 * <p>Note that this implementation will perform timer actions at the exact same time for all of
 * its timers (e.g. timers will not show slightly different results due to the time that each call
 * takes).</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class TimerGroup implements Timer {

  private final StringProperty displayName = new SimpleStringProperty();

  private final ObservableMap<URI, NanoTimer> timers = FXCollections.observableHashMap();
  private final ObservableMap<URI, NanoTimer> timerView = FXCollections
      .unmodifiableObservableMap(this.timers);

  private final ObjectProperty<State> state = new SimpleObjectProperty<>(State.WAITING);
  private final NanoTimer realtimeTimer;
  private final ObjectProperty<NanoTimer> activeTimer = new SimpleObjectProperty<>();
  private final Set<NanoTimer> pausedTimers = new HashSet<>();

  public TimerGroup() {
    this.realtimeTimer = this.create(REALTIME, "Realtime");
    this.activeTimer.set(this.realtimeTimer);
  }

  public TimerGroup(@NonNull String displayName) {
    this();
    this.displayName.setValue(displayName);
  }

  /**
   * Creates a new timer within this group.
   *
   * @param id a globally unique timer identifier.
   * @param displayName a human readable name for the new timer.
   * @return a timer reference.
   */
  @NonNull
  public NanoTimer create(@NonNull URI id, @NonNull String displayName) {
    var timer = new NanoTimer(displayName);
    this.timers.put(id, timer);
    return timer;
  }

  /**
   * <p>Removes a timer from this group.</p>
   *
   * <p>Once removed, the timer will no longer be controlled by this group and will thus not be
   * started, paused, un-paused or stopped when the group state changes.</p>
   *
   * @param id a globally unique timer identifier.
   */
  public void remove(@NonNull URI id) {
    if (REALTIME.equals(id)) {
      throw new IllegalArgumentException("Cannot remove real time timer");
    }

    var timer = this.timers.get(id);
    if (timer == null) {
      throw new IllegalArgumentException("Cannot remove timer: not in group");
    }
    if (timer == this.activeTimer.get()) {
      this.activeTimer.set(this.realtimeTimer);
    }

    this.timers.remove(id);
    this.pausedTimers.remove(timer);
  }

  /**
   * Retrieves the complete timer map.
   *
   * @return a map of timers.
   */
  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableMap<URI, NanoTimer> getTimers() {
    return this.timerView;
  }

  /**
   * Switches the active timer.
   *
   * @param timer a timer.
   */
  public void setActiveTimer(@NonNull NanoTimer timer) {
    if (!this.timers.containsValue(timer)) {
      throw new IllegalArgumentException("Cannot set timer active: not in group");
    }

    this.activeTimer.set(timer);
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public String getDisplayName() {
    return this.displayName.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() {
    if (this.state.get() != State.WAITING) {
      throw new IllegalStateException("Cannot start timer: already running");
    }

    var time = System.nanoTime();
    this.timers.values().forEach((t) -> t.start(time));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void pause() {
    if (this.state.get() != State.RUNNING) {
      throw new IllegalStateException("Cannot pause timer: already paused");
    }

    var time = System.nanoTime();
    var running = this.timers.values().stream()
        .filter((t) -> t.getState() == State.RUNNING)
        .collect(Collectors.toSet());

    this.pausedTimers.clear();
    this.pausedTimers.addAll(running);

    running.forEach((t) -> t.pause(time));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unpause() {
    if (this.state.get() != State.PAUSED) {
      throw new IllegalStateException("Cannot un-pause timer: not paused");
    }

    var time = System.nanoTime();
    this.pausedTimers.forEach((t) -> t.unpause(time));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    if (this.state.get() != State.RUNNING && this.state.get() != State.PAUSED) {
      throw new IllegalStateException("Cannot stop timer: not running");
    }

    var time = System.nanoTime();
    this.timers.values().forEach((t) -> t.stop(time));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getElapsedNanos() {
    return this.activeTimer.get().getElapsedNanos();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getTotalElapsedNanos() {
    return this.activeTimer.get().getTotalElapsedNanos();
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public State getState() {
    return this.state.get();
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public ReadOnlyObjectProperty<State> stateProperty() {
    return this.state;
  }
}
