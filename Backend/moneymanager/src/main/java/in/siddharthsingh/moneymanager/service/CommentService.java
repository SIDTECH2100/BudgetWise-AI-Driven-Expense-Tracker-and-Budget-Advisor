package in.siddharthsingh.moneymanager.service;

import in.siddharthsingh.moneymanager.dto.AuthorDTO;
import in.siddharthsingh.moneymanager.dto.CommentDTO;
import in.siddharthsingh.moneymanager.entity.Comment;
import in.siddharthsingh.moneymanager.entity.Post;
import in.siddharthsingh.moneymanager.entity.ProfileEntity;
import in.siddharthsingh.moneymanager.repository.CommentRepository;
import in.siddharthsingh.moneymanager.repository.PostRepository;
import in.siddharthsingh.moneymanager.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          ProfileRepository profileRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.profileRepository = profileRepository;
    }

    // Add a comment
    public Comment addComment(Long id, Long postId, String content) {
        ProfileEntity user = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .author(user)
                .post(post)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    // Map Comment entity to DTO
    public CommentDTO mapToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .postId(comment.getPost().getId())
                .author(AuthorDTO.builder()
                        .id(comment.getAuthor().getId())
                        .fullName(comment.getAuthor().getFullName())
                        .build())
                .build();
    }

    // Get comments for a post
    public List<CommentDTO> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
