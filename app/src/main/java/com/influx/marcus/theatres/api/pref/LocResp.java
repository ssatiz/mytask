package com.influx.marcus.theatres.api.pref;

public class LocResp {
  public String getSTATUS() {
    return STATUS;
  }

  public void setSTATUS(String STATUS) {
    this.STATUS = STATUS;
  }

  String STATUS;

  public DataResp getDATA() {
    return DATA;
  }

  public void setDATA(DataResp DATA) {
    this.DATA = DATA;
  }

  DataResp DATA;

  public Theatres getTheatres() {
    return theatres;
  }

  public void setTheatres(Theatres theatres) {
    this.theatres = theatres;
  }

  Theatres theatres;
}
