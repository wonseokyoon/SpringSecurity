package Spring.Controller.Login;

import Spring.Domain.Member.Member;
import Spring.Dto.Login.JoinDTO;
import Spring.Dto.ProfileResponse;
import Spring.Exception.BaseException;
import Spring.Exception.ErrorCode;
import Spring.Service.Login.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    // 회원 가입
    @PostMapping("/join")
    public ResponseEntity<ProfileResponse> signUp(@Valid @RequestBody JoinDTO dto) throws BaseException {
        // 중복 검사
        if(loginService.existByUsername(dto.getUsername())){
            throw new BaseException(ErrorCode.DUPLICATE_USERNAME);
        }
        Member member = loginService.join(dto);
        ProfileResponse response=new ProfileResponse(member.getUsername(),member.getRole().name());
        return ResponseEntity.ok(response);
    }

    // 관리자 생성
    @PostMapping("/joinAdmin")
    public ResponseEntity<ProfileResponse> joinAdmin(@Valid @RequestBody JoinDTO dto) throws BaseException {
        if(loginService.existByUsername(dto.getUsername())){
            throw new BaseException(ErrorCode.DUPLICATE_USERNAME);
        }
        Member member = loginService.joinAdmin(dto);
        ProfileResponse response=new ProfileResponse(member.getUsername(),member.getRole().name());
        return ResponseEntity.ok(response);
    }

}
