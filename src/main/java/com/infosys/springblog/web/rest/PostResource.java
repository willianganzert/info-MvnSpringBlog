package com.infosys.springblog.web.rest;

import com.infosys.springblog.domain.Post;
import com.infosys.springblog.repository.PostRepository;
import com.infosys.springblog.security.SecurityUtils;
import com.infosys.springblog.service.PostService;
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
public class PostResource {
    private static final String ENTITY_NAME = "post";

    private PostService postService;

    public PostResource(PostService postService) {
        if(postService == null){
            postService = new PostRepository();
        }
        this.postService = postService;
    }


    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody Post post) throws URISyntaxException {
        if (post.getId() != null) {
            return new ResponseEntity<>("error.http.999.post",HttpStatus.BAD_REQUEST);
        }
        else if(!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && (!SecurityUtils.getCurrentUserLogin().isPresent() || !post.getUser().getLogin().equals(SecurityUtils.getCurrentUserLogin().get()))) {
            return new ResponseEntity<>("error.http.999.post.author.insert",HttpStatus.FORBIDDEN);
        }
        Post result = postService.persist(post);
        return ResponseEntity.created(new URI("/api/posts/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(@RequestBody Post post,@PathVariable Long id) throws URISyntaxException {
        Optional<String> opLogin = SecurityUtils.getCurrentUserLogin();
        if(!opLogin.isPresent()) {
            return new ResponseEntity<>("error.http.403",HttpStatus.FORBIDDEN);
        }
        if (id == null) {
            return new ResponseEntity<>("error.http.999.put",HttpStatus.BAD_REQUEST);
        }
        Optional<Post> opDBPost = postService.findOne(id);
        if (!opDBPost.isPresent() || !opDBPost.get().getId().equals(id)) {
            return new ResponseEntity<>("error.http.999.put",HttpStatus.BAD_REQUEST);
        }
        if(!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN")) {
            if(!opDBPost.get().getUser().getLogin().equals(post.getUser().getLogin())) {
                return new ResponseEntity<>("error.http.999.post.author.update_author",HttpStatus.FORBIDDEN);
            }
            if(!opDBPost.get().getUser().getLogin().equals(opLogin.get())) {
                return new ResponseEntity<>("error.http.999.post.author.update",HttpStatus.FORBIDDEN);
            }

        }
        Post result = postService.persist(post);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, id.toString()))
                .body(result);
    }

    @GetMapping("/posts")
    //public ResponseEntity<List<Post>> getAllPosts(Pageable pageable) {
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> postList = postService.findAll();
        return new ResponseEntity<>(postList, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        Optional<Post> post = postService.findOne(id);
        return ResponseEntity.ok()
                .body(post.isPresent()?post.get():"{}");
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        Optional<Post> post = postService.findOne(id);
        if(!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && (!SecurityUtils.getCurrentUserLogin().isPresent() || !post.isPresent()|| !post.get().getUser().getLogin().equals(SecurityUtils.getCurrentUserLogin().get()))) {
            return new ResponseEntity<>("error.http.999.post.author.delete",HttpStatus.FORBIDDEN);
        }
        postService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
