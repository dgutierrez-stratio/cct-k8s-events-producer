/*
 * © 2019 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary
 * information of Stratio Big Data Inc., Sucursal en España and may not be
 * revealed, sold, transferred, modified, distributed or otherwise made
 * available, licensed or sublicensed to third parties; nor reverse engineered,
 * disassembled or decompiled, without express written authorization from
 * Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.cct.k8s.adapter.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden
public class CustomErrorController extends BasicErrorController {

  public CustomErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties,
      ObjectProvider<List<ErrorViewResolver>> errorViewResolversProvider) {
    super(errorAttributes, serverProperties.getError(), errorViewResolversProvider.getIfAvailable());
  }

  @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Override
  public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
    return super.error(request);
  }

}
