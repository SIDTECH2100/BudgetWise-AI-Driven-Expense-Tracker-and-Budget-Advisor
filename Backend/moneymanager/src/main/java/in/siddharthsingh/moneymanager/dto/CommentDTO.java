package in.siddharthsingh.moneymanager.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private AuthorDTO author;
    private Long postId;
}
