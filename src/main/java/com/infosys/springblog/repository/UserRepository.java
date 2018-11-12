package com.infosys.springblog.repository;

import com.infosys.springblog.domain.User;
import com.infosys.springblog.service.UserService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements UserService {

    public  List<User> findAll(){
        return TempRepoSingleton.getInstance().findAll(User.class);
    }

    public Optional<User> findOne(Long id){
        return TempRepoSingleton.getInstance().findOne(User.class,id);
    }
    public User persist(User one){
        return TempRepoSingleton.getInstance().persist(User.class,one);
    }

    public User delete(Long id){
        return TempRepoSingleton.getInstance().delete(User.class,id);
    }
}
