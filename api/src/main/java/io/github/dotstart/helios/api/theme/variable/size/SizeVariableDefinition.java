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
import java.net.URI;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class SizeVariableDefinition extends AbstractVariableDefinition {

  private final double defaultValue;
  private final SizeUnit defaultUnit;

  public SizeVariableDefinition(
      @NonNull URI uri,
      @NonNull String displayName,
      @NonNull String description,
      double defaultValue,
      @NonNull SizeUnit defaultUnit) {
    super(uri, displayName, description);
    this.defaultValue = defaultValue;
    this.defaultUnit = defaultUnit;
  }

  public SizeVariableDefinition(@NonNull String uri,
      @NonNull String displayName,
      @NonNull String description,
      double defaultValue,
      @NonNull SizeUnit defaultUnit) {
    super(uri, displayName, description);
    this.defaultValue = defaultValue;
    this.defaultUnit = defaultUnit;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public SizeVariable createVariable() {
    return new SizeVariable(this.uri, this.defaultValue, this.defaultUnit);
  }

  public double getDefaultValue() {
    return this.defaultValue;
  }

  @NonNull
  public SizeUnit getDefaultUnit() {
    return this.defaultUnit;
  }
}
