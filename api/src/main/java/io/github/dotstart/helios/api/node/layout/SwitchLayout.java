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
package io.github.dotstart.helios.api.node.layout;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.node.Component;
import io.github.dotstart.helios.api.node.Component.Direction;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>Provides a layout component which permits switching between a horizontal and vertical
 * layout.</p>
 *
 * <p>Note that this implementation might not be particular optimal (I am personally unsure whether
 * JavaFX permits reflowing of the entire layout in the desired fashion), however we accept
 * performance hits here due to the rare switching between layout styles.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@DefaultProperty("nodes")
public class SwitchLayout extends StackPane {

  private final Logger logger = LogManager.getFormatterLogger(SwitchLayout.class);

  private final ObservableValue<Direction> direction = Component.createDirectionObservable(this);
  private final BooleanProperty inverted = new SimpleBooleanProperty();
  private final ObservableList<Node> node = FXCollections.observableArrayList();

  private Pane contentPane;

  public SwitchLayout() {
    this.rebuild(Direction.HORIZONTAL);

    this.direction.addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        newValue = Direction.HORIZONTAL;
      }

      this.rebuild(newValue);
    });
    this.inverted
        .addListener((observable, oldValue, newValue) -> this.rebuild(this.direction.getValue()));
    Component.registerRecursiveNodeUpdater(this.direction, this.node);
  }

  /**
   * Rebuilds the component layout.
   *
   * @param direction a direction.
   */
  private void rebuild(@NonNull Direction direction) {
    this.logger.debug("Rebuilding layout consisting of %d nodes", this.node.size());

    getChildren().clear();
    if (this.contentPane != null) {
      Bindings.unbindContent(this.contentPane.getChildren(), this.node);
    }

    if (this.inverted.get()) {
      direction = direction.invert();
    }

    switch (direction) {
      case HORIZONTAL:
        this.contentPane = new HBox();
        break;
      case VERTICAL:
        this.contentPane = new VBox();
        break;
    }

    Bindings.bindContent(this.contentPane.getChildren(), this.node);
    this.getChildren().add(this.contentPane);
  }

  /**
   * Retrieves the direction in which this layout is currently displayed.
   *
   * @return an arbitrary direction.
   */
  @NonNull
  public Direction getDirection() {
    return this.direction.getValue();
  }

  /**
   * Forcefully updates the direction of this layout component.
   *
   * @param direction a direction.
   */
  public void setDirection(@NonNull Direction direction) {
    Component.setNodeDirection(this, direction);
  }

  public boolean isInverted() {
    return this.inverted.get();
  }

  @NonNull
  public BooleanProperty invertedProperty() {
    return this.inverted;
  }

  public void setInverted(boolean inverted) {
    this.inverted.set(inverted);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableList<Node> getNodes() {
    return this.node;
  }
}
