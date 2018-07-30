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
package io.github.dotstart.helios.api.utility;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Provides utility methods for the (de-)serialization of various complex data types within themes
 * and components configurations.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public final class SerializationUtility {

  private static final Charset STR_CHARSET = StandardCharsets.UTF_8;

  private SerializationUtility() {
  }

  /**
   * <p>Decodes a string from the specified source buffer.</p>
   *
   * <p>When none was serialized into the buffer previously, an empty string will be returned
   * instead.</p>
   *
   * @param source a source buffer.
   * @return a string.
   */
  @NonNull
  public static String readString(@NonNull ByteBuf source) {
    if (!source.isReadable(1)) {
      throw new IllegalArgumentException(
          "Reached end of buffer: Expected at least one readable byte");
    }

    var len = source.readUnsignedByte();
    if (len == 0) {
      return "";
    }

    var buf = new byte[len];
    source.readBytes(buf);

    return new String(buf, STR_CHARSET);
  }

  /**
   * <p>Encodes a string into the specified target buffer.</p>
   *
   * <p>When null or an empty string is passed, the string will be encoded as a zero length string
   * instead. Please note that {@link #readString(ByteBuf)} will interpret it as such as well.
   * Handling is up to the caller.</p>
   *
   * <p>This method is limited to a total of 255 bytes (actual amount of characters depends on the
   * encoded characters as represented within UTF-8).</p>
   *
   * @param target a target buffer.
   * @param value an arbitrary string.
   */
  public static void writeString(@NonNull ByteBuf target, @Nullable String value) {
    if (value == null) {
      value = "";
    }

    target.writeByte(value.length());

    if (value.isEmpty()) {
      return;
    }
    target.writeBytes(value.getBytes(STR_CHARSET));
  }
}
