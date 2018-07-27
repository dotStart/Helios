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
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Provides a list of various supported color modes which each provide unique abilities and styles
 * to component elements.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public enum ColorType {

  /**
   * <p><strong>Solid Color</strong></p>
   *
   * <p>Indicates that only a single solid color is to be rendered for a given on-screen
   * element. This is the standard behavior.</p>
   */
  SOLID(SolidColor.class),

  /**
   * <p><strong>Linear Gradient</strong></p>
   *
   * <p>Indicates that a gradient consisting of an arbitrary amount of colors is to be rendered for
   * a given on-screen element.</p>
   */
  LINEAR_GRADIENT(LinearGradient.class);

  private final Class<? extends Color> type;

  ColorType(@NonNull Class<? extends Color> type) {
    this.type = type;
  }

  /**
   * Reads a color from the specified buffer.
   *
   * @param buf a buffer.
   * @return a color.
   * @throws IllegalArgumentException when the input data is invalid.
   * @throws IllegalStateException when the default object construction fails.
   * @throws UnsupportedOperationException when the specified color type does not support
   * de-serialization.
   */
  @NonNull
  public Color read(@NonNull ByteBuf buf) {
    Color color;
    try {
      var constructor = MethodHandles.lookup()
          .findConstructor(this.type, MethodType.methodType(void.class));
      color = (Color) constructor.invoke();
    } catch (NoSuchMethodException | IllegalAccessException ex) {
      throw new UnsupportedOperationException(
          "Color type does not support construction from serialized data");
    } catch (Throwable ex) {
      throw new IllegalStateException("Failed to de-serialize color of type " + this.type.getName()
          + ": Default object construction failed", ex);
    }

    color.read(buf);
    return color;
  }
}
