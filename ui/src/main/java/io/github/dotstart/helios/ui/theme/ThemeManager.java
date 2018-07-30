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
package io.github.dotstart.helios.ui.theme;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.github.dotstart.helios.api.layout.ComponentRegistry;
import io.github.dotstart.helios.api.node.Component;
import io.github.dotstart.helios.api.theme.Theme;
import io.github.dotstart.helios.api.theme.variable.Variable;
import io.github.dotstart.helios.api.theme.variable.VariableDefinition;
import io.github.dotstart.helios.api.theme.variable.color.ColorVariableDefinition;
import io.github.dotstart.helios.api.theme.variable.color.SolidColor;
import io.github.dotstart.helios.api.theme.variable.size.BorderSizeVariableDefinition;
import io.github.dotstart.helios.api.theme.variable.size.SizeUnit;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides a manager which handles the regeneration of theme stylesheets within the application.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Singleton
public class ThemeManager implements AutoCloseable {

  private static final Logger logger = LogManager.getFormatterLogger(ThemeManager.class);

  /**
   * Defines a list of global variables (e.g. variables which are not specific to a single given
   * component).
   */
  private static final Set<VariableDefinition> GLOBALS = Set.of(
      // Colors
      new ColorVariableDefinition(
          "helios+theme://io.dotstart.github.helios/backgroundColor",
          "Background Color", "Specifies the window background color",
          SolidColor.BLACK
      ),
      new ColorVariableDefinition(
          "helios+theme://io.dotstart.github.helios/textColor",
          "Text Color", "Specifies the standard text color",
          SolidColor.WHITE
      ),

      // Margins & Paddings
      new BorderSizeVariableDefinition(
          "helios+theme://io.dotstart.github.helios/componentPadding",
          "Component Padding",
          "Specifies the padding (inner spacing) given to each respective component",
          5, 5, 5, 5, SizeUnit.PIXELS
      )
  );

  private final ComponentRegistry componentRegistry;

  private final ObjectProperty<Theme> theme = new SimpleObjectProperty<>(
      new Theme("Default Theme"));
  private final ObservableList<Scene> scenes = FXCollections.observableArrayList();

  private final ObjectProperty<Path> stylesheetPath = new SimpleObjectProperty<>();

  private final ObservableList<Scene> scenesView = FXCollections
      .unmodifiableObservableList(this.scenes);

  @Inject
  public ThemeManager(@NonNull ComponentRegistry componentRegistry) {
    this.componentRegistry = componentRegistry;
  }

  /**
   * <p>Refreshes the current application theme.</p>
   *
   * <p>Note that this call is somewhat expensive as it will cause all component stylesheets to be
   * re-compiled. In addition, it will cause all themable scenes to reload and apply their
   * respective user agent stylesheets.</p>
   */
  public void refresh() {
    try {
      var path = Files.createTempFile("helios", "theme.css");
      logger.debug("allocated temporary file %s for application theme", path);

      var source = new StringBuilder();
      var variables = new HashMap<URI, VariableDefinition>();

      GLOBALS.forEach((v) -> variables.put(v.getUri(), v));
      try (var style = this.getClass().getResourceAsStream("/stylesheet/global.scss")) {
        source.append(new String(ByteStreams.toByteArray(style), StandardCharsets.UTF_8));
        source.append("\n\n\n");
      }

      this.componentRegistry.getComponents()
          .forEach((c) -> {
            try (var style = c.getStyleResource()) {
              if (style == null) {
                return;
              }

              source.append(new String(ByteStreams.toByteArray(style), StandardCharsets.UTF_8));
              source.append("\n\n\n");
              c.getStyleVariables().forEach((v) -> variables.put(v.getUri(), v));
            } catch (IOException ex) {
              logger.error("failed to load style for component %s", c.getName(), ex);
            }
          });
      var themeVariables = new HashMap<URI, Variable>();
      this.theme.getValue().getVariables().forEach((v) -> themeVariables.put(v.getUri(), v));

      var compiler = new Compiler();
      var options = new Options();
      options.setFunctionProviders(List.of(new SassFunctionProvider(variables, themeVariables)));

      try {
        var out = compiler.compileString(source.toString(), options);
        var css = out.getCss();
        if (css == null) {
          css = "";
        }

        logger.debug("written updated application theme to disk");
        Files.write(path, css.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
            StandardOpenOption.WRITE);
      } catch (CompilationException ex) {
        logger.error("failed to reload theme", ex);
        return;
      }

      var currentPath = this.stylesheetPath.get();
      if (currentPath != null) {
        logger.debug("removing previous theme files");

        try {
          Files.delete(currentPath);
        } catch (IOException ex) {
          logger.warn("Failed to remove compiled theme file: " + currentPath, ex);
        }
      }

      this.stylesheetPath.set(path);
    } catch (IOException ex) {
      logger.error("failed to read or write one or more files", ex);
    }
  }

