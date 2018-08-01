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
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.helios.api.layout.ComponentRegistry;
import io.github.dotstart.helios.api.layout.TimerLayout;
import io.github.dotstart.helios.api.node.ComponentNode;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

/**
 * Provides a window which permits the modification of the currently loaded layout.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LayoutWindow implements Initializable {

  private final ObjectProperty<TimerLayout> layout = new SimpleObjectProperty<>();

  private final ContextMenu contextMenu = new ContextMenu();

  @FXML
  private Button addButton;
  @FXML
  private ListView<ComponentNode<?, ?>> componentList;
  @FXML
  private StackPane settingsPane;

  @Inject
  public LayoutWindow(@NonNull ComponentRegistry componentRegistry,
      @NonNull MainWindow mainWindow) {
    this.layout.bind(mainWindow.layoutProperty());

    // TODO: categories
    componentRegistry.getComponents().forEach((c) -> {
      var item = new MenuItem(c.getName());
      item.setOnAction(event -> this.layout.get().addComponent(c));
      this.contextMenu.getItems().add(item);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.componentList.setCellFactory(this::createFactory);
    this.componentList.itemsProperty().bind(Bindings.select(this.layout, "nodes"));

    this.componentList.getSelectionModel().selectedItemProperty()
        .addListener(this::onChangeSelection);
  }

  @NonNull
  private ListCell<ComponentNode<?, ?>> createFactory(
      @NonNull ListView<ComponentNode<?, ?>> param) {
    return new ListCell<>() {
      @Override
      protected void updateItem(ComponentNode<?, ?> item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
          this.setText(null);
          return;
        }

        this.setText(item.definition().getName());
      }
    };
  }

  @FXML
  private void onAddComponent() {
    var pos = this.addButton.localToScreen(0, this.addButton.getHeight());
    this.contextMenu.show(this.addButton, pos.getX(), pos.getY());
  }

  @FXML
  private void onRemoveComponent() {
    var selection = this.componentList.getSelectionModel().getSelectedItems();
    selection.forEach((c) -> this.layout.get().removeComponent(c));
  }

  private void onChangeSelection(@NonNull ObservableValue<? extends ComponentNode<?, ?>> ob,
      @Nullable ComponentNode<?, ?> oldValue, @Nullable ComponentNode<?, ?> newValue) {
    if (newValue == null) {
      this.settingsPane.getChildren().clear();
      return;
    }

    var node = newValue.createConfigurationNode();
    if (node == null) {
      var label = new Label("This component does not offer any settings");
      label.setAlignment(Pos.CENTER);
      this.settingsPane.getChildren().setAll(label);
      return;
    }
    this.settingsPane.getChildren().setAll(node);
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
