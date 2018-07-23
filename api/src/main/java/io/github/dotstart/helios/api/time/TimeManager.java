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

import com.google.inject.Singleton;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.time.split.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Provides a management component which handles the current timeline and timer state.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Singleton
public class TimeManager {

  private final ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>(new Timeline());
  private final ObjectProperty<TimerGroup> timerGroup = new SimpleObjectProperty<>(
      new TimerGroup());

  /**
   * Resets the timer and timeline back to their initial state.
   */
  public void reset() {
    this.timeline.get().clear();
    this.timerGroup.set(new TimerGroup());

    // TODO: Re-Register game integration
  }

  /**
   * Retrieves the currently selected timeline.
   *
   * @return a timeline.
   */
  @NonNull
  public Timeline getTimeline() {
    return this.timeline.get();
  }

  @NonNull
  public ReadOnlyObjectProperty<Timeline> timelineProperty() {
    return this.timeline;
  }

  /**
   * <p>Selects a new timeline.</p>
   *
   * <p>The timer state will automatically reset if a different timeline than the currently active
   * one is passed through this method.</p>
   *
   * @param timeline a timeline.
   */
  public void setTimeline(@NonNull Timeline timeline) {
    var old = this.timeline.get();
    this.timeline.set(timeline);

    if (timeline != old) {
      this.reset();
    }
  }

  /**
   * Retrieves the current timer group which tracks the progress of the current run.
   *
   * @return a timer group.
   */
  @NonNull
  public TimerGroup getTimerGroup() {
    return this.timerGroup.get();
  }

  @NonNull
  public ReadOnlyObjectProperty<TimerGroup> timerGroupProperty() {
    return this.timerGroup;
  }
}
