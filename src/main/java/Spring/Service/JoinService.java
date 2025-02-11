package Spring.Service;


import Spring.Domain.Member;
import Spring.Dto.JoinDTO;
import Spring.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class JoinService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // 중복 확인
        Boolean isExist=memberRepository.existsByUsername(username);
        if(isExist){
            return;
        }

        // 생성
        Member member=new Member();
        member.setUsername(username);
        member.setPassword(bCryptPasswordEncoder.encode(password));
        // 역할 부여
        member.setRole("ROLE_ADMIN");

        memberRepository.save(member);
    }

}
