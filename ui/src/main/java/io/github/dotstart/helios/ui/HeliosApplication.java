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
package io.github.dotstart.helios.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.di.provider.FXMLLoaderProvider;
import io.github.dotstart.helios.ui.module.ModuleManager;
import io.github.dotstart.helios.ui.theme.ThemeManager;
import io.github.dotstart.helios.ui.utility.WindowUtility;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Provides a JavaFX compatible application entry point.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class HeliosApplication extends Application {

  private static final Logger logger = LogManager.getFormatterLogger(HeliosApplication.class);

  private final Injector injector;

  public HeliosApplication() {
    this.injector = Guice.createInjector((binder) -> {
      binder.bind(HeliosApplication.class).toInstance(this);

      binder.bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
    });
  }

  /**
   * JVM Entry Point
   *
   * @param arguments an array of command line arguments.
   */
  public static void main(@NonNull String[] arguments) {
    System.out.println("Helios v" + Optional
        .ofNullable(HeliosApplication.class.getPackage().getImplementationVersion())
        .orElse("0.0.0+dev"));
    System.out.println("Published under the terms of the Apache License, Version 2.0");
    System.out.println();

    if (Boolean.getBoolean("io.github.dotstart.helios.ui.enableDebugLogging")) {
      Configurator.setRootLevel(Level.DEBUG);
      logger.warn("debug logging is enabled");
    }

    logger.info("nano epoch: %d", System.nanoTime());
    var t = new Thread(HeliosApplication::benchmarkTimerResolution);
    t.setName("benchmark");
    t.start();

    Application.launch(HeliosApplication.class, arguments);
  }

  /**
   * Attempts to roughly estimate the system timer resolution (in fractions of seconds).
   */
  private static void benchmarkTimerResolution() {
    logger.info("performing benchmark of system clock resolution (this may take a few seconds)");

    long resolution = Long.MAX_VALUE;
    long previous = System.nanoTime();
    for (int i = 0; i < 1000000; ++i) {
      long current = System.nanoTime();
      long difference = current - previous;

      if (difference != 0) {
        resolution = Math.min(resolution, current - previous);
        previous = current;
      }
    }

    logger.info("system timer resolution: %.09f seconds", resolution / 1000000000d);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(@NonNull Stage primaryStage) throws Exception {
    Thread.currentThread().setName("ui");

    printCapabilities();
    this.injector.getInstance(ModuleManager.class).initializeModules();

    var scene = WindowUtility.createScene(this.injector, "/fxml/MainWindow.fxml");
    this.injector.getInstance(ThemeManager.class).hookScene(scene);

    var transparencyAvailable = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
    primaryStage.initStyle(transparencyAvailable ? StageStyle.TRANSPARENT : StageStyle.UNDECORATED);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Writes all available JavaFX conditional features to the log.
   */
  private static void printCapabilities() {
    var supported = EnumSet.allOf(ConditionalFeature.class).stream()
        .filter(Platform::isSupported)
        .map(ConditionalFeature::toString)
        .collect(Collectors.joining(", "));

    logger.info("available JavaFX features: %s", supported);
  }
}
