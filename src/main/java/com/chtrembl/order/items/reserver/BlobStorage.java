package com.chtrembl.order.items.reserver;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import java.io.InputStream;

public class BlobStorage implements Storage {

    @Override
    public void save(String name, InputStream inputStream) {
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
    }
}
