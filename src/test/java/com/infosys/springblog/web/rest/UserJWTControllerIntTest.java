package com.infosys.springblog.web.rest;

import com.infosys.springblog.SpringBlogApp;
import com.infosys.springblog.domain.User;
import com.infosys.springblog.repository.UserRepository;
import com.infosys.springblog.security.jwt.TokenProvider;
import com.infosys.springblog.util.TestUtil;
import com.infosys.springblog.web.rest.errors.ExceptionTranslator;
import com.infosys.springblog.web.rest.vm.LoginVM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserJWTController REST controller.
 *
 * @see UserJWTController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBlogApp.class)
public class UserJWTControllerIntTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        UserJWTController userJWTController = new UserJWTController(tokenProvider, authenticationManager);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userJWTController)
            .setControllerAdvice(exceptionTranslator)
            .build();
    }

    @Test
//    @Transactional
    public void testAuthorize() throws Exception {
        User user = new User();
        user.setLogin("user-jwt-controller");
        user.setPassword("test");

        User userToBeDelete = userRepository.persist(user);

        LoginVM login = new LoginVM();
        login.setUsername("user-jwt-controller");
        login.setPassword("test");
        MockHttpServletRequestBuilder mk =  post("/api/authenticate");
        mockMvc.perform(mk
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(isEmptyString())));
        userRepository.delete(userToBeDelete.getId());
    }

    @Test
    @Transactional
    public void testAuthorizeFails() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("wrong-user");
        login.setPassword("wrong password");
        mockMvc.perform(post("/api/authenticate")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.id_token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"));
    }
}
