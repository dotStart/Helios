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

/**
 * Represents an arbitrary fill color (such as a solid color or gradient) which may be placed within
 * color variables.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Color {

  /**
   * Retrieves the globally unique color type which is used to handle the serialization of the
   * respective color variable.
   *
   * @return a color type.
   */
  @NonNull
  ColorType getType();

  /**
   * <p>Converts the color value into its respective CSS instruction format.</p>
   *
   * <p>The respective instruction differs depending on the color implementation (for instance,
   * {@code linear-gradient(...)} will be used for gradients).</p>
   *
   * <p>Note that sass interprets these values as actual strings unless interpolated into the
   * component stylesheet: {@code #{componentVariable('helios+component://org.example/myComponentVariable'}}</p>
   *
   * @return a css instruction.
   */
  @NonNull
  String toCssInstruction();

  /**
   * @see io.github.dotstart.helios.api.theme.variable.Variable#read(ByteBuf)
   */
  void read(@NonNull ByteBuf buf);

  /**
   * @see io.github.dotstart.helios.api.theme.variable.Variable#write(ByteBuf)
   */
  void write(@NonNull ByteBuf buf);
}
