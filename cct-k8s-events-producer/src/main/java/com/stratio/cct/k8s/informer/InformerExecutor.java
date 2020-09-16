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
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetList;
import io.fabric8.kubernetes.api.model.batch.CronJob;
import io.fabric8.kubernetes.api.model.batch.CronJobList;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
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
public class InformerExecutor implements SmartLifecycle {

  @NonNull
  private KubernetesClient kubernetesClient;

  private SharedInformerFactory sharedInformerFactory;

  @Override
  public void start() {
    log.info("start");
    sharedInformerFactory = kubernetesClient.informers();

    initializeInformer(Deployment.class, DeploymentList.class, "apps");
    initializeInformer(ReplicaSet.class, ReplicaSetList.class, "apps");
    initializeInformer(Job.class, JobList.class, "batch");
    initializeInformer(CronJob.class, CronJobList.class, "batch");

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

  private <T extends HasMetadata, L extends KubernetesResourceList<T>> SharedIndexInformer<T>
      initializeInformer(Class<T> typeClass, Class<L> typeListClass, String apiGroupName) {
    SharedIndexInformer<T> informer =
        sharedInformerFactory.sharedIndexInformerFor(typeClass, typeListClass,
            new OperationContext().withApiGroupName(apiGroupName), Long.MAX_VALUE);
    informer.addEventHandler(
        new ResourceEventHandler<T>() {
          @Override
          public void onAdd(T object) {
            log.info("{} {} added: {}", object.getMetadata().getName(), object.getKind(), object);
          }

          @Override
          public void onUpdate(T oldObject, T newObject) {
            log.info("{} {} updated: {}", oldObject.getMetadata().getName(), oldObject.getKind(), newObject);
          }

          @Override
          public void onDelete(T object, boolean deletedFinalStateUnknown) {
            log.info("{} {} deleted: {}", object.getMetadata().getName(), object.getKind(), object);
          }
        });
    return informer;
  }

  public static void main(String[] args) throws InterruptedException {
    Config config = new ConfigBuilder().build();
    KubernetesClient kubernetesClient = new DefaultKubernetesClient(config);
    InformerExecutor informerExecutor = new InformerExecutor(kubernetesClient);

    informerExecutor.start();

    Thread.sleep(10000);

    informerExecutor.stop();
  }
}
