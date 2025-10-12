package in.siddharthsingh.moneymanager.repository;

import in.siddharthsingh.moneymanager.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId); // <-- Add this
}
