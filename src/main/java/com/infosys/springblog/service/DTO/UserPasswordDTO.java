package com.infosys.springblog.service.DTO;

//import com.infosys.blog.domain.Authority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infosys.springblog.domain.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A DTO representing a user
 */
public class UserPasswordDTO extends UserDTO{
    public UserPasswordDTO() {
    }

    private String password;


    public UserPasswordDTO(User user) {
        super(user);
        this.password = user.getPassword();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public User getUser() {
        User user = super.getUser();
        user.setPassword(password);
        return user;
    }
}
