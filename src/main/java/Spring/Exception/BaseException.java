package Spring.Exception;

import lombok.Getter;

@Getter
public class BaseException extends Exception{
    private final ErrorCode errorCode;
    public BaseException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode=errorCode;
    }

}
