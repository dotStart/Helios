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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.layout.TimerLayout;
import io.github.dotstart.helios.api.node.layout.SwitchLayout;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * Provides a controller for the main timer window.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class MainWindow implements Initializable {

  private final ObjectProperty<TimerLayout> layout = new SimpleObjectProperty<>();

  @FXML
  private SwitchLayout components;

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.layout.addListener((observable, oldValue, newValue) -> {
      if (oldValue != null) {
        Bindings.unbindContent(this.components.getChildren(), newValue.getNodes());
      }

      if (newValue == null) {
        this.layout.set(new TimerLayout());
        return;
      }

      Bindings.bindContent(this.components.getChildren(), newValue.getNodes());
    });

    this.layout.set(new TimerLayout());
  }

  @NonNull
  public TimerLayout getLayout() {
    return this.layout.get();
  }

  public void setLayout(@NonNull TimerLayout layout) {
    this.layout.set(layout);
  }

  @NonNull
  public ObjectProperty<TimerLayout> layoutProperty() {
    return this.layout;
  }
}
