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
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.HeliosModule;
import io.github.dotstart.helios.api.HeliosModuleMetadata;
import io.github.dotstart.helios.ui.theme.ThemeManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides a manager implementation which keeps records of the list of loaded modules and their
 * respective metadata, initializes modules and bootstraps their respective component registration.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Singleton
public class ModuleManager {

  private static final Logger logger = LogManager.getFormatterLogger(ModuleManager.class);

  private final Injector injector;
  private final Injector childInjector;

  private final Set<HeliosModule> modules = new HashSet<>();

  @Inject
  @SuppressWarnings("unchecked")
  public ModuleManager(@NonNull Injector injector) {
    this.injector = injector;
    this.childInjector = injector.createChildInjector((binder) -> {
      var modules = Multibinder.newSetBinder(binder, HeliosModule.class);
      modules.addBinding().to(HeliosCoreModule.class).in(Singleton.class);

      // TODO
    });
  }

  /**
   * Performs the initialization of all available modules.
   */
  public void initializeModules() {
    var type = (TypeLiteral<Set<HeliosModule>>) TypeLiteral.get(Types.setOf(HeliosModule.class));
    var modules = this.childInjector.getInstance(Key.get(type));

    logger.info("performing module initialization:");
    logger.info("");

    modules.forEach((m) -> {
      logger.info("=== %s v%s ===", m.getName(), m.getVersion());
      logger.info("description: %s", m.getDescription());
      logger.info("categories: %s",
          m.getCategories().stream().map((c) -> c.toString().toLowerCase())
              .collect(Collectors.joining(", ")));
      if (!m.getDevelopers().isEmpty()) {
        logger.info("developers: %s", m.getDevelopers().stream().collect(Collectors.joining(", ")));
      }
      if (!m.getContributors().isEmpty()) {
        logger.info("contributors: %s",
            m.getContributors().stream().collect(Collectors.joining(", ")));
      }

      try {
        m.initialize();
      } catch (Throwable ex) {
        logger.error("failed to initialize module", ex);
      }

      this.modules.add(m);
    });

    logger.info("");

    logger.info("performing initial theme generation (this may take a few seconds)");
    this.injector.getInstance(ThemeManager.class).refresh();
  }

  /**
   * Retrieves the complete list of installed modules within the application.
   *
   * @return a list of modules.
   */
  @NonNull
  public Set<HeliosModuleMetadata> getModules() {
    return Collections.unmodifiableSet(this.modules);
  }
}
