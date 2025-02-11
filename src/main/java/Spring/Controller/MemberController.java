package Spring.Controller;

import Spring.Domain.Member;
import Spring.Dto.CustomMemberDetails;
import Spring.Dto.JoinDTO;
import Spring.Dto.ProfileResponse;
import Spring.Exception.BaseException;
import Spring.Exception.ErrorCode;
import Spring.Service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

//     회원가입
//    @PostMapping("/join")
//    public String join(@Valid @RequestBody JoinDTO dto) {
//        memberService.join(dto);
//        return "success";
//    }
    // 회원 가입
    @PostMapping("/join")
    public ResponseEntity<ProfileResponse> signUp(@Valid @RequestBody JoinDTO dto) throws BaseException {
        // 중복 검사
        if(memberService.existByUsername(dto.getUsername())){
            throw new BaseException(ErrorCode.DUPLICATE_USERNAME);
        }
        Member member = memberService.join(dto);
        ProfileResponse response=new ProfileResponse(member.getUsername(),member.getRole().name());
        return ResponseEntity.ok(response);
    }

    // 관리자 생성
    @PostMapping("/joinAdmin")
    public ResponseEntity<ProfileResponse> joinAdmin(@Valid @RequestBody JoinDTO dto) throws BaseException {
        if(memberService.existByUsername(dto.getUsername())){
            throw new BaseException(ErrorCode.DUPLICATE_USERNAME);
        }
        Member member = memberService.join(dto);
        ProfileResponse response=new ProfileResponse(member.getUsername(),member.getRole().name());
        return ResponseEntity.ok(response);
    }

    // 프로필
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        if(memberDetails==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = memberDetails.getUsername();
        String role=memberDetails.getRole();    // 역할
//        String authorities=memberDetails.getAuthorities().iterator().next().getAuthority(); // 권한

//        ProfileResponse profileResponse = new ProfileResponse(username,role,authorities);
        ProfileResponse profileResponse = new ProfileResponse(username,role);
        return ResponseEntity.ok(profileResponse);
    }

    // 관리자 페이지
    @GetMapping("/admin")
    public ResponseEntity<ProfileResponse> adminPage(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        String username = memberDetails.getUsername();
        String role=memberDetails.getRole();
//        String authorities=memberDetails.getAuthorities().iterator().next().getAuthority();

        ProfileResponse profileResponse = new ProfileResponse(username,role);
        return ResponseEntity.ok(profileResponse);
    }





}
