package com.chtrembl.order.items.reserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.ServiceBusQueueTrigger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.nonNull;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    @FunctionName("upload")
    public void run(
            @ServiceBusQueueTrigger(
                    name = "message",
                    queueName = "orders",
                    connection = "SERVICE_BUS_CONNECTION_STRING") String message,
            final ExecutionContext context) {
        context.getLogger().info("Service Bus Trigger executed");


        if (nonNull(message)) {
            BlobStorage blobStorage = new BlobStorage();
            ObjectMapper mapper = new ObjectMapper();
            Order order = null;
            try {
                order = mapper.readValue(message, Order.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            InputStream inputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
            blobStorage.save(order.getId() + ".json", inputStream);
        }
    }
}
