package com.grape.lab.dao.impl;

import com.grape.lab.dao.BookDao;
import com.grape.lab.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class HibernateBookDao implements BookDao {

    //language=SQL
    private final static String FIND_ALL_QUERY =
            "from book";
    //language=SQL
    private final static String FIND_BY_NAME_IGNORE_CASE_QUERY =
            "from book where lower(name) like :name";
    private SessionFactory sessionFactory;

    @Override
    public List<Book> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery(FIND_ALL_QUERY, Book.class)
                .getResultList();
    }

    @Override
    public void add(Book book) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(book);
    }

    @Override
    public void delete(Integer id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Book bookToDelete = currentSession.load(Book.class, id);
        currentSession.delete(bookToDelete);
    }

    @Override
    public List<Book> findByNameIgnoreCase(String name) {
        Session currentSession = sessionFactory.getCurrentSession();
        return currentSession.createQuery(FIND_BY_NAME_IGNORE_CASE_QUERY, Book.class)
                .setParameter("name", "%" + name.toLowerCase() + "%")
                .getResultList();
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
