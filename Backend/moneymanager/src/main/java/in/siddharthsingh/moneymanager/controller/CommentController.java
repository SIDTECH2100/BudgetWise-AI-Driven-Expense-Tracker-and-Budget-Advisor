package in.siddharthsingh.moneymanager.controller;

import in.siddharthsingh.moneymanager.dto.CommentDTO;
import in.siddharthsingh.moneymanager.dto.CommentRequestDTO;
import in.siddharthsingh.moneymanager.entity.Comment;
import in.siddharthsingh.moneymanager.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Add a comment

    @PostMapping
    public CommentDTO addComment(@RequestParam Long id,
                                 @RequestParam Long postId,
                                 @RequestBody CommentRequestDTO request) {
        Comment comment = commentService.addComment(id, postId, request.getContent());
        return commentService.mapToDTO(comment);
    }

//    @PostMapping
//    public CommentDTO addComment(@RequestParam Long id,
//                                 @RequestParam Long postId,
//                                 @RequestBody String content) {
//        Comment comment = commentService.addComment(id, postId, content);
//        return commentService.mapToDTO(comment); // map entity to DTO before returning
//    }

    // Get all comments for a post
    @GetMapping("/post/{postId}")
    public List<CommentDTO> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId); // already returns List<CommentDTO>
    }
}
