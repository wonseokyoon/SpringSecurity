package Spring.Jwt;

import Spring.Dto.CustomMemberDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

//    public LoginFilter(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            Map<String,String> loginData=new ObjectMapper().readValue(request.getInputStream(), Map.class);
            //클라이언트 요청에서 username, password 추출
            String username = loginData.get("username");
            String password = loginData.get("password");
            System.out.println(username);

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            //이름,비밀번호,role
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("로그인 처리 중 오류 발생", e);
        }

    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

//        CustomMemberDetails memberDetails= (CustomMemberDetails) authentication.getPrincipal();
//        String username= memberDetails.getUsername();
//        String StringRole = memberDetails.getRole();
//        String role=authentication.getAuthorities().iterator().next().getAuthority();

        // 유저 확인
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role=auth.getAuthority();

        // 액세스 토큰 생성
        String access = jwtUtil.createJwt("access",username, role, 60 * 60 * 1000L); // 1시간 지속
        // 리프레시 토큰 생성
        String refresh = jwtUtil.createJwt("refresh",username, role, 60 * 60 * 24 * 86400000L); // 24시간 지속


        // JSON 형식으로 응답
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", access);
        tokens.put("refresh", refresh);

        // 응답 설정
        response.addCookie(createCookie("refresh", refresh));
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());

        new ObjectMapper().writeValue(response.getWriter(), tokens);
    }

    //로그인 실패시 실행하는 메소드

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try{
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"error\": \"다시 로그인하세요.\"}");
        }catch (IOException e){
            e.printStackTrace();
        }



    }

    // 쿠키 생성
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 1일
        cookie.setHttpOnly(true);
        return cookie;
    }
}
