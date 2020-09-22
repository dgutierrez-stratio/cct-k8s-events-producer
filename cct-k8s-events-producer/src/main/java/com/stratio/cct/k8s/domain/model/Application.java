package com.stratio.cct.k8s.domain.model;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
  private String id;
  private String name;
  private String status;
  private String healthy;
  private BigDecimal memory;
  private BigDecimal cpu;
  private BigDecimal disk;
  private BigDecimal gpu;
  private int tasks;
  private Map<String, String> labels;

  public Application(String id) {
    this.id = id;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof Application)) {
      return false;
    } else {
      Application other = (Application)o;
      if (!other.canEqual(this)) {
        return false;
      } else {
        Object thisId = this.getId();
        Object otherId = other.getId();
        if (thisId == null) {
          if (otherId != null) {
            return false;
          }
        } else if (!thisId.equals(otherId)) {
          return false;
        }
        return true;
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof Application;
  }

}
