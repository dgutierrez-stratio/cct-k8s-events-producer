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

import org.springframework.context.SmartLifecycle;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.OperationContext;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SimpleInformerExecutor<T extends HasMetadata, L extends KubernetesResourceList<T>>
    implements SmartLifecycle {

  @NonNull
  private final Class<T> apiTypeClass;

  @NonNull
  private final Class<L> apiListTypeClass;

  @NonNull
  private final KubernetesClient kubernetesClient;

  @NonNull
  private final ResourceEventHandler<T> resourceEventHandler;

  private final String apiGroupName;

  private SharedInformerFactory sharedInformerFactory;

  @Override
  public void start() {
    log.info("start");
    sharedInformerFactory = kubernetesClient.informers();

    initializeInformer();

    sharedInformerFactory.startAllRegisteredInformers();
  }

  @Override
  public void stop() {
    log.info("stop");
    sharedInformerFactory.stopAllRegisteredInformers();
    sharedInformerFactory = null;
  }

  @Override
  public boolean isRunning() {
    return sharedInformerFactory != null;
  }

  protected SharedIndexInformer<T> initializeInformer() {
    SharedIndexInformer<T> informer = sharedInformerFactory.sharedIndexInformerFor(apiTypeClass, apiListTypeClass,
        new OperationContext().withApiGroupName(apiGroupName), Long.MAX_VALUE);
    informer.addEventHandler(resourceEventHandler);
    return informer;
  }

}
