package com.influx.marcus.theatres.api.pref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Theatres {

  private List<City> city = null;
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public List<City> getCity() {
    return city;
  }

  public void setCity(List<City> city) {
    this.city = city;
  }

  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}
