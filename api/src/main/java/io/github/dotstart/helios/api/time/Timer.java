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
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * <p>Represents a timer which keeps track of the total amount of time passed since a certain
 * origin (epoch).</p>
 *
 * <p>Time will be tracked from the start of the timer (as indicated by {@link #start()}) until
 * they are stopped (as indicated by {@link #stop()}). Additionally they may be temporarily paused
 * via the {@link #pause()} and {@link #unpause()} methods. Any time spent in the paused state will
 * be subtracted from the total.</p>
 *
 * <p>Each timer may enter any of the following states:</p>
 *
 * <ul>
 * <li><strong>Waiting</strong> - The timer has been created but not started yet</li>
 * <li><strong>Running</strong> - The timer has been started and is currently counting</li>
 * <li><strong>Paused</strong> - The timer has been started but is currently paused</li>
 * <li><strong>Stopped</strong> - The timer has been completely stopped (e.g. the time has been
 * finalized)</li>
 * </ul>
 *
 * <p>By default timers will be initialized in the {@code waiting} state.</p>
 *
 * <p>Timers are not re-usable (e.g. once stopped, a timer cannot be reset and a new timer must be
 * created in its place instead).</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Timer {

  /**
   * Defines the URI of the real time timer.
   */
  URI REALTIME = URI.create("helios+timer://io.github.dotstart.helios/realtime");

  /**
   * Defines the URI for the in-game timer.
   */
  URI IN_GAME = URI.create("helios+timer://io.github.dotstart.helios/ingame");

  /**
   * <p>Retrieves a human readable name for this timer.</p>
   *
   * <p>This value will be displayed within the respective "Compare Against" and similar menus and
   * additionally persist to the splits file (in order to identify the timer even on systems where
   * its defining module is not loaded).</p>
   *
   * @return a display name.
   */
  @Nullable
  String getDisplayName();

  /**
   * <p>Starts measuring the passed time.</p>
   *
   * <p>Once this method is called, the timer will keep track of the time that has passed since
   * this method has been called. The actual resolution of the timer may differ between architecture
   * but is guaranteed to be accurate to the millisecond at minimum.</p>
   *
   * @throws IllegalStateException when the timer has previously been started.
   */
  void start();

  /**
   * Temporarily pauses the timer (e.g. stops measuring time from now on until the timer is
   * un-paused).
   *
   * @throws IllegalStateException when the timer is not running at the moment.
   */
  void pause();

  /**
   * Un-Pauses the timer.
   *
   * @throws IllegalStateException when the timer is not paused at the moment.
   */
  void unpause();

  /**
   * <p>Toggles the pause state of this timer.</p>
   *
   * <p>This is a utility method which is designed for user interactions (such as hotkeys) where a
   * toggle is desired in favor of explicit pausing/un-pausing.</p>
   *
   * @throws IllegalStateException when the timer is not running at the moment.
   */
  default void togglePause() {
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
  void stop();

  /**
   * Retrieves the total elapsed time (in nanoseconds) within this timer.
   *
   * @return an amount of elapsed nanos.
   */
  long getElapsedNanos();

  /**
   * Retrieves the total elapsed time (in nanoseconds) without regard for times at which this timer
   * has been paused.
   *
   * @return an amount of elapsed nanos.
   */
  long getTotalElapsedNanos();

  /**
   * Retrieves the current timer state.
   */
  @NonNull
  State getState();

  @NonNull
  ReadOnlyObjectProperty<State> stateProperty();

  /**
   * Evaluates whether this timer has been created but not yet started.
   *
   * @return true if waiting.
   */
  default boolean isWaiting() {
    return this.getState() == State.WAITING;
  }

  /**
   * Evaluates whether this timer is currently paused.
   *
   * @return true if paused, false otherwise.
   */
  default boolean isPaused() {
    return this.getState() == State.PAUSED;
  }

  /**
   * Evaluates whether this timer is running (e.g. whether it is currently measuring time or is
   * paused).
   *
   * @return true if running.
   */
  default boolean isRunning() {
    return this.getState() == State.RUNNING || this.getState() == State.PAUSED;
  }

  /**
   * Evaluates whether this timer has been stopped.
   *
   * @return true if stopped.
   */
  default boolean isStopped() {
    return this.getState() == State.STOPPED;
  }

  /**
   * <p>Converts the elapsed time into a human readable format.</p>
   *
   * <p>The respective passed precision value defines the smallest unit displayed within the timer.
   * For instance: A precision of {@link FormatPrecision#SECONDS} will result in a time formatted as
   * {@code HH:mm:ss} while {@link FormatPrecision#MILLISECONDS} would result in {@code
   * HH:mm:ss:SSS}.</p>
   *
   * @return a human readable version of the elapsed time.
   */
  @NonNull
  default String toString(@NonNull FormatPrecision precision) {
    var out = "";
    var val = this.getElapsedNanos();

    long nanos = val % 1000000;
    val /= 1000000;
    long millis = val % 1000;
    val /= 1000;
    long seconds = val % 60;
    val /= 60;
    long minutes = val % 60;
    val /= 60;

    if (val > 0) {
      out += val;
      out += ":";
    }
    out += String.format("%02d", minutes);
    out += ":";
    out += String.format("%02d", seconds);

    if (precision == FormatPrecision.SECONDS) {
      return out;
    }

    if (precision == FormatPrecision.MILLISECONDS) {
      out += ".";
      out += String.format("%03d", millis);
      return out;
    }

    out += ".";
    out += String.format("%03d", millis);
    out += String.format("%06d", nanos);
    return out;
  }

  enum State {
    WAITING,
    RUNNING,
    PAUSED,
    STOPPED
  }

  enum FormatPrecision {
    SECONDS,
    MILLISECONDS,
    NANOSECONDS
  }
}
