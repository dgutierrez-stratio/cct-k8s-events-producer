/*
 * © 2019 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal
 * en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or
 * sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written
 * authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.cct.k8s.configuration;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.instrument.async.TraceableScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

import com.stratio.cct.k8s.informer.InformerExecutor;
import com.stratio.cct.k8s.watcher.WatchersExecutor;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import okhttp3.OkHttpClient;

@Configuration
public class ApplicationConfig {

  @Bean
  public ResponseStatusExceptionResolver responseStatusExceptionResolver() {
    ResponseStatusExceptionResolver resolver = new ResponseStatusExceptionResolver();
    resolver.setWarnLogCategory(
        "org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver.warnLogger");
    return resolver;
  }

  @Bean
  public TraceableScheduledExecutorService cacheRefreshScheduledExecutorService(BeanFactory beanFactory) {
    return new TraceableScheduledExecutorService(beanFactory, Executors.newSingleThreadScheduledExecutor());
  }

  @Bean
  @ConditionalOnProperty(name = "application.api-client.type", havingValue = "STANDARD")
  public ApiClient standardApiClient() throws IOException {
    ApiClient apiClient = ClientBuilder.standard().build();
    OkHttpClient httpClient =
        apiClient.getHttpClient().newBuilder().readTimeout(0, TimeUnit.SECONDS).build();
    apiClient.setHttpClient(httpClient);
    return apiClient;
  }

  @Bean
  @ConditionalOnProperty(name = "application.api-client.type", havingValue = "CLUSTER", matchIfMissing = true)
  public ApiClient clusterApiClient() throws IOException {
    ApiClient apiClient = ClientBuilder.cluster().build();
    OkHttpClient httpClient =
        apiClient.getHttpClient().newBuilder().readTimeout(5, TimeUnit.SECONDS).build();
    apiClient.setHttpClient(httpClient);
    return apiClient;
  }

  @Bean
  @ConditionalOnProperty(name = "application.watcher.enabled", havingValue = "true", matchIfMissing = true)
  public WatchersExecutor wathersExecutor(ApiClient apiClient) {
    return new WatchersExecutor(apiClient);
  }

  @Bean
  @ConditionalOnProperty(name = "application.informer.enabled", havingValue = "true", matchIfMissing = true)
  public InformerExecutor informerExecutor(ApiClient apiClient) {
    return new InformerExecutor(apiClient);
  }

}
