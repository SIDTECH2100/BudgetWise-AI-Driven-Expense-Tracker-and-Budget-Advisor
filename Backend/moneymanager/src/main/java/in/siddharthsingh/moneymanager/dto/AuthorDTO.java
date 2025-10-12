package in.siddharthsingh.moneymanager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDTO {
    private Long id;
    private String fullName;
}
