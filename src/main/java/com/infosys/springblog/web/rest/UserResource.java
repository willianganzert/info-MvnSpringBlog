package com.infosys.springblog.web.rest;

import com.infosys.springblog.domain.User;
import com.infosys.springblog.repository.UserRepository;
import com.infosys.springblog.security.SecurityUtils;
import com.infosys.springblog.service.DTO.UserDTO;
import com.infosys.springblog.service.DTO.UserPasswordDTO;
import com.infosys.springblog.service.UserService;
import com.infosys.springblog.web.rest.util.HeaderUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserResource {
    private static final String ENTITY_NAME = "user";




    private UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserPasswordDTO userPasswordDTO) throws URISyntaxException {
        if (userPasswordDTO.getId() != null) {
            return new ResponseEntity<>("error.http.999.user",HttpStatus.BAD_REQUEST);
        }
        else if(!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && (!SecurityUtils.getCurrentUserLogin().isPresent() || !userPasswordDTO.getLogin().equals(SecurityUtils.getCurrentUserLogin().get()))) {
            return new ResponseEntity<>("error.http.999.user.author.insert",HttpStatus.FORBIDDEN);
        }
        User result = userService.persist(userPasswordDTO.getUser());
        return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(new UserDTO(result));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, @PathVariable Long id) throws URISyntaxException {
        Optional<String> opLogin = SecurityUtils.getCurrentUserLogin();
        if(!opLogin.isPresent()) {
            return new ResponseEntity<>("error.http.403",HttpStatus.FORBIDDEN);
        }
        if (userDTO.getId() == null) {
            return new ResponseEntity<>("error.http.999.put",HttpStatus.BAD_REQUEST);
        }
        Optional<User> opDBUser = userService.findOne(userDTO.getId());
        if (!opDBUser.isPresent() || !opDBUser.get().getId().equals(id)) {
            return new ResponseEntity<>("error.http.999.put",HttpStatus.BAD_REQUEST);
        }
        if(!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN")) {
            if(!opDBUser.get().getLogin().equals(userDTO.getLogin())) {
                return new ResponseEntity<>("error.http.999.user.author.update_author",HttpStatus.FORBIDDEN);
            }
            if(!opDBUser.get().getLogin().equals(opLogin.get())) {
                return new ResponseEntity<>("error.http.999.user.author.update",HttpStatus.FORBIDDEN);
            }
        }
        Optional<UserDTO> optionalUserDTO= userUpdate(userDTO);
        return optionalUserDTO.map(response -> ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userDTO.getId().toString()))
                .body(optionalUserDTO.get()))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    Optional<UserDTO> userUpdate(UserDTO userDTO){
        return Optional.of(userService
                .findOne(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {
                    user.setLogin(userDTO.getLogin().toLowerCase());
                    user.setName(userDTO.getName());
                    userService.persist(user);
                    return user;
                })
                .map(UserDTO::new);
    }

    @GetMapping("/users")
    //public ResponseEntity<List<User>> getAllUsers(Pageable pageable) {
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll().stream().map(UserDTO::new).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findOne(id);
        return ResponseEntity.ok()
                .body(user.isPresent()?new UserDTO(user.get()):"{}");
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
