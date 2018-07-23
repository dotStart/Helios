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
package io.github.dotstart.helios.ui.module.component;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.node.StatelessComponent;
import io.github.dotstart.helios.api.time.TimeManager;
import io.github.dotstart.helios.ui.module.component.node.TimerComponentNode;

/**
 * <p>Provides a timer component.</p>
 *
 * <p>This component displays the overall time spent within a single run (either measured as total
 * time within the run or time spent in-game). The timing mode is specified by the respective timer
 * configuration (e.g. depending on whether the user is comparing against in-game or real
 * time).</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Singleton
public class TimerComponent implements StatelessComponent<TimerComponentNode> {

  private final TimeManager timeManager;

  @Inject
  public TimerComponent(@NonNull TimeManager timeManager) {
    this.timeManager = timeManager;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getName() {
    return "Timer";
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getDescription() {
    return "Displays the current overall run time";
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public TimerComponentNode createNode() {
    return new TimerComponentNode(this, this.timeManager);
  }
}
