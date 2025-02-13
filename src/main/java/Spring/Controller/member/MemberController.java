package Spring.Controller.member;

import Spring.Dto.Login.CustomMemberDetails;
import Spring.Dto.ProfileResponse;
import Spring.Service.Member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 프로필(일반유저)
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal CustomMemberDetails memberDetails){
        ProfileResponse response= memberService.getProfile(memberDetails);
        return ResponseEntity.ok(response);
    }

    // 관리자 페이지
    @GetMapping("/admin")
    public ResponseEntity<ProfileResponse> adminPage(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        ProfileResponse response= memberService.getProfile(memberDetails);
    //    String authorities=memberDetails.getAuthorities().iterator().next().getAuthority();
        return ResponseEntity.ok(response);
    }

}

