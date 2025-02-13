package Spring.Jwt;

import Spring.Domain.Member;
import Spring.Domain.Role;
import Spring.Dto.CustomMemberDetails;
import Spring.Repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    public JWTFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    protected String getToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }
        return authorization.substring(7);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");

        // Authorization 헤더가 null이거나 Bearer가 없으면 다음 필터로
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.substring(7);
        String category=jwtUtil.getCategory(token);

        if (category.equals("refresh")) {
            // 리프레시 토큰으로 접근 완료, 재발급 처리 등의 준비
            if (request.getRequestURI().equals("/reissue")) {
                // /reissue 요청을 처리
                filterChain.doFilter(request, response);
                return; // 리프레시 토큰으로 접근할 경우 필터 진행
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.print("refresh token cannot be used for access");
                return; // 접근 거부
            }
        }else if (category.equals("access")) {
            // 토큰 만료 검증
            try {
                jwtUtil.isExpired(token);
            } catch (ExpiredJwtException e) {

                //response body
                PrintWriter writer = response.getWriter();
                writer.print("access token expired");

                //response status code
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //토큰에서 username과 role 획득
            String username = jwtUtil.getUsername(token);
            String authority = jwtUtil.getRole(token);
            Role role=Role.valueOf(authority);

            //userEntity를 생성하여 값 set
            Member member = new Member();
            member.setUsername(username);
            member.setRole(role);

            //UserDetails에 회원 정보 객체 담기
            CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);

            //스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customMemberDetails, null, customMemberDetails.getAuthorities());

            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        }
    }
}