  /**
   * <p>Retrieves the currently loaded theme.</p>
   *
   * <p>When no specific theme has been loaded, an empty default theme will be returned
   * instead.</p>
   *
   * @return a theme.
   */
  @NonNull
  public Theme getTheme() {
    return this.theme.get();
  }

  @NonNull
  public ObjectProperty<Theme> themeProperty() {
    return this.theme;
  }

  public void setTheme(@NonNull Theme theme) {
    this.theme.set(theme);
  }

  /**
   * <p>Retrieves the local path to the stylesheet file.</p>
   *
   * <p>This path changes every time the stylesheet is reloaded by the application. Note that this
   * is a workaround for JavaFX's missing public style reload APIs (StyleManager is sadly
   * unavailable to us at the moment).</p>
   *
   * @return a stylesheet path.
   */
  @NonNull
  public Path getStylesheetPath() {
    return this.stylesheetPath.get();
  }

  @NonNull
  public ReadOnlyObjectProperty<Path> stylesheetPathProperty() {
    return this.stylesheetPath;
  }

  /**
   * Applies the manager's theme to the specified scene and ensures that future reloads will be
   * applied in a similar fashion.
   *
   * @param scene a scene.
   */
  public void hookScene(@NonNull Scene scene) {
    scene.userAgentStylesheetProperty().bind(Bindings.createStringBinding(
        () -> {
          logger.debug("updating stylesheet for scene");
          var path = this.stylesheetPath.get();
          if (path == null) {
            logger.debug("stylesheet not ready");
            return null;
          }
          return path.toUri().toString();
        },
        this.stylesheetPath
    ));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws Exception {
    // TODO: Make sure this is called on application shutdown to prevent tempfile leaks
    var path = this.stylesheetPath.get();

    if (path != null) {
      Files.deleteIfExists(path);
    }
  }

  /**
   * Provides custom functions to sass based component stylesheets.
   */
  public final class SassFunctionProvider {

    private final Map<URI, VariableDefinition> variableDefinitions;
    private final Map<URI, Variable> variables;

    private SassFunctionProvider(
        @NonNull Map<URI, VariableDefinition> variableDefinitions,
        @NonNull Map<URI, Variable> variables) {
      this.variableDefinitions = variableDefinitions;
      this.variables = variables;
    }

    /**
     * Exposes the generated class name for a given component (based on its URI).
     *
     * @param ref a component uri.
     * @return a class name or null.
     */
    @NonNull
    public String componentClass(@NonNull String ref) {
      var uri = URI.create(ref);
      return ThemeManager.this.componentRegistry.getComponent(uri)
          .map(Component::getGeneratedClassName)
          .map((c) -> "." + c)
          .orElseThrow(() -> new IllegalArgumentException("No such component: " + uri));
    }

    /**
     * Retrieves the value of a specific theme variable.
     *
     * @param ref a variable uri.
     * @return a value.
     * @throws IllegalArgumentException when no such variable is defined.
     */
    @NonNull
    public Object themeVariable(@NonNull String ref) {
      var uri = URI.create(ref);

      var variable = this.variables.get(uri);
      if (variable != null) {
        return variable.toCss();
      }

      var definition = this.variableDefinitions.get(uri);
      if (definition == null) {
        throw new IllegalArgumentException("No such variable: " + uri);
      }

      return definition.createVariable().toCss();
    }
  }
}
