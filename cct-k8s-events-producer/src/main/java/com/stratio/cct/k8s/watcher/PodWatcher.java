/// *
// * © 2020 Stratio Big Data Inc., Sucursal en España. All rights reserved.
// *
// * This software – including all its source code – contains proprietary
// * information of Stratio Big Data Inc., Sucursal en España and may not be
// * revealed, sold, transferred, modified, distributed or otherwise made
// * available, licensed or sublicensed to third parties; nor reverse engineered,
// * disassembled or decompiled, without express written authorization from
// * Stratio Big Data Inc., Sucursal en España.
// */
//
// package com.stratio.cct.k8s.watcher;
//
// import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
//
// import com.google.gson.reflect.TypeToken;
//
// import io.kubernetes.client.openapi.ApiClient;
// import io.kubernetes.client.openapi.apis.CoreV1Api;
// import io.kubernetes.client.openapi.models.V1PodList;
// import io.kubernetes.client.util.Watch;
// import lombok.NonNull;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @RequiredArgsConstructor
// @Slf4j
// public class PodWatcher implements Runnable {
//
// @NonNull
// private ApiClient apiClient;
//
// @Override
// public void run() {
// log.info("run");
// try {
// CoreV1Api coreV1Api = new CoreV1Api(apiClient);
// Watch<V1PodList> watch =
// Watch.createWatch(
// apiClient,
// coreV1Api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null),
// new TypeToken<Watch.Response<V1PodList>>() {
// }.getType());
//
// try {
// for (Watch.Response<V1PodList> item : watch) {
// log.info("{}: {}", item.type, ReflectionToStringBuilder.toString(item));
// }
// } finally {
// watch.close();
// }
// } catch (Exception e) {
// log.error(e.getMessage(), e);
// }
// }
//
// }
