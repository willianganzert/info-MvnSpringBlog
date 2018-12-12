package com.infosys.springblog.web.rest;

import com.infosys.springblog.SpringBlogApp;
import com.infosys.springblog.domain.Post;
import com.infosys.springblog.repository.PostRepository;
import com.infosys.springblog.util.TestUtil;
import com.infosys.springblog.web.rest.errors.ExceptionTranslator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBlogApp.class)
public class PostResourceTest {

    @Autowired
    private WebApplicationContext wac ;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private DispatcherServlet   dispatcherServlet;

    @Autowired
    private PostRepository dataRepository;


    private MockMvc mockMvc;
    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
    }

    @Test
    public void CorsUserServicesTest() throws Exception{
        this.mockMvc.perform(options("/api/posts")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .header(HttpHeaders.ORIGIN, "http://www.someurl.com")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setDispatchOptionsRequest(true);
        return dispatcherServlet;
    }

    @Test
    public void RestfulInsertCRUD() throws Exception {
        Post dataToBeInserted = new Post();
        dataToBeInserted.setTitle("POST-TO-BE-INSERTED");
        dataToBeInserted.setContent("POST-TO-BE-INSERTED");

        MockHttpServletRequestBuilder mk =  MockMvcRequestBuilders.post("/api/posts");
        MvcResult mvcResult = mockMvc.perform(mk
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dataToBeInserted)))
                .andReturn();
        Assert.assertEquals("Object Insertion should return the status ok.", mvcResult.getResponse().getStatus(), HttpStatus.CREATED.value());

        Post dataFromJson = TestUtil.convertJsonBytesToObject(mvcResult.getResponse().getContentAsByteArray(), Post.class);

        Optional<Post> optionalData = dataRepository.findOne(dataFromJson.getId());
        boolean dataInserted = optionalData.isPresent();
        if(dataInserted){
            dataRepository.delete(dataFromJson.getId());
        }
        Assert.assertTrue("The delete endpoint is not property deleting the data.",dataInserted);
    }
    @Test
    public void RestfulDeleteCRUD() throws Exception {
        Post post = new Post();
        post.setTitle("POST-TO-BE-DELETED");
        post.setContent("POST-TO-BE-DELETED");

        Post userToBeDelete = dataRepository.persist(post);

        MockHttpServletRequestBuilder mk =  delete("/api/posts/"+userToBeDelete.getId());
        mockMvc.perform(mk
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        Optional<Post> optionalPost = dataRepository.findOne(userToBeDelete.getId());
        boolean userDeletedForce = optionalPost.isPresent();
        if(userDeletedForce){
            dataRepository.delete(post.getId());
        }
        Assert.assertFalse("The delete endpoint is not property deleting the data.",userDeletedForce);
    }
}
