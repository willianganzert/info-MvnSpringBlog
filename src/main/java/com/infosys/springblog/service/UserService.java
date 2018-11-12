package com.infosys.springblog.service;

import com.infosys.springblog.domain.User;

import java.util.List;
import java.util.Optional;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;

public interface UserService {

    User persist(User post);
    List<User> findAll();
    //Page<User> findAll(Pageable pageable);

    Optional<User> findOne(Long id);

    User delete(Long id);
}
