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
package io.github.dotstart.helios.api.time;

import io.github.dotstart.helios.api.time.Timer.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <p>Provides test cases which evaluate whether the timer implementation operates within the
 * specified bounds.</p>
 *
 * <p>Due to the inaccurate nature of {@link Thread#sleep(long)}, we'll give the timer a 250 ms
 * grace period to operate in. While this kind of deviation is not acceptable within real world
 * scenarios, it is perfectly within the bounds of this test.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class NanoTimerTest {

  private static final long SLEEP_TOLERANCE = 250 * 1000000; // 2s

  /**
   * Evaluates whether the timer component correctly keeps track of the time even while paused.
   */
  @Test
  public void testTime() throws InterruptedException {
    var timer = new NanoTimer();
    Assertions.assertEquals(State.WAITING, timer.getState());
    Assertions.assertEquals(0, timer.getElapsedNanos());
    Assertions.assertEquals(0, timer.getTotalElapsedNanos());

    timer.start();
    Thread.sleep(10000);
    timer.pause();

    Assertions.assertEquals((double) 10000 * 1000000, timer.getElapsedNanos(), SLEEP_TOLERANCE);
    Assertions
        .assertEquals((double) 10000 * 1000000, timer.getTotalElapsedNanos(), SLEEP_TOLERANCE);
    Thread.sleep(10000);
    Assertions.assertEquals((double) 10000 * 1000000, timer.getElapsedNanos(), SLEEP_TOLERANCE);
    Assertions
        .assertEquals((double) 20000 * 1000000, timer.getTotalElapsedNanos(), SLEEP_TOLERANCE);

    timer.unpause();
    Assertions.assertEquals((double) 10000 * 1000000, timer.getElapsedNanos(), SLEEP_TOLERANCE);
    Assertions
        .assertEquals((double) 20000 * 1000000, timer.getTotalElapsedNanos(), SLEEP_TOLERANCE);

    Thread.sleep(10000);
    timer.stop();
    Assertions.assertEquals((double) 20000 * 1000000, timer.getElapsedNanos(), SLEEP_TOLERANCE);
    Assertions
        .assertEquals((double) 30000 * 1000000, timer.getTotalElapsedNanos(), SLEEP_TOLERANCE);
  }
}
