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
package io.github.dotstart.helios.ui.controller;

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.layout.TimerLayout;
import io.github.dotstart.helios.api.node.layout.SwitchLayout;
import io.github.dotstart.helios.api.time.TimeManager;
import io.github.dotstart.helios.api.time.Timer.State;
import io.github.dotstart.helios.ui.module.component.TimerComponent;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides a controller for the main timer window.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class MainWindow implements Initializable {

  private static final Logger logger = LogManager.getFormatterLogger(MainWindow.class);

  private final TimeManager timeManager;
  private final TimerComponent defaultComponent;
  private final ObjectProperty<TimerLayout> layout = new SimpleObjectProperty<>();

  @FXML
  private SwitchLayout componentPane;

  @Inject
  public MainWindow(@NonNull TimeManager timeManager, @NonNull TimerComponent defaultComponent) {
    this.timeManager = timeManager;
    this.defaultComponent = defaultComponent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.componentPane.prefWidthProperty().bind(Bindings.selectDouble(this.layout, "width"));
    this.componentPane.prefHeightProperty().bind(Bindings.selectDouble(this.layout, "height"));

    this.layout.addListener((observable, oldValue, newValue) -> {
      if (oldValue != null) {
        logger.debug("Removing bindings from previous timer layout");
        Bindings.unbindContent(this.componentPane.getNodes(), newValue.getNodes());
      }

      if (newValue == null) {
        logger.debug("Layout deleted - Constructing default layout");
        this.layout.set(this.createDefaultLayout());
        return;
      }

      logger.debug("Binding new timer layout (currently consisting of %d nodes)",
          newValue.getNodes().size());
      Bindings.bindContent(this.componentPane.getNodes(), newValue.getNodes());
    });

    // TODO: Load previously loaded layout
    this.layout.set(this.createDefaultLayout());

    // initialize the context menu
    var menu = this.createMenu();

    this.componentPane.setOnContextMenuRequested(
        event -> menu.show(this.componentPane, event.getScreenX(), event.getScreenY()));
    this.componentPane.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY) {
        menu.hide();
      }
    });
  }

  @NonNull
  private ContextMenu createMenu() {
    var menu = new ContextMenu();
    var state = Bindings.<State>select(this.timeManager.timerGroupProperty(), "state");

    var subMenu = new Menu("Timer");
    menu.getItems().add(subMenu);

    var item = new MenuItem("Start");
    item.setOnAction(event -> this.timeManager.getTimerGroup().start());
    item.disableProperty().bind(Bindings.createBooleanBinding(
        () -> state.get() != State.WAITING,
        state
    ));
    subMenu.getItems().add(item);

    item = new MenuItem();
    item.textProperty().bind(Bindings.createStringBinding(
        () -> state.get() == State.PAUSED ? "Unpause" : "Pause",
        state
    ));
    item.setOnAction(event -> this.timeManager.getTimerGroup().togglePause());
    item.disableProperty().bind(Bindings.createBooleanBinding(
        () -> state.get() == State.WAITING || state.get() == State.STOPPED,
        state
    ));
    subMenu.getItems().add(item);

    item = new MenuItem("Stop");
    item.setOnAction(event -> this.timeManager.getTimerGroup().stop());
    item.disableProperty().bind(Bindings.createBooleanBinding(
        () -> state.get() != State.RUNNING && state.get() != State.PAUSED,
        state
    ));
    subMenu.getItems().add(item);

    item = new MenuItem("Reset");
    item.setOnAction(event -> this.timeManager.reset());
    item.disableProperty().bind(Bindings.createBooleanBinding(
        () -> state.get() == State.WAITING,
        state
    ));
    subMenu.getItems().add(item);

    subMenu = new Menu("Splits");
    menu.getItems().add(subMenu);

    item = new MenuItem("Open File ...");
    item.setDisable(true); // TODO
    subMenu.getItems().add(item);

    item = new MenuItem("Save to File ...");
    item.setDisable(true); // TODO
    subMenu.getItems().add(item);

    item = new MenuItem("Close");
    item.setDisable(true); // TODO
    subMenu.getItems().add(item);

    subMenu = new Menu("Layout");
    menu.getItems().add(subMenu);

    item = new MenuItem("Edit");
    item.setDisable(true); // TODO
    subMenu.getItems().add(item);

    item = new MenuItem("Open File ...");
    item.setDisable(true); // TODO
    subMenu.getItems().add(item);

    item = new MenuItem("Save File ...");
    item.setDisable(true); // TODO
    subMenu.getItems().add(item);

    item = new MenuItem("Close");
    item.setDisable(true); // TODO
    subMenu.getItems().add(item);

    // dev option - only available if ScenicView is available
    try {
      var scenicClass = Class.forName("org.scenicview.ScenicView");
      var m = MethodHandles.lookup()
          .findStatic(scenicClass, "show", MethodType.methodType(void.class, Parent.class));

      item = new MenuItem("Show ScenicView");
      item.setOnAction(event -> {
        try {
          m.invoke(this.componentPane);
        } catch (Throwable ex) {
          logger.warn("failed to open ScenicView", ex);
        }
      });
      menu.getItems().add(item);

      logger.info("ScenicView is available within the Class-Path - dev options have been enabled");
    } catch (ClassNotFoundException ignore) {
    } catch (IllegalAccessException | NoSuchMethodException ex) {
      logger.warn(
          "incompatible ScenicView version: org.scenicview.ScenicView#show(Parent) is not defined");
    }

    item = new MenuItem("Quit");
    item.setOnAction(event -> ((Stage) this.componentPane.getScene().getWindow()).close());
    menu.getItems().add(item);

    return menu;
  }

  /**
   * <p>Creates a standardized timer layout.</p>
   *
   * <p>The standard layout is used in place of a user configuration when none is available or is
   * unloaded via the context menu.</p>
   *
   * @return a standard timer layout.
   */
  @NonNull
  private TimerLayout createDefaultLayout() {
    var layout = new TimerLayout();
    layout.addComponent(this.defaultComponent);
    return layout;
  }

  /**
   * Retrieves the currently loaded timer layout.
   *
   * @return a layout.
   */
  @NonNull
  public TimerLayout getLayout() {
    return this.layout.get();
  }

  /**
   * Replaces the currently loaded timer layout.
   *
   * @param layout a new layout.
   */
  public void setLayout(@NonNull TimerLayout layout) {
    this.layout.set(layout);
  }

  @NonNull
  public ObjectProperty<TimerLayout> layoutProperty() {
    return this.layout;
  }
}
