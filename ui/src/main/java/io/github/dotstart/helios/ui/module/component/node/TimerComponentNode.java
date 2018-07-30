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
package io.github.dotstart.helios.ui.module.component.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.helios.api.node.Component;
import io.github.dotstart.helios.api.node.ComponentNode;
import io.github.dotstart.helios.api.node.StatelessComponentNode;
import io.github.dotstart.helios.api.time.TimeManager;
import io.github.dotstart.helios.api.time.Timer.FormatPrecision;
import io.github.dotstart.helios.api.time.Timer.State;
import io.github.dotstart.helios.ui.module.component.TimerComponent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Displays the current overall time of the selected timer.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class TimerComponentNode extends VBox implements
    StatelessComponentNode<TimerComponentNode> {

  public static final PseudoClass WAITING_CLASS = PseudoClass.getPseudoClass("waiting");
  public static final PseudoClass RUNNING_CLASS = PseudoClass.getPseudoClass("running");
  public static final PseudoClass PAUSED_CLASS = PseudoClass.getPseudoClass("paused");
  public static final PseudoClass STOPPED_CLASS = PseudoClass.getPseudoClass("stopped");

  public static final PseudoClass TIME_LOSS = PseudoClass.getPseudoClass("time-loss");
  public static final PseudoClass TIME_SAVE = PseudoClass.getPseudoClass("time-save");

  private final TimerComponent definition;
  private final TimeManager timeManager;

  private final ObservableValue<State> stateBinding;

  private final Label label = new Label();
  private final Timeline timeline = new Timeline(
      new KeyFrame(Duration.millis(16), this::refreshLabel)
  );

  public TimerComponentNode(@NonNull TimerComponent definition, @NonNull TimeManager timeManager) {
    this.definition = definition;
    this.timeManager = timeManager;

    this.stateBinding = Bindings.select(this.timeManager.timerGroupProperty(), "state");
    this.stateBinding.addListener(this::refreshState);
    this.refreshState(this.stateBinding, null,
        timeManager.getTimerGroup().getActiveTimer().getState());

    this.getChildren().add(this.label);

    this.timeline.setCycleCount(Timeline.INDEFINITE);
    this.timeline.play();

    ComponentNode.setGeneratedClassName(this);
  }

  /**
   * Refreshes the pseudoclass state (and thus the timer style).
   */
  private void refreshState(@NonNull ObservableValue<? extends State> observable,
      @Nullable State oldValue,
      @NonNull State newValue) {
    this.pseudoClassStateChanged(WAITING_CLASS, newValue == State.WAITING);
    this.pseudoClassStateChanged(RUNNING_CLASS, newValue == State.RUNNING);
    this.pseudoClassStateChanged(PAUSED_CLASS, newValue == State.PAUSED);
    this.pseudoClassStateChanged(STOPPED_CLASS, newValue == State.STOPPED);

    // TODO: Time loss & save
  }

  /**
   * Refreshes the label text.
   */
  private void refreshLabel(@NonNull ActionEvent event) {
    // TODO: Configurable format precision
    this.label.setText(this.timeManager.getTimerGroup().toString(FormatPrecision.MILLISECONDS));
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Component<TimerComponentNode, Void> definition() {
    return this.definition;
  }
}
