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

package com.stratio.cct.k8s.informer;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.context.SmartLifecycle;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.CallGeneratorParams;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InformerExecutor implements SmartLifecycle {

  @NonNull
  private ApiClient apiClient;

  private SharedInformerFactory factory;

  @Override
  public void start() {
    log.info("start");
    CoreV1Api coreV1Api = new CoreV1Api(apiClient);

    factory = new SharedInformerFactory();

    // Pod informer
    SharedIndexInformer<V1Pod> podInformer =
        factory.sharedIndexInformerFor(
            (CallGeneratorParams params) -> {
              try {
                return coreV1Api.listPodForAllNamespacesCall(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    params.resourceVersion,
                    params.timeoutSeconds,
                    params.watch,
                    null);
              } catch (ApiException e) {
                throw new RuntimeException(e);
              }
            },
            V1Pod.class,
            V1PodList.class);

    podInformer.addEventHandler(
        new ResourceEventHandler<V1Pod>() {
          @Override
          public void onAdd(V1Pod pod) {
            log.info("{} pod added!: {}", pod.getMetadata().getName(),
                ReflectionToStringBuilder.toString(pod));
          }

          @Override
          public void onUpdate(V1Pod oldPod, V1Pod newPod) {
            log.info(
                "{} => {} pod updated!: {}",
                oldPod.getMetadata().getName(), newPod.getMetadata().getName(),
                ReflectionToStringBuilder.toString(newPod));
          }

          @Override
          public void onDelete(V1Pod pod, boolean deletedFinalStateUnknown) {
            log.info("{} pod deleted!", pod.getMetadata().getName());
          }
        });

    factory.startAllRegisteredInformers();
  }

  @Override
  public void stop() {
    log.info("stop");
    factory.stopAllRegisteredInformers();
    factory = null;
  }

  @Override
  public boolean isRunning() {
    return factory != null;
  }

}
