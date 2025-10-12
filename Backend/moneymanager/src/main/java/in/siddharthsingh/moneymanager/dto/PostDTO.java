package in.siddharthsingh.moneymanager.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private AuthorDTO author;
    private List<CommentDTO> comments; // optional, if you want to include post comments
}
