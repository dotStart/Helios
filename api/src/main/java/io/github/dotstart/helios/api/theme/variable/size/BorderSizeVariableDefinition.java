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
package io.github.dotstart.helios.api.theme.variable.size;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.dotstart.helios.api.theme.variable.AbstractVariableDefinition;
import io.github.dotstart.helios.api.theme.variable.Variable;
import java.net.URI;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class BorderSizeVariableDefinition extends AbstractVariableDefinition {

  private final SizeUnit unit;
  private final double defaultTopValue;
  private final double defaultRightValue;
  private final double defaultBottomValue;
  private final double defaultLeftValue;

  public BorderSizeVariableDefinition(
      @NonNull URI uri,
      @NonNull String displayName,
      @NonNull String description,
      double defaultTopValue,
      double defaultRightValue,
      double defaultBottomValue,
      double defaultLeftValue,
      @NonNull SizeUnit unit) {
    super(uri, displayName, description);
    this.unit = unit;
    this.defaultTopValue = defaultTopValue;
    this.defaultRightValue = defaultRightValue;
    this.defaultBottomValue = defaultBottomValue;
    this.defaultLeftValue = defaultLeftValue;
  }

  public BorderSizeVariableDefinition(
      @NonNull String uri,
      @NonNull String displayName,
      @NonNull String description,
      double defaultTopValue,
      double defaultRightValue,
      double defaultBottomValue,
      double defaultLeftValue,
      @NonNull SizeUnit unit) {
    super(uri, displayName, description);
    this.unit = unit;
    this.defaultTopValue = defaultTopValue;
    this.defaultRightValue = defaultRightValue;
    this.defaultBottomValue = defaultBottomValue;
    this.defaultLeftValue = defaultLeftValue;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Variable createVariable() {
    return new BorderSizeVariable(
        this.uri,
        this.defaultTopValue,
        this.defaultRightValue,
        this.defaultBottomValue,
        this.defaultLeftValue,
        this.unit
    );
  }
}
