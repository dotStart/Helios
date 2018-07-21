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
package io.github.dotstart.helios.ui.utility;

import com.google.inject.Injector;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.StackWalker.Option;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Provides various utilities to simplify the creation of windows within the application context.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public final class WindowUtility {

  private static final StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);

  private WindowUtility() {
  }

  /**
   * @see #createScene(FXMLLoader, String)
   */
  @NonNull
  public static Scene createScene(@NonNull Injector injector, @NonNull String fxmlPath) {
    return createScene(injector.getInstance(FXMLLoader.class), fxmlPath);
  }

  /**
   * Creates a new scene using the specified loader and FXML path.
   *
   * @param loader an arbitrary FXML loader.
   * @param fxmlPath an FXML resource path.
   * @return a scene.
   */
  @NonNull
  public static Scene createScene(@NonNull FXMLLoader loader, @NonNull String fxmlPath) {
    Class<?> caller = walker.getCallerClass();

    try (InputStream inputStream = caller.getResourceAsStream(fxmlPath)) {
      if (inputStream == null) {
        throw new FileNotFoundException("No such resource: " + fxmlPath);
      }

      return new Scene(loader.load(inputStream));
    } catch (IOException ex) {
      throw new IllegalArgumentException("Failed to load FXML resource", ex);
    }
  }

  /**
   * @see #createWindow(FXMLLoader, String)
   */
  @NonNull
  public static Stage createWindow(@NonNull Injector injector, @NonNull String fxmlPath) {
    return createWindow(injector.getInstance(FXMLLoader.class), fxmlPath);
  }

  /**
   * Initializes a new completely independent window using the given loader and FXML path.
   *
   * @param loader an arbitrary FXML loader.
   * @param fxmlPath an FXML resource path.
   * @return an independent stage.
   */
  @NonNull
  public static Stage createWindow(@NonNull FXMLLoader loader, @NonNull String fxmlPath) {
    Stage stage = new Stage();
    stage.setScene(createScene(loader, fxmlPath));
    return stage;
  }
}
