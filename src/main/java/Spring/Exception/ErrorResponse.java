package Spring.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    //private int status;         // HTTP 상태 코드
    private String errorCode;   // 에러 코드
    private String message;     // 에러 메시지
}
