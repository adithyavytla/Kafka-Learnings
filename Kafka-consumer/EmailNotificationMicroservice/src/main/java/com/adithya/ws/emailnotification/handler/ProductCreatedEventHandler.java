package com.adithya.ws.emailnotification.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.adithya.ws.core.ProductCreatedEvent;
import com.adithya.ws.emailnotification.error.NotRetryableException;
import com.adithya.ws.emailnotification.error.RetryableException;
import com.adithya.ws.emailnotification.io.ProcessedEventEntity;
import com.adithya.ws.emailnotification.io.ProcessedEventRepository;

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private RestTemplate restTemplate;
	private ProcessedEventRepository processedEventRepository;

	public ProductCreatedEventHandler(RestTemplate restTemplate, ProcessedEventRepository processedEventRepository) {
		this.restTemplate = restTemplate;
		this.processedEventRepository = processedEventRepository;
	}

	@KafkaHandler
	@Transactional
	public void handle(@Payload ProductCreatedEvent productCreatedEvent, @Header("messageId") String messageId,
			@Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

		LOGGER.info("Received a new event :" + productCreatedEvent.getTitle());
		
		//Check if the message is already processed before
		ProcessedEventEntity existingRecord = processedEventRepository.findByMessageId(messageId);
		
		if(existingRecord!=null)
		{
			LOGGER.info("Found a duplicate messageId : {}",existingRecord.getMessageId());
			return;
		}
		
		String requestUrl = "http://localhost:8082/response/200";
		try {
			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
			if (response.getStatusCode().value() == HttpStatus.OK.value()) {
				LOGGER.info("Recieved response from a remote service " + response.getBody());
			}
		} catch (ResourceAccessException ex) {
			LOGGER.info(ex.getMessage());
			throw new RetryableException(ex);
		} catch (HttpServerErrorException ex) {
			LOGGER.info(ex.getMessage());
			throw new NotRetryableException(ex);
		} catch (Exception ex) {
			LOGGER.info(ex.getMessage());
			throw new NotRetryableException(ex);
		}
		// Add a unique message id in the database table
		try {
			processedEventRepository.save(new ProcessedEventEntity(messageId, productCreatedEvent.getProductId()));
		} catch (DataIntegrityViolationException ex) {
			throw new NotRetryableException(ex);
		}
	}

}
