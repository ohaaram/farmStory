package kr.co.farmstory.controller;

import kr.co.farmstory.dto.*;
import kr.co.farmstory.entity.User;
import kr.co.farmstory.dto.MarketPageRequestDTO;
import kr.co.farmstory.dto.MarketPageResponseDTO;
import kr.co.farmstory.dto.ProductDTO;
import kr.co.farmstory.entity.Product;
import kr.co.farmstory.service.AdminService;
import kr.co.farmstory.service.MarketService;
import kr.co.farmstory.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Console;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final AdminService adminService;
    private final MarketService marketService;
    private final UserService userService;

    // admin 메인 페이지
    @GetMapping("/admin/index")
    public String adminIndex(Model model,PageRequestDTO pageRequestDTO){

        OrderListResponseDTO orderListResponseDTO = null;

        //여기는 상품현황 시작
        log.info("AdminController - adminIndex-product : 들어옴");

        List<ProductDTO> products= adminService.products();

        log.info("AdminController - adminIndex : "+products);

        model.addAttribute("products",products);
        //상품현황 끝

        //여기는 회원현황 시작
        log.info("AdminController-adminIndex-User : 들어옴");

        List<UserDTO> users = userService.allUser();

        log.info("AdminController-adminIndex-User :"+users);

        model.addAttribute("users",users);
        //회원현황 끝

        //주문현황 시작
        log.info("AdminController - adminIndex-orderlist :  들어옴");



        //모든 주문조회 및 페이지 처리
        orderListResponseDTO = adminService.orderList(pageRequestDTO);

        log.info("AdminController - adminIndex-orderlist"+orderListResponseDTO);

        model.addAttribute(orderListResponseDTO);

        return "/admin/index";
    }


    ////ADMIN-PRODUCT////
    // admin 페이지 상품 목록
    @GetMapping("/admin/product/list")
    public String prodList(Model model, MarketPageRequestDTO marketPageRequestDTO) {
        MarketPageResponseDTO pageResponseDTO = marketService.selectProducts(marketPageRequestDTO);
        log.info("pageResponseDTO : " + pageResponseDTO.toString());
        model.addAttribute(pageResponseDTO);
        return "/admin/product/list";
    }

    // admin 페이지 상품 등록
    @GetMapping("/admin/product/register")
    public String prodRegister() {

        return "/admin/product/register";
    }

    // admin 페이지 상품 등록
    @PostMapping("/admin/product/register")
    public String prodRegister(ProductDTO productDTO,
                               @RequestParam("thumb120") MultipartFile thumb120,
                               @RequestParam("thumb240") MultipartFile thumb240,
                               @RequestParam("thumb750") MultipartFile thumb750) {
        LocalDateTime rdate = LocalDateTime.now();
        productDTO.setRdate(rdate);
        log.info("prodRegister");
        log.info("productDTO : " + productDTO.toString());
        log.info("thumb120 : " + thumb120);
        log.info("thumb240 : " + thumb240);
        log.info("thumb750 : " + thumb750);

        // 상품 등록 service
        adminService.productRegister(productDTO, thumb120, thumb240, thumb750);


        return "/admin/product/register";
    }

////ADMIN-User////
    // 사용자 목록
    @GetMapping("/admin/user/list")
    public String userList(Model model, PageRequestDTO pageRequestDTO) {
        List<UserDTO> userDTOList = userService.getUserList(pageRequestDTO);
        int total = userService.getTotalCount(pageRequestDTO);

        UserResponseDTO userResponseDTO = new UserResponseDTO(pageRequestDTO, userDTOList, total);
        model.addAttribute("userResponseDTO", userResponseDTO);

        return "/admin/user/list";
    }

    // 상세정보
    @GetMapping("/admin/user/view")
    public String userview(@RequestParam("uid") String uid, Model model){
        UserDTO userDTO = userService.getUserByUid(uid);//유저 정보를 가져옴
        log.info("userDTO :"+userDTO);
        model.addAttribute("user", userDTO);
        return "/admin/user/view";
    }

    // 등급, 권한 수정
    @PostMapping("/admin/user/update")
    @ResponseBody
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO){
        try {
            UserDTO userDTO1 = userService.findById(userDTO.getUid());
            if (userDTO1 != null) {
                userService.updateUser(userDTO);
                log.info("Updated UserDTO : " + userDTO);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "사용자 정보가 성공적으로 업데이트 되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "해당 사용자를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            log.error("Update Error", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "정보 업데이트 중 오류가 발생했습니다."));
        }
    }

    // 사용자 탈퇴
    @PostMapping("/admin/user/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@RequestParam("uid") String uid) {
        try {
            userService.deleteUser(uid);
            log.info("Deleted User: " + uid);
            return ResponseEntity.ok(Map.of("success", true, "message", "사용자가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("Delete Error", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "사용자 삭제 중 오류가 발생했습니다."));
        }
    }


    //주문자 리스트 불러오기
    @GetMapping("/admin/order/list")
    public String orderList(Model model,PageRequestDTO pageRequestDTO){

        OrderListResponseDTO orderListResponseDTO = null;

        //모든 주문조회 및 페이지 처리
        orderListResponseDTO = adminService.orderList(pageRequestDTO);

        log.info("controller - orderListResponseDTO : "+orderListResponseDTO);

        model.addAttribute(orderListResponseDTO);//주문정보 및 페이지 정보를 orderListResponseDTO에 넣어둠.

         return "/admin/order/list";
    }

}

