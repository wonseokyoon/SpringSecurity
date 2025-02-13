package Spring.Service.Member;

import Spring.Dto.Login.CustomMemberDetails;
import Spring.Dto.ProfileResponse;
import Spring.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean existByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    public ProfileResponse getProfile(CustomMemberDetails memberDetails) {
        String username=memberDetails.getUsername();
        String role=memberDetails.getRole();

        return new ProfileResponse(username,role);
    }
}
