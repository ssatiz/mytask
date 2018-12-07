package com.influx.marcus.theatres.api.pref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class City {

  private String cityname;
  private List<Datum> data = null;
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public String getCityname() {
    return cityname;
  }

  public void setCityname(String cityname) {
    this.cityname = cityname;
  }

  public List<Datum> getData() {
    return data;
  }

  public void setData(List<Datum> data) {
    this.data = data;
  }

  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}
