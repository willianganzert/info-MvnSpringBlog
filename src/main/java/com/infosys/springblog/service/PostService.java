package com.infosys.springblog.service;

import com.infosys.springblog.domain.Post;

import java.util.List;
import java.util.Optional;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;

public interface PostService {

    Post persist(Post post);
    List<Post> findAll();
    //Page<Post> findAll(Pageable pageable);

    Optional<Post> findOne(Long id);

    Post delete(Long id);
}
