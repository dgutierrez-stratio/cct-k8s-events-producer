/*
 * © 2019 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal
 * en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or
 * sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written
 * authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.cct.k8s;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

  private TenantResolverProperties tenantResolver = new TenantResolverProperties();

  @Data
  public static class TenantResolverProperties {
    private TenantResolverStubProperties stub = new TenantResolverStubProperties();
  }

  @Data
  public static class TenantResolverStubProperties {
    private boolean enabled;
    private String defaultTenant = "NONE";
  }

}
