package com.mstruzek.azm.mapper.demo;

/**
 * Single abstract method - mapper method input/output
 */
public interface ProductMapper {

  ProductDTO toDto(Product product);

}
