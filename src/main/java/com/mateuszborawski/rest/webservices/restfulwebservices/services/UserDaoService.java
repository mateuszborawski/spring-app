package com.mateuszborawski.rest.webservices.restfulwebservices.services;

import com.mateuszborawski.rest.webservices.restfulwebservices.beans.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class UserDaoService {
    private static List<User> users = new ArrayList<>();
    private static int id = 0;

    static {
        users.add(new User(++id, "Ania", LocalDate.parse("1993-04-01")));
        users.add(new User(++id, "Mati", LocalDate.parse("1993-10-01")));
        users.add(new User(++id, "Abi", LocalDate.parse("2017-04-30")));
    }

    public List<User> findAll() {
        return users;
    }

    public User save(User user) {
        if(user.getId() == null) {
            user.setId(++id);
        }
        users.add(user);

        return user;
    }

    public User findOne(int id) {
        for(User user : users) {
            if(user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public User deleteById(int id) {
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if(user.getId() == id) {
                iterator.remove();
                return user;
            }
        }
        return null;
    }
}
