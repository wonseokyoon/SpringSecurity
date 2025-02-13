package Spring.Service.Login;


import Spring.Domain.Member.Member;
import Spring.Domain.Member.Role;
import Spring.Dto.Login.JoinDTO;
import Spring.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

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

}
