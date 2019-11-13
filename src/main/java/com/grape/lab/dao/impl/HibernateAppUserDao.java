package com.grape.lab.dao.impl;

import com.grape.lab.dao.AppUserDao;
import com.grape.lab.model.AppUser;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public class HibernateAppUserDao implements AppUserDao {

    private SessionFactory sessionFactory;

    //language=HQL
    private final static String FIND_BY_LOGIN_QUERY =
            "from appuser where login = :login";

    @Override
    public Optional<AppUser> findByLogin(String login) {
        return sessionFactory.getCurrentSession()
                .createQuery(FIND_BY_LOGIN_QUERY, AppUser.class)
                .setParameter("login", login)
                .list()
                .stream()
                .findAny();
    }

    @Override
    public void save(AppUser user) {
        sessionFactory.getCurrentSession().save(user);
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
