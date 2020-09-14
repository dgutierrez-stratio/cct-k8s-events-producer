/*
 * © 2020 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary
 * information of Stratio Big Data Inc., Sucursal en España and may not be
 * revealed, sold, transferred, modified, distributed or otherwise made
 * available, licensed or sublicensed to third parties; nor reverse engineered,
 * disassembled or decompiled, without express written authorization from
 * Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.cct.k8s.watcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.SmartLifecycle;

import io.kubernetes.client.openapi.ApiClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WatchersExecutor implements SmartLifecycle {

  @NonNull
  private ApiClient apiClient;

  private ScheduledExecutorService executor;

  @Override
  public void start() {
    log.info("start");
    executor = Executors.newScheduledThreadPool(4, r -> new Thread(r, "k8s-watcher-thread"));
    executor.scheduleAtFixedRate(new PodWatcher(apiClient), 0, 1, TimeUnit.SECONDS);
  }

  @Override
  public void stop() {
    log.info("stop");
    executor.shutdown();
    try {
      executor.awaitTermination(60, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public boolean isRunning() {
    return executor != null && !executor.isShutdown();
  }

}
