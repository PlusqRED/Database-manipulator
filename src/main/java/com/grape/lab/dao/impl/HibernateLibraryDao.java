package com.grape.lab.dao.impl;

import com.grape.lab.dao.LibraryDao;
import com.grape.lab.model.Library;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class HibernateLibraryDao implements LibraryDao {

    //language=SQL
    private final static String FIND_ALL_QUERY = "from library";
    private SessionFactory sessionFactory;

    @Override
    public void add(Library library) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(library);
    }

    @Override
    public List<Library> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery(FIND_ALL_QUERY, Library.class)
                .getResultList();
    }

    @Override
    public void delete(Library item) {
        sessionFactory.getCurrentSession().delete(item);
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
