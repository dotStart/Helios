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
import io.netty.buffer.ByteBuf;
import java.net.URI;
import javafx.scene.Node;

/**
 * <p>Represents a variable with an arbitrary value.</p>
 *
 * <p>Each respective variable may be assigned a single value of an arbitrary type and serializes
 * its value into one of two formats: Binary (for permanent storage within the theme file) and CSS
 * compatible (e.g. a string which is passed to the transpiler).</p>
 *
 * <p>Variables are addressed via URIs (typically relative to their respective parent component)
 * and are thus globally unique (even when multiple components of the same name are provided by
 * different modules).</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Variable {

  /**
   * <p>Retrieves the URI for this particular variable.</p>
   *
   * <p>The variable URI is prefixed by the respective component URI and may otherwise be chosen
   * freely. For instance: {@code helios+component://org.example.helios/mycomponent/variable}.</p>
   *
   * @return a variable uri.
   */
  @NonNull
  URI getUri();

  /**
   * <p>Creates a JavaFX node which permits the customization of this variable's value.</p>
   *
   * <p>The respective node should specifically only refer to the value of the variable. The
   * respective name and description will be placed by the caller.</p>
   *
   * @return a settings node.
   */
  @NonNull
  Node createSettingsNode();

  /**
   * Creates an exact duplicate of this variable along with its current value.
   *
   * @return a variable duplicate.
   */
  @NonNull
  Variable duplicate();

  /**
   * <p>Decodes a variable value from the specified binary buffer.</p>
   *
   * <p>The actual format of the variable within the buffer depends on the implementation. There
   * are effectively no restrictions on how values are to be represented.</p>
   *
   * @param buf an input buffer.
   */
  void read(@NonNull ByteBuf buf);

  /**
   * <p>Encodes a variable value into the specified binary buffer.</p>
   *
   * <p>The actual format of the variable within the buffer depends on the implementation. There
   * are effectively no restrictions on how values are to be represented.</p>
   *
   * @param buf an output buffer.
   */
  void write(@NonNull ByteBuf buf);

  /**
   * <p>Encodes the value in its CSS compatible format.</p>
   *
   * <p>The returned string will be passed directly as a variable to the CSS transpiler and is thus
   * required to be a valid <a href="http://lesscss.org/">LESS</a> statement.</p>
   *
   * @return a css compatible version of this variable.
   */
  @NonNull
  Object toCss();
}
