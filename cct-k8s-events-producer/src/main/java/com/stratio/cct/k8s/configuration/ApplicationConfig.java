/*
 * © 2019 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal
 * en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or
 * sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written
 * authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.cct.k8s.configuration;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.instrument.async.TraceableScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

import com.stratio.cct.k8s.informer.SimpleInformerExecutor;
import com.stratio.cct.k8s.informer.handlers.LoggingResourceEventHandler;
import com.stratio.cct.k8s.watcher.WatchersExecutor;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;

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
  public KubernetesClient kubernetesClient() {
    Config config = new ConfigBuilder().withNamespace(null).build();
    return new DefaultKubernetesClient(config);
  }

  @Bean
  @ConditionalOnProperty(name = "application.watcher.enabled", havingValue = "true", matchIfMissing = true)
  public WatchersExecutor wathersExecutor(KubernetesClient kubernetesClient) {
    return new WatchersExecutor(kubernetesClient);
  }

  @Bean
  public <T extends HasMetadata> LoggingResourceEventHandler<T> loggingResourceEventHandler() {
    return new LoggingResourceEventHandler<>();
  }

  @Bean
  @Conditional(DeploymentInformerExecutorEnabled.class)
  public SimpleInformerExecutor<Deployment, DeploymentList> deploymentsInformerExecutor(
      KubernetesClient kubernetesClient, ResourceEventHandler<Deployment> resourceEventHandler) {
    return new SimpleInformerExecutor<>(Deployment.class, DeploymentList.class, kubernetesClient, resourceEventHandler,
        "apps");
  }

  @Bean
  @Conditional(PodsInformerExecutorEnabled.class)
  public SimpleInformerExecutor<Pod, PodList> podsInformerExecutor(
      KubernetesClient kubernetesClient, ResourceEventHandler<Pod> resourceEventHandler) {
    return new SimpleInformerExecutor<>(Pod.class, PodList.class, kubernetesClient, resourceEventHandler, null);
  }

  public static class DeploymentInformerExecutorEnabled extends AllNestedConditions {

    public DeploymentInformerExecutorEnabled() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(name = "application.informer.enabled", havingValue = "true", matchIfMissing = true)
    static class InformerEnabled {
    }

    @ConditionalOnProperty(name = "application.informer.deployments.enabled", havingValue = "true",
        matchIfMissing = true)
    static class DeploymentsInformerEnabled {
    }
  }


  public static class PodsInformerExecutorEnabled extends AllNestedConditions {

    public PodsInformerExecutorEnabled() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(name = "application.informer.enabled", havingValue = "true", matchIfMissing = true)
    static class InformerEnabled {
    }

    @ConditionalOnProperty(name = "application.informer.pods.enabled", havingValue = "true",
        matchIfMissing = true)
    static class DeploymentsInformerEnabled {
    }
  }

}
