package com.mstruzek.azm.mapper.demo;

import com.mstruzek.azm.mapper.MapperFactory;

public class MapperFactoryDemo {

  public static void main(String[] args) {

    /*
     * Create on demand new type ProductMapper_Mapper.class
     * - create all necessary mappings between fields according to fieldNames getters ans setters.
     * - on a subsequent request load from ProductMapper class ClassLoader.
     */
    ProductMapper mapper = MapperFactory.getInstance(ProductMapper.class);

    Product product = new Product();
    product.setProdId(997);
    product.setProdName("cucumbers");
    product.setHartBeat(180.19);

    ProductDTO productDTO = mapper.toDto(product);

    assert productDTO != null;
    assert productDTO.getProdId().compareTo(product.getProdId()) == 0;
    assert productDTO.getProdName().compareTo(product.getProdName()) == 0;
    assert productDTO.getHartBeat().compareTo(product.getHartBeat()) == 0;
  }
}
