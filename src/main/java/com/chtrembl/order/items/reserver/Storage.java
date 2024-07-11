package com.chtrembl.order.items.reserver;

import java.io.InputStream;

public interface Storage {

    void save(String name, InputStream inputStream);
}
