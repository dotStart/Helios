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
import io.bit3.jsass.type.SassColor;
import io.netty.buffer.ByteBuf;
import java.net.URI;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * Provides a variable which stores an arbitrary color value.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ColorVariable extends AbstractVariable {

  private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

  public ColorVariable(@NonNull URI uri, @NonNull Color initialValue) {
    super(uri);
    this.color.setValue(initialValue);
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
    if (!buf.isReadable(4)) {
      throw new IllegalArgumentException("Illegal color value: At least 4 bytes required");
    }

    var r = buf.readUnsignedByte();
    var g = buf.readUnsignedByte();
    var b = buf.readUnsignedByte();
    var a = buf.readUnsignedByte();

    this.color.setValue(Color.rgb((int) r, (int) g, (int) b, a / 255d));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NonNull ByteBuf buf) {
    buf.ensureWritable(4);

    var c = this.color.getValue();
    buf.writeByte((int) Math.round(c.getRed() * 255));
    buf.writeByte((int) Math.round(c.getGreen() * 255));
    buf.writeByte((int) Math.round(c.getBlue() * 255));
    buf.writeByte((int) Math.round(c.getOpacity() * 255));
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public SassColor toCss() {
    var c = this.color.getValue();
    return new SassColor(
        c.getRed(),
        c.getGreen(),
        c.getBlue(),
        c.getOpacity()
    );
  }

  @NonNull
  public Color getColor() {
    return this.color.get();
  }

  public void setColor(@NonNull Color color) {
    this.color.set(color);
  }

  @NonNull
  public ObjectProperty<Color> colorProperty() {
    return this.color;
  }
}
