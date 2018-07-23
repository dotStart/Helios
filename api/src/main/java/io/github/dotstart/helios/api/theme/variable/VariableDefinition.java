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
package io.github.dotstart.helios.api.theme.variable;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.net.URI;

/**
 * <p>Provides a variable definition which identifies the respective variable display name, type
 * and URI.</p>
 *
 * <p>Definitions effectively decouple theme variables from their actual definitions and are
 * provided by the loaded components.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface VariableDefinition {

  /**
   * <p>Retrieves a human readable variable name.</p>
   *
   * <p>This name will be used to refer to this particular variable within logs and the application
   * UI as necessary and may be freely defined. The name should roughly reflect the respective style
   * area that it affects.</p>
   *
   * @return a display name.
   */
  @NonNull
  String getDisplayName();

  /**
   * <p>Retrieves a human readable description.</p>
   *
   * <p>This value will be displayed within the settings help (typically within a tooltip) whithin
   * the respective theme customization menus.</p>
   *
   * @return a description.
   */
  @NonNull
  String getDescription();

  /**
   * <p>Retrieves the URI for this respective variable.</p>
   *
   * <p>The variable URI is prefixed by the respective component URI and may otherwise be chosen
   * freely. For instance: {@code helios+component://org.example.helios/mycomponent/variable}.</p>
   *
   * <p>Note that this value is also used to map theme variables to their actual definitions. When
   * a theme contains an unknown variable it will be tossed. If a theme does not contain a
   * definition with the same identification it will be substituted with the default value.</p>
   *
   * @return a variable uri.
   */
  @NonNull
  URI getUri();

  /**
   * <p>Retrieves the name with which this variable will be addressed within the stylesheet.</p>
   *
   * <p>Typically this method should not be overridden by implementations as it will be referenced
   * automatically referenced within other application components by default. This method is only
   * provided for compatibility reasons (e.g. when third party stylesheets are embedded).</p>
   *
   * @return a css variable name.
   */
  @NonNull
  default String getCssName() {
    var uri = this.getUri();
    return uri.getHost().replace(".", "_") + "___" + uri.getPath().replace("/", "_");
  }

  /**
   * <p>Retrieves the variable implementation which represents this respective component variable
   * within themes.</p>
   *
   * <p>This method is responsible for constructing a variable for use within a theme with its
   * respective default values. Themes may override these values as needed (either through the
   * respective user interface provided via {@link Variable#createSettingsNode()} or through a
   * previously stored copy of the theme).</p>
   *
   * @return a variable.
   */
  @NonNull
  Variable createVariable();
}
