package Spring.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinDTO {
    private String username;
    private String password;


}
