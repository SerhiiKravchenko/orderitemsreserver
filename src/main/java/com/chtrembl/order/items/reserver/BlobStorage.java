package com.chtrembl.order.items.reserver;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlobStorage implements Storage {

    private static final Logger log = LoggerFactory.getLogger(BlobStorage.class);

    @Override
    public void save(String name, InputStream inputStream) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                String connectionString = System.getenv("STORAGE_CONNECTION_STRING");
                BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();
                BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("orders");
                if (!blobContainerClient.exists()) {
                    blobContainerClient.create();
                }
                BlobClient blobClient = blobContainerClient.getBlobClient(name);
                blobClient.upload(inputStream, true);
                break;
            } catch (Exception e) {
                attempts++;
                if (attempts == 3) {
                    throw new RuntimeException("Failed after 3 attempts to upload", e);
                }
            }
        }
    }
}
