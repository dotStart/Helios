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
import io.github.dotstart.helios.ui.module.component.TimerComponent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides a controller for the main timer window.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class MainWindow implements Initializable {

  private static final Logger logger = LogManager.getFormatterLogger(MainWindow.class);

  private final TimerComponent defaultComponent;
  private final ObjectProperty<TimerLayout> layout = new SimpleObjectProperty<>();

  @FXML
  private SwitchLayout componentPane;

  @Inject
  public MainWindow(@NonNull TimerComponent defaultComponent) {
    this.defaultComponent = defaultComponent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
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
