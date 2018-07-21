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
package io.github.dotstart.helios.di.provider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.nio.charset.StandardCharsets;
import javafx.fxml.FXMLLoader;

/**
 * <p>Provides augmented FXMLLoader instances which rely on the injection framework in order to
 * construct their controllers.</p>
 *
 * <p>By default controllers constructed by JavaFX are required to provide a zero-args constructor
 * which permits the framework to construct instances and later initialize its FXML properties. This
 * is, however, far too limited for our purposes as we'll need consistent access to various
 * application components throughout the controller lifecycle.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class FXMLLoaderProvider implements Provider<FXMLLoader> {

  private final Injector injector;

  @Inject
  public FXMLLoaderProvider(@NonNull Injector injector) {
    this.injector = injector;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FXMLLoader get() {
    var loader = new FXMLLoader();
    loader.setCharset(StandardCharsets.UTF_8);
    loader.setControllerFactory(this.injector::getInstance);
    // TODO: Inject resource bundle
    return loader;
  }
}
