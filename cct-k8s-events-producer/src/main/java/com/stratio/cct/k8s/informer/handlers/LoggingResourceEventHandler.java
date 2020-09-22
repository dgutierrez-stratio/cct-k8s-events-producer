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

package com.stratio.cct.k8s.informer.handlers;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingResourceEventHandler<T extends HasMetadata> implements ResourceEventHandler<T> {

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

}
