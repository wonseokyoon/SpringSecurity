package Spring.Controller;

import Spring.Domain.RefreshEntity;
import Spring.Exception.BaseException;
import Spring.Exception.ErrorCode;
import Spring.Jwt.JWTUtil;
import Spring.Repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    // refresh 토큰으로 access 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws BaseException {

        // 쿠키 사용하여 리프레시 토큰 추출
        String refreshToken=null;
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("refresh")){
                    refreshToken = cookie.getValue();
                }
            }
        }

//        // Bearer 헤더에서 리프레시 토큰 추출
//        String refreshToken = request.getHeader("Authorization");
//        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
//            throw new BaseException(ErrorCode.REFRESH_TOKEN_NULL); // 예외 처리
//        }

//        refreshToken = refreshToken.substring(7); // Bearer 제거

        // 만료 체크
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new BaseException(ErrorCode.TOKEN_IS_EXPIRED);
        }

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {  // 없으면
            return new ResponseEntity<>("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 새로운 JWT를 생성
        // 액세스 토큰 생성
        String newAccess = jwtUtil.createJwt("access",username, role, 30 * 60 * 1000L); // 30분
        // 리프레시 토큰 생성
        String newRefresh = jwtUtil.createJwt("refresh",username, role, 7 * 24 * 60 * 60 * 1000L); // 1주일


        // 응답
        Map<String, String> responseBody = new LinkedHashMap<>();
        responseBody.put("new_Access", newAccess);
        responseBody.put("new_Refresh", newRefresh);
        // 쿠키에 리프레시 저장
        response.addCookie(createCookie("refresh", newRefresh));

        // 기존 refresh 삭제 후 새 refresh 생성 후 엔티티에 저장
        refreshRepository.deleteByRefresh(refreshToken);
        addRefreshEntity(username, newRefresh, 86400000L);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    // 쿠키 생성
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());
        refreshRepository.save(refreshEntity);
    }
}
