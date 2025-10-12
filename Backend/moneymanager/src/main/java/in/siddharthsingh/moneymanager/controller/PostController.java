package in.siddharthsingh.moneymanager.controller;

import in.siddharthsingh.moneymanager.dto.PostDTO;
import in.siddharthsingh.moneymanager.entity.Post;
import in.siddharthsingh.moneymanager.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public PostDTO createPost(@RequestParam("id") Long id, @RequestBody Post post) {
        Post savedPost = postService.createPost(id, post.getTitle(), post.getContent());
        return postService.mapToDTO(savedPost);
    }

    @GetMapping
    public List<PostDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable Long id) {
        return postService.getPostById(id);
    }
}
