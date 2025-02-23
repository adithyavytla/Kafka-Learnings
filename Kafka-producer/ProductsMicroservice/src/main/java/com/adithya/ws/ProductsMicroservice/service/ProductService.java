package com.adithya.ws.ProductsMicroservice.service;

import com.adithya.ws.ProductsMicroservice.rest.CreateProductRestModel;

public interface ProductService {

	String createProduct(CreateProductRestModel productRestModel) throws Exception;
	
}
