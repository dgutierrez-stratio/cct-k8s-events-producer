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

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.SmartLifecycle;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WatchersExecutor implements SmartLifecycle {

  @NonNull
  private KubernetesClient kubernetesClient;

  private List<Watch> watchers;

  @Override
  public void start() {
    log.info("start");

    watchers = new LinkedList<>();

    watchers.add(initializeDeploymentsWatcher());
  }

  @Override
  public void stop() {
    log.info("stop");
    watchers.forEach(Watch::close);
    watchers = null;
  }

  @Override
  public boolean isRunning() {
    return watchers != null;
  }

  private Watch initializeDeploymentsWatcher() {
    return kubernetesClient.apps().deployments().watch(new Watcher<Deployment>() {

      @Override
      public void eventReceived(Action action, Deployment resource) {
        log.info("{} {}: {}", action, resource.getKind(), resource);
      }

      @Override
      public void onClose(KubernetesClientException cause) {
        log.debug("Watcher onClose");
        if (cause != null) {
          log.error(cause.getMessage(), cause);
        }
      }

    });
  }
}
