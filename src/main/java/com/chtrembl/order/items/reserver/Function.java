package com.chtrembl.order.items.reserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    @FunctionName("upload")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<Order>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        Order order = request.getBody().orElse(null);
        if (nonNull(order)) {
            BlobStorage blobStorage = new BlobStorage();
            ObjectMapper mapper = new ObjectMapper();
            String json = null;
            try {
                json = mapper.writeValueAsString(order);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            blobStorage.save(order.getId() + ".json", inputStream);
        }

        if (order == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(order.toString()).build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body(order).build();
        }
    }
}
