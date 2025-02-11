package Spring.Service;


import Spring.Domain.Member;
import Spring.Domain.Role;
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
    public void join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        if(memberRepository.existsByUsername(username)) {
            throw new IllegalStateException("username is already in use");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(bCryptPasswordEncoder.encode(password));
        member.setRole(Role.ROLE_USER);

        memberRepository.save(member);
    }

    // 관리자 회원가입
    public void joinAdmin(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        if(memberRepository.existsByUsername(username)) {
            throw new IllegalStateException("username is already in use");
        }

        try {
            Member member = new Member();
            member.setUsername(username);
            member.setPassword(bCryptPasswordEncoder.encode(password));
            member.setRole(Role.ROLE_ADMIN);
            memberRepository.save(member);

        } catch (Exception e) {
            System.out.println("관리자 계정 생성 실패: "+e.getMessage());
            e.printStackTrace();
        }
    }



}
