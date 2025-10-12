package in.siddharthsingh.moneymanager.repository;

import in.siddharthsingh.moneymanager.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
