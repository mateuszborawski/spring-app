package com.mateuszborawski.rest.webservices.restfulwebservices.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.mateuszborawski.rest.webservices.restfulwebservices.beans.User;
import com.mateuszborawski.rest.webservices.restfulwebservices.exceptions.UserNotFoundException;
import com.mateuszborawski.rest.webservices.restfulwebservices.services.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserDaoService service;

    @GetMapping(path = "/users")
    public MappingJacksonValue retrieveAllUsers() {
        MappingJacksonValue mapping = filterEntity(service.findAll(), "id", "name");
        return mapping;
    }

    @GetMapping(path = "/users-filter")
    public MappingJacksonValue retrieveAllUsersWithFiltering() {
        MappingJacksonValue mapping = filterEntity(service.findAll(), "name", "birthDate");
        return mapping;
    }

    @GetMapping(path = "/users/{id}")
    public MappingJacksonValue retrieveUser(@PathVariable int id) {
        User user = service.findOne(id);

        if(user == null) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }

        Link link = linkTo(methodOn(this.getClass()).retrieveAllUsers()).withRel("all-users");

        MappingJacksonValue mapping = filterEntity(EntityModel.of(user, link), "name", "birthDate");
        return mapping;
    }

    @PostMapping(path = "/users")
    public ResponseEntity createUser(@Valid @RequestBody User user) {
        User savedUser = service.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(path = "/users/{id}")
    public void deleteUser(@PathVariable int id) {
        User user = service.deleteById(id);

        if(user == null) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }
    }

    private MappingJacksonValue filterEntity(Object entity, String... propertyArray) {
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(propertyArray);
        FilterProvider filters = new SimpleFilterProvider().addFilter("UserFilter", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(entity);
        mapping.setFilters(filters);

        return mapping;
    }
}
