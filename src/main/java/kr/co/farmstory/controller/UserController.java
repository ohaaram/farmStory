package kr.co.farmstory.controller;


import kr.co.farmstory.dto.TermsDTO;
import kr.co.farmstory.entity.Terms;
import kr.co.farmstory.repository.TermsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.farmstory.dto.UserDTO;
import kr.co.farmstory.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    private final TermsRepository termsRepository;
    private final UserService userService;

    // 로그인 페이지 매핑
    @GetMapping("/user/login")
    public String loginPage() {
        return "/user/login";
    }


    // 아이디 찾기 페이지 매핑
    @GetMapping("/user/findId")
    public String FindIdPage() {
        return "/user/findId";
    }

    // 아이디 찾기
    @PostMapping("/user/findId")
    public String findId(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("code") String code, HttpSession session, Model model) {
        String sessionCode = (String) session.getAttribute("code");
        if (sessionCode != null && sessionCode.equals(code)) {
            // 인증 코드가 일치할 경우, 아이디 찾기 로직 실행
            String userId = userService.findUserIdByNameAndEmail(name, email);
            if (userId != null) {
                model.addAttribute("userId", userId);
                return "/user/findIdResult"; // 조회 결과를 findIdResult.html에 표시
            } else {
                model.addAttribute("error", "아이디를 찾을 수 없습니다. 입력 정보를 확인해 주세요.");
                return "/user/findId";
            }
        } else {
            model.addAttribute("error", "인증번호가 일치하지 않습니다.");
            return "/user/findId";
        }
    }

    // 아이디 찾기 결과
    @PostMapping("/user/findIdResult")
    public String findIdResult(@RequestParam String name, @RequestParam String email, Model model) {
        String userId = userService.findUserIdByNameAndEmail(name, email);
        if (userId != null) {
            model.addAttribute("userId", userId);
            return "/findIdResult";
        } else {
            model.addAttribute("error", "아이디를 찾을 수 없습니다. 입력 정보를 확인해 주세요.");
            return "/user/findId";
        }
    }

    // 비밀번호 찾기
    @PostMapping("/user/findPassword")
    public String findPassword(@RequestParam("uid") String uid, @RequestParam("email") String email, Model model) {
        // 사용자 인증 로직 (이메일로 인증 코드 발송 및 확인)
        // 인증 성공 시 비밀번호 변경 페이지로 이동
        return "redirect:/user/findPasswordChange";
    }

    // 비밀번호 찾기 완료 페이지
    @PostMapping("/user/changePassword")
    public String changePassword(@RequestParam("uid") String uid, @RequestParam("newPassword") String newPassword) {
        // 비밀번호 변경 로직
        userService.updateUserPassword(uid, newPassword);
        return "redirect:/user/login"; // 비밀번호 변경 후 로그인 페이지로 리다이렉트
    }

    // DB에서 약관정보 조회
    @GetMapping("/user/terms")
    public String terms(Model model) {

        Terms terms = termsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("약관 정보를 찾을 수 없습니다1."));

        TermsDTO termsDTO = TermsDTO.builder()
                .terms(terms.getTerms())
                .privacy(terms.getPrivacy())
                .build();

        model.addAttribute("termsDTO", termsDTO);

        return "/user/terms";
    }

    // 회원가입 페이지 매핑
    @GetMapping("/user/register")
    public String register() {

        return "/user/register";

    }

    //타입에 따라 db에 있는지 중복확인을 시켜줌.만약 type이 email이라면 이메일을 보내주는 역할
    @ResponseBody
    @GetMapping("/user/{type}/{value}")
    public ResponseEntity<?> checkUser(HttpSession session,
                                       @PathVariable("type") String type,
                                       @PathVariable("value") String value) {

        int count = userService.selectCountUser(type, value);

        log.info("count : " + count);

        if (type.equals("email") && count <= 0) {
            log.info("email : " + value);
            userService.sendEmailCode(session, value);
        }

        // Json 생성
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", count);

        return ResponseEntity.ok().body(resultMap);
    }

    // 이메일 인증 코드 검사
    @ResponseBody
    @GetMapping("/email/{code}")
    public ResponseEntity<?> checkEmail(HttpSession session, @PathVariable("code") String code) {

        String sessionCode = (String) session.getAttribute("code");

        if (sessionCode.equals(code)) {
            // Json 생성
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", true);

            return ResponseEntity.ok().body(resultMap);
        } else {
            // Json 생성
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", false);

            return ResponseEntity.ok().body(resultMap);
        }
    }

    // 회원가입 진행
    @PostMapping("/user/register")
    public String register(HttpServletRequest req, UserDTO userDTO) {

        String regip = req.getRemoteAddr();
        userDTO.setRegip(regip);

        log.info(userDTO.toString());

        userService.insertUser(userDTO);

        return "/user/login";
    }

    //role을 delete로 바꾸어 줌
    @GetMapping("/user/delete/{uid}")
    public ResponseEntity<?> deleteUser(@PathVariable("uid") String uid) {

        //여기서 uid를 받아서 role을 delete로 만들어주기(업댓)

        log.info("여기 들어오나요?");

        String role = "delete";

        log.info("uid : " + uid);


        userService.updateRole(uid, role);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", 1);

        log.info("result : " + resultMap);

        return ResponseEntity.ok().body(resultMap);
    }
}
