package Spring.Controller;

import Spring.Dto.JoinDTO;
import Spring.Service.JoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public String join(@Valid @RequestBody JoinDTO dto) {
        joinService.join(dto);
        return "success";
    }
}
