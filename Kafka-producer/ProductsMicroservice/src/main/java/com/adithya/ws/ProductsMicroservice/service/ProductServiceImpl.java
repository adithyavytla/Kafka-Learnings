package com.adithya.ws.ProductsMicroservice.service;

import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.adithya.ws.ProductsMicroservice.rest.CreateProductRestModel;
import com.adithya.ws.core.ProductCreatedEvent;

@Service
public class ProductServiceImpl implements ProductService {
	
	KafkaTemplate<String,ProductCreatedEvent> kafkaTemplate;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public ProductServiceImpl(KafkaTemplate<String,ProductCreatedEvent> kafkaTemplate) {
		this.kafkaTemplate=kafkaTemplate;
	}

	@Override
	public String createProduct(CreateProductRestModel productRestModel) throws Exception {
		String productId = UUID.randomUUID().toString();
		ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId,
				productRestModel.getTitle(),
				productRestModel.getPrice(),
				productRestModel.getQuantity());
		//kafkaTemplate.send("product-created-events-topic",productId,productCreatedEvent);
		
		LOGGER.info("Before publishing a ProductCreatedEvent");
		
		ProducerRecord<String,ProductCreatedEvent> record = new ProducerRecord<>("product-created-events-topic",productId,productCreatedEvent);
		record.headers().add("messageId",UUID.randomUUID().toString().getBytes());
		SendResult<String,ProductCreatedEvent> result = kafkaTemplate.send(record).get();
		
		LOGGER.info("Partition: " + result.getRecordMetadata().partition());
		LOGGER.info("Topic: " + result.getRecordMetadata().topic());
		LOGGER.info("Offset: " + result.getRecordMetadata().offset());
		

		
		LOGGER.info("Returning product id");
		return productId;
	}

}
