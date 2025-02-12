package Spring.Service;


import Spring.Domain.Member;
import Spring.Domain.Role;
import Spring.Dto.CustomMemberDetails;
import Spring.Dto.JoinDTO;
import Spring.Dto.ProfileResponse;
import Spring.Exception.BaseException;
import Spring.Exception.ErrorCode;
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

    // 일반 회원가입
    public Member join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(bCryptPasswordEncoder.encode(password));
        member.setRole(Role.ROLE_USER);

        memberRepository.save(member);
        return member;
    }

    // 관리자 회원가입
    public Member joinAdmin(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(bCryptPasswordEncoder.encode(password));
        member.setRole(Role.ROLE_ADMIN);
        memberRepository.save(member);

        return member;
    }

    public boolean existByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    public ProfileResponse getProfile(CustomMemberDetails memberDetails) {
        String username=memberDetails.getUsername();
        String role=memberDetails.getRole();

        return new ProfileResponse(username,role);
    }
}
