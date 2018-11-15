package com.infosys.springblog.service.DTO;

//import com.infosys.blog.domain.Authority;
import com.infosys.springblog.domain.User;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.*;

/**
 * A DTO representing a user
 */
public class UserDTO {

    private Long id;

    @NotBlank
    @Pattern(regexp = "^[_.@A-Za-z0-9-]*$")
    @Size(min = 5, max = 254)
    private String login;

    @Size(max = 100)
    private String name;

//    private Set<String> authorities;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
        /*this.authorities = user.getAuthorities().stream()
                .map(Authority::getName)
                .collect(Collectors.toSet());*/
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "login='" + login + '\'' +
                ", name='" + name + '\'' +
                "}";
    }

    protected User getUser() {
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setName(name);
        return user;
    }
}
