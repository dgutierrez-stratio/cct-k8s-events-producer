/* © 2019 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal
 * en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or
 * sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written
 * authorization from Stratio Big Data Inc., Sucursal en España. */

package com.stratio.cct.k8s.configuration;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

import com.stratio.cct.k8s.adapter.web.CustomResponseStatusExceptionResolver;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @PostConstruct
  public void setUp() {
    log.info("Web MVC configured");
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    exceptionResolvers.removeIf(r -> r instanceof ResponseStatusExceptionResolver);

    CustomResponseStatusExceptionResolver customResponseStatusExceptionResolver =
        new CustomResponseStatusExceptionResolver();
    customResponseStatusExceptionResolver.setMessageSource(applicationContext);

    exceptionResolvers.add(customResponseStatusExceptionResolver);
  }

}
