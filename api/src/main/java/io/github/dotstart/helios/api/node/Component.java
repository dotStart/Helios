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

import edu.umd.cs.findbugs.annotations.CreatesObligation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.helios.api.theme.variable.VariableDefinition;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;

/**
 * <p>Provides a base interface for splitter components (such as timers, graphs or titles).</p>
 *
 * <p>Components may optionally define a set of configuration properties which will be displayed to
 * the user through the layout panes. When no configuration is desired, {@link Void} will be used as
 * a placeholder instead.</p>
 *
 * @param <C> an arbitrary configuration object.
 * @param <N> the node implementation which provides the display portion of this component.
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Component<N extends Node & ComponentNode, C> {

  /**
   * Defines a pseudoclass which is applied to components when they are displayed within the
   * horizontal orientation.
   */
  PseudoClass HORIZONTAL_DIRECTION = PseudoClass.getPseudoClass("horizontal");

  /**
   * Defines a pseudoclass which is applied to components when they are displayed within the
   * vertical orientation.
   */
  PseudoClass VERTICAL_DIRECTION = PseudoClass.getPseudoClass("vertical");

  /**
   * <p>Updates the layout direction of an arbitrary node.</p>
   *
   * <p>Note that this method is mainly provided as a utility for node implementations and should
   * never be invoked without receiving a change from the root layout components.</p>
   *
   * @param node an arbitrary node.
   * @param direction a new layout direction.
   */
  static void setNodeDirection(@NonNull Node node, @NonNull Direction direction) {
    node.pseudoClassStateChanged(HORIZONTAL_DIRECTION, direction == Direction.HORIZONTAL);
    node.pseudoClassStateChanged(VERTICAL_DIRECTION, direction == Direction.VERTICAL);
  }

  /**
   * <p>Updates the layout direction of multiple nodes within a layout tree.</p>
   *
   * <p>Note that this method is mainly provided as a utility for node implementations and should
   * never be invoked without receiving a change from the root layout components.</p>
   *
   * @param nodes an arbitrary collection of nodes.
   * @param direction a new layout direction.
   */
  static void setNodeDirection(@NonNull Collection<? extends Node> nodes,
      @Nullable Direction direction) {
    final Direction dir = direction == null ? Direction.HORIZONTAL : direction;
    nodes.forEach((n) -> setNodeDirection(n, dir));
  }

  /**
   * <p>Creates an observable value which tracks changes to a node's orientation.</p>
   *
   * <p>The node orientation is typically only ever changed by the root component (through the
   * respective settings pane).</p>
   *
   * @param node an arbitrary component node.
   * @return an observable version of the component direction.
   */
  @NonNull
  static ObservableValue<Direction> createDirectionObservable(@NonNull Styleable node) {
    var classes = node.getPseudoClassStates(); // strong reference required to prevent gc

    return Bindings.createObjectBinding(
        () -> {
          if (classes.contains(VERTICAL_DIRECTION)) {
            return Direction.VERTICAL;
          }
          return Direction.HORIZONTAL; // fallback value
        },
        classes
    );
  }

  /**
   * <p>Registers a recursive node updater with the specified observable.</p>
   *
   * <p>The resulting listener will automatically update each child node within the given
   * collection when the direction observable is changed. This is the recommended method of passing
   * the layout direction to child nodes.</p>
   *
   * @param direction a direction observable.
   * @param nodes a collection of child nodes.
   */
  @SuppressWarnings("unchecked")
  static void registerRecursiveNodeUpdater(@NonNull ObservableValue<Direction> direction,
      @NonNull Collection<? extends Node> nodes) {
    ChangeListener<Direction> listener = (observable, oldValue, newValue) -> setNodeDirection(nodes, newValue);

    if (nodes instanceof ObservableList) {
      var ob = (ObservableList<? extends Node>) nodes;
      ob.addListener((ListChangeListener<Node>) c -> {
        while (c.next()) {
          setNodeDirection(c.getAddedSubList(), direction.getValue());
        }
      });
    }

    direction.addListener(listener);
    listener.changed(direction, null, direction.getValue());
  }

  /**
   * <p>Retrieves the globally unique resource identifier for this component.</p>
   *
   * <p>This URI is used for identification purposes within serialized versions of the layout and
   * is expected to follow the following scheme: {@code helios+component://&lt;package&gt;/&lt;component&gt;}
   * (for instance: {@code helios+component://org.example.helios.package/mycomponent}.</p>
   *
   * @return a globally unique component identifier.
   */
  @NonNull
  default URI getURI() {
    try {
      return new URI("helios+component", this.getClass().getPackageName(),
          "/" + this.getClass().getSimpleName(), null);
    } catch (URISyntaxException ex) {
      throw new IllegalStateException(
          "Illegal component URI - Manual URI specification may be required", ex);
    }
  }

  /**
   * <p>Retrieves a human readable name for this component in its current configuration.</p>
   *
   * <p>This value is used primarily for logging purposes and configuration panels.</p>
   *
   * @return a human readable component name.
   */
  @NonNull
  String getName();

  /**
   * <p>Retrieves a human readable description for this component.</p>
   *
   * <p>This value will be displayed in tooltips within various configuration panels (such as the
   * "Add Component" dialogues and tooltips).</p>
   *
   * @return a human readable component description.
   */
  @NonNull
  String getDescription();

  /**
   * <p>Evaluates whether this component is "unique".</p>
   *
   * <p>Unique components may only be present once within a single layout (typically due to
   * technical limitations).</p>
   *
   * @return true if this component is unique, false otherwise.
   */
  default boolean isUnique() {
    return true;
  }

  /**
   * <p>Retrieves the type (e.g. class) which is used to represent the component's configuration
   * when serialized to disk (e.g. as part of a layout file).</p>
   *
   * <p>When no configuration is desired, this method should return {@link Void} instead of a real
   * value.</p>
   *
   * @return a configuration type.
   */
  @NonNull
  Class<C> getConfigurationType();

  /**
   * <p>Constructs a completely new JavaFX node which provides the display capabilities for this
   * component within the actual splitter window.</p>
   *
   * <p>Note that the {@link #HORIZONTAL_DIRECTION} and {@link #VERTICAL_DIRECTION} pseudoclasses
   * will be applied automatically by the application as they change. Whether and how these classes
   * are handled by the returned node is up to the component author and in no way defined.</p>
   *
   * @return a JavaFX node.
   */
  @NonNull
  N createNode();

  /**
   * <p>Constructs a JavaFX node based on a previously stored component configuration (such as a
   * layout file).</p>
   *
   * <p>Note that the {@link #HORIZONTAL_DIRECTION} and {@link #VERTICAL_DIRECTION} pseudoclasses
   * will be applied automatically by the application as they change. Whether and how these classes
   * are handled by the returned node is up to the component author and in no way defined.</p>
   *
   * @param conf a parsed configuration object.
   * @return a JavaFX node.
   * @throws UnsupportedOperationException when this component does not provide any configuration
   * properties.
   */
  @NonNull
  N loadNode(@NonNull C conf);

  /**
   * <p>Retrieves the stylesheet resource for this component.</p>
   *
   * <p>When no special stylesheet is desired, null will be returned instead. In this case, no
   * stylesheet processing will be performed for this component (e.g. the return value of {@link
   * #getStyleVariables()} will be ignored).</p>
   *
   * <p>Note that this behavior is not recommended as the global styles are rather rudimentary.</p>
   *
   * @return a style resource stream.
   */
  @Nullable
  @CreatesObligation
  default InputStream getStyleResource() {
    return null;
  }

  /**
   * <p>Retrieves a list of variable definitions provided by this component.</p>
   *
   * <p>The elements within this list will effectively be used to facilitate the generation of the
   * component stylesheet using a mixture of default and user defined style variables. When multiple
   * components share the same definitions, a single shared variable instance should be returned
   * instead.</p>
   *
   * @return a style variable.
   */
  @NonNull
  default Set<VariableDefinition> getStyleVariables() {
    return Collections.emptySet();
  }

  /**
   * Generates a globally unique class name for the specified component.
   *
   * @param component a component definition.
   */
  @NonNull
  static String getGeneratedClassName(@NonNull Component<?, ?> component) {
    var uri = component.getURI();
    return uri.getHost().replace(".", "_") + "___" + uri.getPath().replace("/", "_");
  }

  /**
   * Logs all relevant component details to the debug log.
   */
  default void logComponentDetails() {
    var logger = LogManager.getFormatterLogger(this.getClass());
    var uri = this.getURI();

    logger.debug("=> component %s", uri);
    logger.debug("style class: %s", getGeneratedClassName(this));
  }

  /**
   * Provides a list of valid timer layout directions.
   */
  enum Direction {
    HORIZONTAL {
      @NonNull
      @Override
      public Direction invert() {
        return VERTICAL;
      }
    },
    VERTICAL {
      @NonNull
      @Override
      public Direction invert() {
        return HORIZONTAL;
      }
    };

    /**
     * Retrieves the inversion for this direction.
     *
     * @return an inverted direction.
     */
    @NonNull
    public abstract Direction invert();
  }
}
