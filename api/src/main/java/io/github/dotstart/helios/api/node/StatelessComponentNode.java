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
import javafx.scene.Node;

/**
 * Provides a utility extension to the component node specification which provides a base to
 * components which do not wish to provide any configuration parameters.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface StatelessComponentNode<N extends Node & StatelessComponentNode<N>> extends
    ComponentNode<N, Void> {

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  default Void configuration() {
    throw new UnsupportedOperationException("Component does not provide configuration parameters");
  }
}
