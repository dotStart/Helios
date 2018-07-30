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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.helios.api.node.StatelessComponent;
import io.github.dotstart.helios.api.theme.variable.VariableDefinition;
import io.github.dotstart.helios.api.theme.variable.color.ColorVariableDefinition;
import io.github.dotstart.helios.api.theme.variable.color.LinearGradient;
import io.github.dotstart.helios.api.theme.variable.color.LinearGradient.Stop;
import io.github.dotstart.helios.api.theme.variable.color.SolidColor;
import io.github.dotstart.helios.api.time.TimeManager;
import io.github.dotstart.helios.ui.module.component.node.TimerComponentNode;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

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

  private static final Set<VariableDefinition> variables = Set.of(
      new ColorVariableDefinition(
          "helios+component://io.github.dotstart.helios.ui.module.component/TimerComponent/waitingColor",
          "Standby Color", "Specifies the standby timer text color",
          new LinearGradient(
              LinearGradient.Direction.TOP_TO_BOTTOM,
              new Stop(SolidColor.WHITE, 0),
              new Stop(new SolidColor(128, 128, 128), 1)
          )
      ),
      new ColorVariableDefinition(
          "helios+component://io.github.dotstart.helios.ui.module.component/TimerComponent/runningColor",
          "Running Color",
          "Specifies the standard running timer text color (when no prior time is known or comparison is disabled)",
          new LinearGradient(
              LinearGradient.Direction.TOP_TO_BOTTOM,
              new Stop(SolidColor.WHITE, 0),
              new Stop(new SolidColor(128, 128, 128), 1)
          )
      ),
      new ColorVariableDefinition(
          "helios+component://io.github.dotstart.helios.ui.module.component/TimerComponent/pausedColor",
          "Paused Color", "Specifies the standard paused timer text color",
          new LinearGradient(
              LinearGradient.Direction.TOP_TO_BOTTOM,
              new Stop(new SolidColor(128, 128, 128), 0),
              new Stop(new SolidColor(180, 180, 180), 1)
          )
      )
  );

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

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public InputStream getStyleResource() {
    return this.getClass().getResourceAsStream("/stylesheet/TimerComponent.scss");
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Set<VariableDefinition> getStyleVariables() {
    return Collections.unmodifiableSet(variables);
  }
}
