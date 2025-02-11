package Spring.Exception;

public enum ErrorCode {

    // 예외 등록

    USER_NOT_FOUND("유저를 찾을 수 없습니다."),
    POST_NOT_FOUND("게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS("권한이 없습니다."),
    DUPLICATE_USERNAME("이미 사용중인 이름입니다."),
    COMMENT_NOT_FOUND("댓글이 없습니다"),

    TOKEN_IS_EXPIRED("토큰이 만료됐습니다.")




    ;

    private final String message;
    ErrorCode(String message){
        this.message=message;
    }
    public String getMessage(){
        return message;
    }

}
