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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * <p>Represents a single timer which may be started at an arbitrary time and (optionally) paused
 * at any time.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class NanoTimer implements Timer {

  private final StringProperty displayName = new SimpleStringProperty();
  private final ObjectProperty<State> state = new SimpleObjectProperty<>(State.WAITING);
  private long start;
  private long end;

  private long pauseStart;
  private long elapsedPauseTime;

  public NanoTimer() {
  }

  public NanoTimer(@NonNull String displayName) {
    this.displayName.setValue(displayName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start() {
    this.start(System.nanoTime());
  }

  void start(long nanos) {
    if (this.state.get() != State.WAITING) {
      throw new IllegalStateException("Cannot start timer: Already running");
    }

    this.start = nanos;
    this.state.set(State.RUNNING);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void pause() {
    this.pause(System.nanoTime());
  }

  void pause(long nanos) {
    if (this.state.get() != State.RUNNING) {
      throw new IllegalStateException("Cannot pause timer: Not running");
    }

    this.pauseStart = nanos;
    this.state.set(State.PAUSED);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unpause() {
    this.unpause(System.nanoTime());
  }

  void unpause(long nanos) {
    if (this.state.get() != State.PAUSED) {
      throw new IllegalStateException("Cannot un-pause timer: Not paused");
    }

    var delta = nanos - this.pauseStart;
    this.elapsedPauseTime += delta;
    this.state.set(State.RUNNING);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void togglePause() {
    if (this.isPaused()) {
      this.unpause();
    } else {
      this.pause();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    this.stop(System.nanoTime());
  }

  void stop(long nanos) {
    if (this.state.get() != State.RUNNING && this.state.get() != State.PAUSED) {
      throw new IllegalStateException("Cannot stop timer: Not running");
    }

    if (this.isPaused()) {
      this.unpause();
    }

    this.end = nanos;
    this.state.set(State.STOPPED);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getElapsedNanos() {
    switch (this.state.get()) {
      case WAITING:
        return 0;
      case RUNNING:
        return System.nanoTime() - this.start - this.elapsedPauseTime;
      case PAUSED:
        return this.pauseStart - this.start - this.elapsedPauseTime;
      case STOPPED:
        return this.end - this.start - this.elapsedPauseTime;
    }

    throw new UnsupportedOperationException(); // ?!?!
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getTotalElapsedNanos() {
    if (this.state.get() == State.WAITING) {
      return 0;
    }

    var end = this.end;
    if (this.state.get() != State.STOPPED) {
      end = System.nanoTime();
    }

    return end - this.start;
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public String getDisplayName() {
    return this.displayName.get();
  }

  @NonNull
  public StringProperty displayNameProperty() {
    return this.displayName;
  }

  public void setDisplayName(@Nullable String displayName) {
    this.displayName.set(displayName);
  }

  /**
   * Retrieves the current timer state.
   */
  @NonNull
  public State getState() {
    return this.state.get();
  }

  @NonNull
  public ReadOnlyObjectProperty<State> stateProperty() {
    return this.state;
  }
}
