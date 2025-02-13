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
    private final RefreshRepository refreshRepository; // 리프레시 레포지토리 추가

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

        String authorization = request.getHeader("Authorization");

        // Authorization 헤더가 null이거나 Bearer가 없으면 다음 필터로
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7); // Bearer 제거
        String category = jwtUtil.getCategory(token); // 카테고리 확인

        // 카테고리에 따른 검증
        try {
            if (category.equals("access")) {
                // 액세스 토큰 만료 검증
                if (jwtUtil.isExpired(token)) {
                    // Access token 만료 메시지 응답
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    PrintWriter writer = response.getWriter();
                    writer.print("access token expired");
                    return;
                }

                // 토큰에서 사용자 정보 획득
                String username = jwtUtil.getUsername(token);
                String authority = jwtUtil.getRole(token);
                Role role = Role.valueOf(authority);

                Member member = new Member();
                member.setUsername(username);
                member.setRole(role);

                CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);
                Authentication authToken = new UsernamePasswordAuthenticationToken(customMemberDetails, null, customMemberDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } else if (category.equals("refresh")) {
                // 리프레시 토큰 만료 및 DB 검증
                if (jwtUtil.isExpired(token) || !refreshRepository.existsByRefresh(token)) {
                    // Refresh token 만료 메시지 응답
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    PrintWriter writer = response.getWriter();
                    writer.print("refresh token is invalid");
                    return;
                }
                // 리프레시 토큰이 유효할 경우 추가 처리를 하세요.
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        } catch (ExpiredJwtException e) {
            // 기본 오류 메시지
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.print("token expired");
            return;
        }

        filterChain.doFilter(request, response);
    }
}