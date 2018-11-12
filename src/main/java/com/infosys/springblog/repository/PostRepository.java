package com.infosys.springblog.repository;

import com.infosys.springblog.domain.Post;
import com.infosys.springblog.service.PostService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository  implements PostService {

    public  List<Post> findAll(){
        return TempRepoSingleton.getInstance().findAll(Post.class);
    }

    public Optional<Post> findOne(Long id){
        return TempRepoSingleton.getInstance().findOne(Post.class,id);
    }
    public Post persist(Post one){
        return TempRepoSingleton.getInstance().persist(Post.class,one);
    }

    public Post delete(Long id){
        return TempRepoSingleton.getInstance().delete(Post.class,id);
    }
}
