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
package io.github.dotstart.helios.ui.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.HeliosModule;
import io.github.dotstart.helios.api.layout.ComponentRegistry;
import io.github.dotstart.helios.ui.module.component.TimerComponent;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <p>Provides a Helios module for the purposes of registering all core components with their
 * respective registries within the application.</p>
 *
 * <p>This is technically a "virtual" module as it cannot be uninstalled and is part of the
 * application core itself.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
// TODO: Move into separate module?
public class HeliosCoreModule implements HeliosModule {

  private final Injector injector;

  @Inject
  public HeliosCoreModule(@NonNull Injector injector) {
    this.injector = injector;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getName() {
    return "Helios Core";
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getVersion() {
    var p = this.getClass().getPackage();
    return Optional.ofNullable(p.getImplementationVersion()).orElse("0.0.0+dev");
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getDescription() {
    return "Provides various built-in components";
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Set<Category> getCategories() {
    return EnumSet.of(Category.COMPONENT);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public List<String> getDevelopers() {
    return List.of("Johannes \".start\" Donath"); // TODO: Method to pull this from maven?
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize() {
    this.injector.getInstance(ComponentRegistry.class)
        .register(this.injector.getInstance(TimerComponent.class));
  }
}
