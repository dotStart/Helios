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
package io.github.dotstart.helios.api.theme.variable.color;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.dotstart.helios.api.theme.variable.AbstractVariableDefinition;
import java.net.URI;

/**
 * Provides a variable which stores an arbitrary color value (for instance, to describe the color of
 * a component background or text).
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ColorVariableDefinition extends AbstractVariableDefinition {

  private final Color defaultValue;

  public ColorVariableDefinition(@NonNull URI uri, @NonNull String displayName,
      @NonNull String description, @Nullable Color defaultValue) {
    super(uri, displayName, description);
    this.defaultValue = defaultValue;
  }

  public ColorVariableDefinition(@NonNull String uri, @NonNull String displayName,
      @NonNull String description, @Nullable Color defaultValue) {
    super(uri, displayName, description);
    this.defaultValue = defaultValue;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public ColorVariable createVariable() {
    return new ColorVariable(this.uri, this.defaultValue);
  }
}
