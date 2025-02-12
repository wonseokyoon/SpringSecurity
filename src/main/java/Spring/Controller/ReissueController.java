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
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    // refresh 토큰으로 access 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request,HttpServletResponse response) throws BaseException {

        //refresh 토큰
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
                break;
            }
        }

        if (refresh == null) {
            //response status code
            throw new BaseException(ErrorCode.REFRESH_TOKEN_NULL);
        }

        //만료 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new BaseException(ErrorCode.TOKEN_IS_EXPIRED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 저장되어있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if(!isExist) {  // 없으면
            return new ResponseEntity<>("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // 쿠키에 refresh 토큰 추가
        response.addCookie(createCookie("refresh",newRefresh));
        // 기존 refresh 삭제 후 새 refresh 생성 후 엔티티에 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username,newRefresh,86400000L);

        //response
        Map<String,String> responseBody=new LinkedHashMap<>();
        responseBody.put("new_Access",newAccess);
        responseBody.put("new_Refresh",newRefresh);


        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
