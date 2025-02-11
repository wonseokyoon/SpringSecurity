package Spring.Controller;

import Spring.Dto.CustomMemberDetails;
import Spring.Dto.ProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class MemberController {

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        if(memberDetails==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = memberDetails.getUsername();
        String role=memberDetails.getRole();    // 역할
        String authorities=memberDetails.getAuthorities().iterator().next().getAuthority(); // 권한

        ProfileResponse profileResponse = new ProfileResponse(username,role,authorities);
        return ResponseEntity.ok(profileResponse);
    }

    // 관리자 페이지
    @GetMapping("/admin")
    public ResponseEntity<ProfileResponse> adminPage(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        String username = memberDetails.getUsername();
        String role=memberDetails.getRole();
        String authorities=memberDetails.getAuthorities().iterator().next().getAuthority();

        ProfileResponse profileResponse = new ProfileResponse(username,role,authorities);
        return ResponseEntity.ok(profileResponse);
    }





}
