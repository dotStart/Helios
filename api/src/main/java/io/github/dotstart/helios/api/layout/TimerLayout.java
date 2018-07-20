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
package io.github.dotstart.helios.api.layout;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.Component;
import io.github.dotstart.helios.api.node.ComponentNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * <p>Represents an active timer layout (e.g. a listing of selected components and their respective
 * configurations).</p>
 *
 * <p>(De-)serialization is provided in addition to the component management capabilities of this
 * implementation in order to permanently store layouts on disk.</p>
 *
 * <p>Layout instances are expected to represent a single known set of components and will thus be
 * created from scratch when a new layout is created or </p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class TimerLayout {

  private final ObservableList<Node> nodes = FXCollections.observableArrayList();
  private final ObservableList<Node> nodeView = FXCollections
      .unmodifiableObservableList(this.nodes);

  /**
   * Appends a new component at the end of the layout.
   *
   * @param component a component specification.
   */
  @SuppressWarnings("unchecked")
  public void addComponent(@NonNull Component<?, ?> component) {
    this.nodes.add(component.createNode());
  }

  /**
   * <p>Adds a new component to the layout at the specified location.</p>
   *
   * <p>If the specified index is already occupied, the component at its position and all
   * subsequent nodes will be pushed backwards by one index.</p>
   *
   * @param index an absolute index to place the resulting node at
   * @param component a component specification.
   */
  @SuppressWarnings("unchecked")
  public void addComponent(int index, @NonNull Component<?, ?> component) {
    this.nodes.add(index, component.createNode());
  }

  /**
   * Removes a single component node from the timer layout.
   *
   * @param node a node.
   */
  @SuppressWarnings("SuspiciousMethodCalls")
  public void removeComponent(@NonNull ComponentNode<?, ?> node) {
    this.nodes.remove(node);
  }

  /**
   * <p>Retrieves a list of nodes which have been initialized within this timer layout.</p>
   *
   * <p>The returned list is merely an observable view of the actual node list and prevents direct
   * modification of the component node list. Please use the respective component based methods
   * instead when adding or removing nodes.</p>
   *
   * @return an observable list of initialized component nodes.
   */
  @NonNull
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public ObservableList<Node> getNodes() {
    return this.nodeView;
  }
}
