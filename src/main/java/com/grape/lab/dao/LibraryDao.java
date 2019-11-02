package com.grape.lab.dao;

import com.grape.lab.model.Library;

import java.util.List;

public interface LibraryDao {
    void add(Library library);

    List<Library> findAll();

    void delete(Library item);
}
