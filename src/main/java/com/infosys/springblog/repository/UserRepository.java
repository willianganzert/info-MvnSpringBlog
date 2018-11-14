package com.infosys.springblog.repository;

import com.infosys.springblog.domain.User;
import com.infosys.springblog.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements UserService {
    private BCryptPasswordEncoder passwordEncoder;
    public  UserRepository(){
        passwordEncoder = new BCryptPasswordEncoder();
    }
    public  List<User> findAll(){
        return TempRepoSingleton.getInstance().findAll(User.class);
    }

    public Optional<User> findOne(Long id){
        return TempRepoSingleton.getInstance().findOne(User.class,id);
    }
    public Optional<User> findOneByLogin(String login){
        Optional one = Optional.empty();
        List<User> users = TempRepoSingleton.getInstance().findAll(User.class);
        if(users.size() > 0){
            for(User user : users){
                if (user.getLogin().equals(login)){
                    return  Optional.of(user);
                }
            }
        }
        return one;

    }

    //FIXME FIX NULL POINTER EXCEPTION ON USER
    //findOneWithAuthoritiesByEmail
    public User persist(User one){
        if(one.getPassword()==null) throw new IllegalArgumentException("Password is required.");
        one.setPassword(passwordEncoder.encode(one.getPassword()));
        return TempRepoSingleton.getInstance().persist(User.class,one);
    }

    public User delete(Long id){
        return TempRepoSingleton.getInstance().delete(User.class,id);
    }
}
