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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * <p>Represents a single timer which may be started at an arbitrary time and (optionally) paused
 * at any time.</p>
 *
 * <p>Timers may enter three potential states:</p>
 *
 * <ul>
 * <li><strong>Waiting</strong> - The timer has been created but not started yet</li>
 * <li><strong>Running</strong> - The timer has been started and is currently counting</li>
 * <li><strong>Paused</strong> - The timer has been started but is currently paused</li>
 * <li><strong>Stopped</strong> - The timer has been completely stopped (e.g. the time has been
 * finalized)</li>
 * </ul>
 *
 * <p>Timers are not re-usable (e.g. once they are stopped, they can no longer be altered).</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Timer {

  private final ObjectProperty<State> state = new SimpleObjectProperty<>(State.WAITING);
  private long start;
  private long end;

  private long pauseStart;
  private long elapsedPauseTime;

  /**
   * <p>Starts measuring the passed time.</p>
   *
   * <p>Once this method is called, the timer will keep track of the time that has passed since
   * this method has been called. The actual resolution of the timer may differ between architecture
   * but is guaranteed to be accurate to the millisecond at minimum.</p>
   *
   * @throws IllegalStateException when the timer has previously been started.
   */
  public void start() {
    if (this.state.get() != State.WAITING) {
      throw new IllegalStateException("Cannot start timer: Already running");
    }

    this.start = System.nanoTime();
  }

  /**
   * Temporarily pauses the timer (e.g. stops measuring time from now on until the timer is
   * un-paused).
   *
   * @throws IllegalStateException when the timer is not running at the moment.
   */
  public void pause() {
    if (this.state.get() != State.RUNNING) {
      throw new IllegalStateException("Cannot pause timer: Not running");
    }

    this.pauseStart = System.nanoTime();
    this.state.set(State.PAUSED);
  }

  /**
   * Un-Pauses the timer.
   *
   * @throws IllegalStateException when the timer is not paused at the moment.
   */
  public void unpause() {
    if (this.state.get() != State.PAUSED) {
      throw new IllegalStateException("Cannot un-pause timer: Not paused");
    }

    var delta = System.nanoTime() - this.pauseStart;
    this.elapsedPauseTime += delta;
    this.state.set(State.RUNNING);
  }

  /**
   * <p>Toggles the pause state of this timer.</p>
   *
   * <p>This is a utility method which is designed for user interactions (such as hotkeys) where a
   * toggle is desired in favor of explicit pausing/un-pausing.</p>
   *
   * @throws IllegalStateException when the timer is not running at the moment.
   */
  public void togglePause() {
    if (this.isPaused()) {
      this.unpause();
    } else {
      this.pause();
    }
  }

  /**
   * <p>Permanently stops the timer.</p>
   *
   * <p>This method will also cause the timer to calculate the final elapsed time since its
   * start.</p>
   */
  public void stop() {
    if (this.state.get() != State.RUNNING && this.state.get() != State.PAUSED) {
      throw new IllegalStateException("Cannot stop timer: Not running");
    }

    if (this.isPaused()) {
      this.unpause();
    }

    this.end = System.nanoTime();
    this.state.set(State.STOPPED);
  }

  /**
   * Retrieves the total elapsed time (in nanoseconds) within this timer.
   *
   * @return an amount of elapsed nanos.
   */
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
   * Retrieves the total elapsed time (in nanoseconds) without regard for times at which this timer
   * has been paused.
   *
   * @return an amount of elapsed nanos.
   */
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
   * Retrieves the current
   */
  @NonNull
  public State getState() {
    return this.state.get();
  }

  @NonNull
  public ReadOnlyObjectProperty<State> stateProperty() {
    return this.state;
  }

  /**
   * Evaluates whether this timer has been created but not yet started.
   *
   * @return true if waiting.
   */
  public boolean isWaiting() {
    return this.state.get() == State.WAITING;
  }

  /**
   * Evaluates whether this timer is currently paused.
   *
   * @return true if paused, false otherwise.
   */
  public boolean isPaused() {
    return this.state.get() == State.PAUSED;
  }

  /**
   * Evaluates whether this timer is running (e.g. whether it is currently measuring time or is
   * paused).
   *
   * @return true if running.
   */
  public boolean isRunning() {
    return this.state.get() == State.RUNNING || this.state.get() == State.PAUSED;
  }

  /**
   * Evaluates whether this timer has been stopped.
   *
   * @return true if stopped.
   */
  public boolean isStopped() {
    return this.state.get() == State.STOPPED;
  }

  /**
   * Provides a list of valid timer states.
   */
  public enum State {
    WAITING,
    RUNNING,
    PAUSED,
    STOPPED
  }
}
