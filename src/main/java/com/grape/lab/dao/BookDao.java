package com.grape.lab.dao;

import com.grape.lab.model.Book;

import java.util.List;

public interface BookDao {
    List<Book> findAll();

    void add(Book book);

    void delete(Integer id);

    List<Book> findByNameIgnoreCase(String name);
}
