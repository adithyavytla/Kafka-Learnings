package com.adithya.ws.emailnotification.io;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="processed-events")
public class ProcessedEventEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3372997157093563737L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable=false,unique=true)
	private String messageId;
	
	@Column(nullable=false)
	private String productId;

	
	
	public ProcessedEventEntity() {
		
	}

	public ProcessedEventEntity(String messageId, String productId) {
		this.messageId = messageId;
		this.productId = productId;
	}

	public long getId() {
		return id;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getProductId() {
		return productId;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	
}
