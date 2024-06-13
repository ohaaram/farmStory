package kr.co.farmstory.controller;

import kr.co.farmstory.dto.ProductDTO;
import kr.co.farmstory.dto.UserDTO;
import kr.co.farmstory.service.MarketService;
import kr.co.farmstory.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

    private final BuildProperties buildProperties;
    private final UserService userService;
    private final MarketService marketService;

    // 메인화면
    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {

        // 상단 BuildProperties 주입
        String appVersion = buildProperties.getVersion();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = null;

        log.info(appVersion);
        log.info("사용자가 로그인을 했는지 안했는지 띄워주기(MainController) : " + authentication);

        ////
        // product 테이블의 상품들 최신순으로 16개

        // marketService.selectProductsForMain();
        String cate = "";
        List<ProductDTO> productDTOS = marketService.selectProductsForMain(cate);
        model.addAttribute("productDTOS", productDTOS);


        /////

        model.addAttribute("appVersion", appVersion);

        if (authentication != null && authentication.isAuthenticated()&& !(authentication instanceof AnonymousAuthenticationToken)) {//로그인이 되었을 때

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                uid = ((UserDetails) principal).getUsername();
            } else {
                // 인증된 사용자가 UserDetails를 구현하지 않은 경우에 대한 처리
                uid = principal.toString();
            }

            log.info("uid 찍어보기 : "+uid);

            // 아이디값을 가진 사용자의 hp값을 들고오기
            UserDTO userDTO = userService.findById(uid);

            log.info("로그인한 사용자의 신상정보 : "+userDTO);

            if (userDTO.getHp() == null || userDTO.getHp().isEmpty()) {// 만약에 hp가 null이면 사용자 정보 수정 페이지로 이동

                model.addAttribute("userDTO", userDTO);

                return "/addInfo";

            }else if(userDTO.getRole().equals("delete")){

                log.info("탈퇴한 회원");

                return "/user/login";

            }else {// hp가 null이 아니면 기본 페이지 띄워주기

                return "/index";
            }
        }

        // 로그인을 하지 않았을 때에 대한 처리
        return "/index";
    }


    //소셜로그인 후 추가정보를 입력
    @PostMapping("/user/social")
    public String social(UserDTO userDTO) {

        log.info("userDTO값 방출1 : " + userDTO.getUid());
        log.info("userDTO값 방출2 : " + userDTO.getHp());
        log.info("userDTO값 방출3 : " + userDTO.getZip());


        log.info("추가정보 입력후 : " + userDTO);

        userService.social(userDTO);


        return "redirect:/index";
    }


    // 준비중 페이지를 위한 메서드 추가
    @GetMapping("/notfound")
    public String notFound() {
        // "notfound.html" 템플릿으로 리다이렉트
        return "/notfound";
    }

    // 준비중 페이지를 위한 메서드 추가1
    @GetMapping("/notfound1")
    public String notFound1() {
        // "notfound.html" 템플릿으로 리다이렉트
        return "/notfound1";
    }

    // 준비중 페이지를 위한 메서드 추가2
    @GetMapping("/notfound2")
    public String notFound2() {
        // "notfound.html" 템플릿으로 리다이렉트
        return "/notfound2";
    }

    // 팜스토리 소개
    @GetMapping("/introduction/newHello")
    public String hello() {

        return "/introduction/newHello";
    }

    // 찾아오는 길
    @GetMapping("/introduction/newDirection")
    public String direction() {

        return "/introduction/newDirection";
    }

    // 메인페이지 카테고리별 상품 조회 (16개씩)
    @GetMapping("/index/prodCate/{cate}")
    public ResponseEntity<?> selectProdForCate(@PathVariable("cate") String cate){
        List<ProductDTO> productDTOS = marketService.selectProductsForMain(cate);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("productDTOS", productDTOS);
        return ResponseEntity.ok().body(resultMap);
    }

    // 고객센터
    @GetMapping("/introduction/service")
    public String service() {

        return "/introduction/service";
    }
    
}
