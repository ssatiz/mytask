package com.influx.marcus.theatres.api.pref;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataResp {
  private List<StateModel> states = null;
  private Theatres theatres;
  private List<String> genres = null;
  private List<String> languages = null;
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public List<StateModel> getStates() {
    return states;
  }

  public void setStates(List<StateModel> states) {
    this.states = states;
  }

  public Theatres getTheatres() {
    return theatres;
  }

  public void setTheatres(Theatres theatres) {
    this.theatres = theatres;
  }

  public List<String> getGenres() {
    return genres;
  }

  public void setGenres(List<String> genres) {
    this.genres = genres;
  }

  public List<String> getLanguages() {
    return languages;
  }

  public void setLanguages(List<String> languages) {
    this.languages = languages;
  }

  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }


}
