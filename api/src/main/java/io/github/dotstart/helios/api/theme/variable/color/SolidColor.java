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
import java.util.Locale;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Represents a single solid color.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class SolidColor implements Color {

  public static SolidColor BLACK = new SolidColor();
  public static SolidColor WHITE = new SolidColor(255, 255, 255);
  public static SolidColor TRANSPARENT = new SolidColor(255, 255, 255, 0);

  public static SolidColor RED = new SolidColor(255, 0, 0);
  public static SolidColor GREEN = new SolidColor(0, 255, 0);
  public static SolidColor BLUE = new SolidColor(0, 0, 255);

  private final IntegerProperty red = new SimpleIntegerProperty();
  private final IntegerProperty green = new SimpleIntegerProperty();
  private final IntegerProperty blue = new SimpleIntegerProperty();
  private final IntegerProperty alpha = new SimpleIntegerProperty();

  public SolidColor() {
    this(0, 0, 0, 255);
  }

  public SolidColor(int red, int green, int blue, int alpha) {
    if (red > 255) {
      red = 255;
    } else if (red < 0) {
      red = 0;
    }
    if (green > 255) {
      green = 255;
    } else if (green < 0) {
      green = 0;
    }
    if (blue > 255) {
      blue = 255;
    } else if (blue < 0) {
      blue = 0;
    }
    if (alpha > 255) {
      alpha = 255;
    } else if (alpha < 0) {
      alpha = 0;
    }

    this.red.set(red);
    this.green.set(green);
    this.blue.set(blue);
    this.alpha.set(alpha);
  }

  public SolidColor(int red, int green, int blue) {
    this(red, green, blue, 255);
  }

  public SolidColor(double red, double green, double blue, double alpha) {
    this((int) Math.round(red * 255), (int) Math.round(green * 255),
        (int) Math.round(blue * 255), (int) Math.round(alpha * 255));
  }

  public SolidColor(double red, double green, double blue) {
    this(red, green, blue, 1);
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public ColorType getType() {
    return ColorType.SOLID;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Override
  public String toCssInstruction() {
    if (this.alpha.get() == 255) {
      return String.format("#%02X%02X%02X", this.red.get(), this.green.get(), this.blue.get());
    }

    return String.format(
        Locale.ENGLISH, // required for dot instead of comma
        "rgba(%d, %d, %d, %.4f)",
        this.red.get(),
        this.green.get(),
        this.blue.get(),
        this.alpha.get() / 255d
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void read(@NonNull ByteBuf buf) {
    if (!buf.isReadable(4)) {
      throw new IllegalArgumentException(
          "Illegal solid color buffer: Expected at least 4 bytes of data but got " + buf
              .readableBytes());
    }

    this.red.set(buf.readUnsignedByte());
    this.green.set(buf.readUnsignedByte());
    this.blue.set(buf.readUnsignedByte());
    this.alpha.set(buf.readUnsignedByte());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(@NonNull ByteBuf buf) {
    buf.ensureWritable(4);

    buf.writeByte(this.red.get());
    buf.writeByte(this.green.get());
    buf.writeByte(this.blue.get());
    buf.writeByte(this.alpha.get());
  }

  @NonNull
  public IntegerProperty redProperty() {
    return this.red;
  }

  @NonNull
  public IntegerProperty greenProperty() {
    return this.green;
  }

  @NonNull
  public IntegerProperty blueProperty() {
    return this.blue;
  }

  @NonNull
  public IntegerProperty alphaProperty() {
    return this.alpha;
  }

  public int getRed() {
    return this.red.get();
  }

  public void setRed(int red) {
    this.red.set(red);
  }

  public double getRedPercentage() {
    return this.red.get() / 255d;
  }

  public void setRedPercentage(double red) {
    this.red.set((int) Math.round(red * 255));
  }

  public int getGreen() {
    return this.green.get();
  }

  public void setGreen(int green) {
    this.green.set(green);
  }

  public double getGreenPercentage() {
    return this.green.get() / 255d;
  }

  public void setGreenPercentage(double green) {
    this.green.set((int) Math.round(green * 255));
  }

  public int getBlue() {
    return this.blue.get();
  }

  public void setBlue(int blue) {
    this.blue.set(blue);
  }

  public double getBluePercentage() {
    return this.blue.get() / 255d;
  }

  public void setBluePercentage(double blue) {
    this.blue.set((int) Math.round(blue * 255));
  }

  public int getAlpha() {
    return this.alpha.get();
  }

  public void setAlpha(int alpha) {
    this.alpha.set(alpha);
  }

  public double getAlphaPercentage() {
    return this.alpha.get() / 255d;
  }

  public void setAlphaPercentage(double alpha) {
    this.alpha.set((int) Math.round(alpha * 255));
  }

  @NonNull
  public javafx.scene.paint.Color toFx() {
    return new javafx.scene.paint.Color(this.getRedPercentage(), this.getGreenPercentage(),
        this.getBluePercentage(), this.getAlphaPercentage());
  }

  public void fromFx(@NonNull javafx.scene.paint.Color c) {
    this.setRedPercentage(c.getRed());
    this.setGreenPercentage(c.getGreen());
    this.setBluePercentage(c.getBlue());
    this.setAlphaPercentage(c.getOpacity());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SolidColor)) {
      return false;
    }
    SolidColor that = (SolidColor) o;
    return this.red == that.red &&
        this.green == that.green &&
        this.blue == that.blue &&
        this.alpha == that.alpha;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.red, this.green, this.blue, this.alpha);
  }
}
