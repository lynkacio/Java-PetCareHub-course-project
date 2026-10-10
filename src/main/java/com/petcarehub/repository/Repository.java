package com.petcarehub.repository;

import java.io.IOException;
import java.util.List;

public interface Repository<T> {
    List<T> findAll() throws IOException;
    void saveAll(List<T> items) throws IOException;
}
