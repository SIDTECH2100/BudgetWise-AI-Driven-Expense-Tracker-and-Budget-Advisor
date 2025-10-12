package in.siddharthsingh.moneymanager.service;

import in.siddharthsingh.moneymanager.dto.AuthorDTO;
import in.siddharthsingh.moneymanager.dto.CommentDTO;
import in.siddharthsingh.moneymanager.dto.PostDTO;
import in.siddharthsingh.moneymanager.entity.Post;
import in.siddharthsingh.moneymanager.entity.ProfileEntity;
import in.siddharthsingh.moneymanager.repository.PostRepository;
import in.siddharthsingh.moneymanager.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;

    public PostService(PostRepository postRepository,
                       ProfileRepository profileRepository) {
        this.postRepository = postRepository;
        this.profileRepository = profileRepository;
    }

    // Create a post
    public Post createPost(Long id, String title, String content) {
        ProfileEntity author = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .title(title)
                .content(content)
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        return postRepository.save(post);
    }

    // Map Post entity to PostDTO
    public PostDTO mapToDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .author(AuthorDTO.builder()
                        .id(post.getAuthor().getId())
                        .fullName(post.getAuthor().getFullName())
                        .build())
                .comments(post.getComments() != null ?
                        post.getComments().stream()
                                .map(comment -> CommentDTO.builder()
                                        .id(comment.getId())
                                        .content(comment.getContent())
                                        .createdAt(comment.getCreatedAt())
                                        .postId(post.getId())
                                        .author(AuthorDTO.builder()
                                                .id(comment.getAuthor().getId())
                                                .fullName(comment.getAuthor().getFullName())
                                                .build())
                                        .build())
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    // Get all posts
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get post by ID
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToDTO(post);
    }
}
