package com.mstruzek.azm.mapper.demo;

public class Product {

  public Integer prodId;
  public String prodName;
  public Double hartBeat;

  public Product() {
  }

  public String getProdName() {
    return prodName;
  }

  public void setProdName(String prodName) {
    this.prodName = prodName;
  }

  public Integer getProdId() {
    return prodId;
  }

  public void setProdId(Integer prodId) {
    this.prodId = prodId;
  }

  public Double getHartBeat() {
    return hartBeat;
  }

  public void setHartBeat(Double hartBeat) {
    this.hartBeat = hartBeat;
  }
}
