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
package io.github.dotstart.helios.api.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.Component;
import javafx.css.Styleable;
import javafx.scene.Node;

/**
 * <p>Represents a component node which is part of the JavaFX timer layout.</p>
 *
 * <p>Component nodes are instantiated via their parent {@link Component} implementation which
 * defines their metadata and provides consistent ways of instantiating them with a fresh
 * configuration or from a previously stored state.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface ComponentNode<N extends Node & ComponentNode<N, C>, C> extends Styleable {

  /**
   * <p>Retrieves a reference to the definition from which this component has been initialized.</p>
   *
   * <p>This method is mainly used in order to retrieve component metadata (such as names and
   * descriptions) from the parent object.</p>
   *
   * @return a component definition.
   */
  @NonNull
  Component<N, C> definition();

  /**
   * <p>Retrieves a copy of the respective configuration object.</p>
   *
   * <p>When no configuration is desired, this method will remain unimplemented. {@link Void} will
   * be used as a placeholder type in these cases.</p>
   *
   * <p>This method will only be called when the layout is written back to disk (in order to
   * serialize the component configuration in its current state). As such, this method may
   * optionally construct a transport entity (e.g. a type which has been specifically designed to
   * store all necessary information in a serializable format).</p>
   *
   * @return a configuration object.
   * @throws UnsupportedOperationException when no configuration is desired.
   */
  @NonNull
  C configuration();
}
