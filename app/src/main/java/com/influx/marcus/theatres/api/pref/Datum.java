package com.influx.marcus.theatres.api.pref;

import java.util.HashMap;
import java.util.Map;

public class Datum {

  private String code;
  private String name;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private String zip;
  private Double lat;
  private Double _long;
  private Double miles;
  private String miles_str;
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Double getLong() {
    return _long;
  }

  public void setLong(Double _long) {
    this._long = _long;
  }

  public Double getMiles() {
    return miles;
  }

  public void setMiles(Double miles) {
    this.miles = miles;
  }

  public String getMiles_str() {
    return miles_str;
  }

  public void setMiles_str(String miles_str) {
    this.miles_str = miles_str;
  }

  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}