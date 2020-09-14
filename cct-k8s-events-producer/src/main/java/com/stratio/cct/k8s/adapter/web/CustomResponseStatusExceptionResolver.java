/* © 2019 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal
 * en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or
 * sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written
 * authorization from Stratio Big Data Inc., Sucursal en España. */

package com.stratio.cct.k8s.adapter.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomResponseStatusExceptionResolver extends ResponseStatusExceptionResolver {

  @Override
  protected void logException(Exception ex, HttpServletRequest request) {
    log.error(buildLogMessage(ex, request), ex);
  }

}
