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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides a list of supported CSS size units.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public enum SizeUnit {
  CENTIMETERS("cm"),
  INCHES("in"),
  MILLIMETERS("mm"),
  PERCENT("%"),
  RELATIVE("em"),
  PIXELS("px"),
  POINTS("pt");

  private static final Map<String, SizeUnit> unitMap;

  static {
    unitMap = Stream.of(SizeUnit.values())
        .collect(Collectors.toMap(
            (u) -> u.suffix,
            (u) -> u
        ));
  }

  private final String suffix;

  SizeUnit(@NonNull String suffix) {
    this.suffix = suffix;
  }

  /**
   * Retrieves a size unit based on its respective suffix.
   *
   * @param suffix a suffix.
   * @return a size unit or, if no such unit is supported, an empty optional.
   */
  @NonNull
  public static Optional<SizeUnit> bySuffix(@NonNull String suffix) {
    return Optional.ofNullable(unitMap.get(suffix));
  }

  /**
   * Retrieves the CSS compatible suffix for this size unit.
   *
   * @return a suffix.
   */
  @NonNull
  public String getSuffix() {
    return this.suffix;
  }
}
