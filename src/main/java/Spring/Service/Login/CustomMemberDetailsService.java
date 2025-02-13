package Spring.Service.Login;


import Spring.Dto.Login.CustomMemberDetails;
import Spring.Domain.Member.Member;
import Spring.Repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    public CustomMemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 유저 엔티티 데이터 조회
        Member member = memberRepository.findByUsername(username);

        // 사용자가 존재하지 않을 경우 예외 발생
        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 사용자 정보를 UserDetails로 변환하여 반환
        return new CustomMemberDetails(member);
    }
}
