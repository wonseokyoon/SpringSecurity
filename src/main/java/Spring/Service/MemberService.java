package Spring.Service;


import Spring.Domain.Member;
import Spring.Domain.Role;
import Spring.Dto.JoinDTO;
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

//    @Transactional
//    public void join(JoinDTO joinDTO) {
//        String username = joinDTO.getUsername();
//        String password = joinDTO.getPassword();
//
//        // 중복 확인
//        Boolean isExist=memberRepository.existsByUsername(username);
//        if(isExist){
//            return;
//        }
//
//        // 생성
//        Member member=new Member();
//        member.setUsername(username);
//        member.setPassword(bCryptPasswordEncoder.encode(password));
//        // 역할 부여
//        member.setRole("ROLE_ADMIN");
//
//        memberRepository.save(member);
//    }

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

    public Member findByUsername(String username) throws BaseException {
        if(memberRepository.findByUsername(username) == null) {
            throw new BaseException(ErrorCode.USER_NOT_FOUND);
        }
        return memberRepository.findByUsername(username);
    }

    public boolean existByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }
}
