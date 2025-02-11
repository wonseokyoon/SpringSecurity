package Spring.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex){
       ErrorCode errorCode=ex.getErrorCode();
       String messaage=ex.getErrorCode().getMessage();
        ErrorResponse errorResponse=new ErrorResponse(
                errorCode.name(),
                messaage
        );

        return ResponseEntity.ok(errorResponse);
    }
}
