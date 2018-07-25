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
import java.util.Objects;

/**
 * Provides an abstract variable definition implementation which contains the most commonly needed
 * functionality within definitions.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class AbstractVariableDefinition implements VariableDefinition {

  protected final URI uri;
  private final String displayName;
  private final String description;

  public AbstractVariableDefinition(@NonNull URI uri, @NonNull String displayName,
      @NonNull String description) {
    this.uri = uri;
    this.displayName = displayName;
    this.description = description;
  }

  public AbstractVariableDefinition(@NonNull String uri, @NonNull String displayName,
      @NonNull String description) {
    this(URI.create(uri), displayName, description);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public URI getUri() {
    return this.uri;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractVariableDefinition)) {
      return false;
    }
    AbstractVariableDefinition that = (AbstractVariableDefinition) o;
    return Objects.equals(this.uri, that.uri);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.uri);
  }
}
