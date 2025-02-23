package com.adithya.ws.ProductsMicroservice.rest;

import java.math.BigDecimal;

public class CreateProductRestModel {
	
	private String title;
	private BigDecimal price;
	private Integer quantity;
	
	public String getTitle() {
		return title;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}	
	
}
