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

import com.google.inject.Singleton;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.node.Component;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Provides a dynamic registry which maps components to their respective globally unique
 * identifiers.</p>
 *
 * <p>This registry also provides methods to retrieve the complete list of available components for
 * the purposes of listing components within the configuration panes.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Singleton
public class ComponentRegistry {

  private final Map<URI, Component<?, ?>> registry = new HashMap<>();

  /**
   * Registers a new component with this registry.
   *
   * @param component a component implementation.
   */
  public void register(@NonNull Component<?, ?> component) {
    if (this.registry.containsKey(component.getURI())) {
      throw new IllegalArgumentException(
          "Component with identifier \"" + component.getURI() + "\" has already been registered");
    }

    component.logComponentDetails();
    this.registry.put(component.getURI(), component);
  }

  /**
   * <p>Retrieves a component based on its globally unique identifier.</p>
   *
   * <p>When no component with the specified identifier is registered, an empty optional will be
   * returned instead (e.g. error handling is up to the caller).</p>
   *
   * @param uri a globally unique component identifier.
   * @return a component or an empty optional.
   */
  @NonNull
  public Optional<Component<?, ?>> getComponent(@NonNull URI uri) {
    return Optional.ofNullable(this.registry.get(uri));
  }

  /**
   * <p>Retrieves the complete list of registered components within this registry.</p>
   *
   * <p>Note that the order in which this components are returned is not guaranteed (e.g. order may
   * differ between calls or application restarts). The returned collection is an immutable view of
   * the actual registry.</p>
   *
   * @return a collection of registered components.
   */
  @NonNull
  public Collection<Component<?, ?>> getComponents() {
    return Collections.unmodifiableCollection(this.registry.values());
  }
}
