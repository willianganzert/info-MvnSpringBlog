package com.infosys.springblog.web.rest;

import com.infosys.springblog.domain.User;
import com.infosys.springblog.repository.UserRepository;
import com.infosys.springblog.security.SecurityUtils;
import com.infosys.springblog.service.UserService;
import com.infosys.springblog.web.rest.util.HeaderUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserResource {
    private static final String ENTITY_NAME = "user";

    private UserService userService;

    public UserResource(UserService userService) {
        if(userService == null){
            userService = new UserRepository();
        }
        this.userService = userService;
    }


    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) throws URISyntaxException {
        if (user.getId() != null) {
            return new ResponseEntity<>("error.http.999.user",HttpStatus.BAD_REQUEST);
        }
        else if(!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && (!SecurityUtils.getCurrentUserLogin().isPresent() || !user.getLogin().equals(SecurityUtils.getCurrentUserLogin().get()))) {
            return new ResponseEntity<>("error.http.999.user.author.insert",HttpStatus.FORBIDDEN);
        }
        User result = userService.persist(user);
        return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody User user) throws URISyntaxException {
        Optional<String> opLogin = SecurityUtils.getCurrentUserLogin();
        if (user.getId() == null) {
            return new ResponseEntity<>("error.http.999.put",HttpStatus.BAD_REQUEST);
        }
        else if(!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN")) {
            Optional<User> opDBUser = userService.findOne(user.getId());
            if(!opLogin.isPresent()) {
                return new ResponseEntity<>("error.http.403",HttpStatus.FORBIDDEN);
            }
            if(!opDBUser.get().getLogin().equals(user.getLogin())) {
                return new ResponseEntity<>("error.http.999.user.author.update_author",HttpStatus.FORBIDDEN);
            }
            if(!opDBUser.get().getLogin().equals(opLogin.get())) {
                return new ResponseEntity<>("error.http.999.user.author.update",HttpStatus.FORBIDDEN);
            }


        }
        User result = userService.persist(user);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, user.getId().toString()))
                .body(result);
    }

    @GetMapping("/users")
    //public ResponseEntity<List<User>> getAllUsers(Pageable pageable) {
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userService.findAll();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findOne(id);
        return ResponseEntity.ok()
                .body(user.isPresent()?user.get():"{}");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findOne(id);
        if(!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && (!SecurityUtils.getCurrentUserLogin().isPresent() || !user.isPresent()|| !user.get().getLogin().equals(SecurityUtils.getCurrentUserLogin().get()))) {
            return new ResponseEntity<>("error.http.999.user.author.delete",HttpStatus.FORBIDDEN);
        }
        userService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
