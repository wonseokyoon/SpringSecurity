//package Spring.Controller;
//
//import Spring.Exception.BaseException;
//import Spring.Exception.ErrorCode;
//import Spring.Jwt.JWTUtil;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//public class ReissueController {
//    private final JWTUtil jwtUtil;
//
//    @PostMapping("/reissue")
//    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws BaseException {
//
//        //refresh 토큰
//        String refresh = null;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//
//            if (cookie.getName().equals("refresh")) {
//
//                refresh = cookie.getValue();
//            }
//        }
//
//        if (refresh == null) {
//            //response status code
//            throw new BaseException(ErrorCode.REFRESH_TOKEN_NULL);
//        }
//
//        //만료 체크
//        try {
//            jwtUtil.isExpired(refresh);
//        } catch (ExpiredJwtException e) {
//            throw new BaseException(ErrorCode.TOKEN_IS_EXPIRED);
//        }
//
//        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
//        String category = jwtUtil.getCategory(refresh);
//
//        if (!category.equals("refresh")) {
//
//            //response status code
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
//        }
//
//        String username = jwtUtil.getUsername(refresh);
//        String role = jwtUtil.getRole(refresh);
//
//        //make new JWT
//        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
//
//        //response
//        response.setHeader("access", newAccess);
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//
//
//}
