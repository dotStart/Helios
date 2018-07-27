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
import io.github.dotstart.helios.api.theme.variable.AbstractVariable;
import io.github.dotstart.helios.api.theme.variable.Variable;
import io.netty.buffer.ByteBuf;
import java.net.URI;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * Provides a variable which stores an arbitrary color value.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ColorVariable extends AbstractVariable {

  private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

  public ColorVariable(@NonNull URI uri, @Nullable Color initialValue) {
    super(uri);
    this.color.set(initialValue);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Node createSettingsNode() {
    throw new UnsupportedOperationException(); // TODO
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public Variable duplicate() {
    return new ColorVariable(this.uri, this.color.getValue());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void read(@NonNull ByteBuf buf) {
    if (!buf.isReadable(1)) {
      throw new IllegalArgumentException("Illegal color value: At least one byte required");
    }

    var typeIndex = buf.readUnsignedByte();
    if (typeIndex == 0) {
      return;
    }
    --typeIndex;
    if (typeIndex > ColorType.values().length) {
      throw new IllegalArgumentException(
          "Illegal color value: 0 <= i < " + ColorType.values().length + " but was " + typeIndex);
    }

    var type = ColorType.values()[typeIndex];
    this.color.set(type.read(buf));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NonNull ByteBuf buf) {
    buf.ensureWritable(1);

    var c = this.color.get();
    if (c == null) {
      buf.writeByte(0);
      return;
    }
    buf.writeByte(c.getType().ordinal() + 1);
    c.write(buf);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String toCss() {
    var c = this.color.getValue();

    if (c == null) {
      return "inherit";
    }

    return c.toCssInstruction();
  }

  @Nullable
  public Color getColor() {
    return this.color.get();
  }

  public void setColor(@Nullable Color color) {
    this.color.set(color);
  }

  @NonNull
  public ObjectProperty<Color> colorProperty() {
    return this.color;
  }
}
