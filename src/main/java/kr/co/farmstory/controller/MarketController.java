package kr.co.farmstory.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import kr.co.farmstory.dto.*;
import jakarta.servlet.http.HttpSession;
import kr.co.farmstory.dto.MarketPageRequestDTO;
import kr.co.farmstory.dto.MarketPageResponseDTO;
import kr.co.farmstory.dto.ProductDTO;
import kr.co.farmstory.dto.UserDTO;
import kr.co.farmstory.entity.Product;
import kr.co.farmstory.service.MarketService;
import kr.co.farmstory.service.ReviewService;
import kr.co.farmstory.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.text.NumberFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MarketController {

    private final MarketService marketService;
    private final ReviewService reviewService;
    private final UserService userService;

    /*
        # 장보기 글목록 페이지 매핑 (cate, pg, type, keyword 받음)
          - cate : 상품 카테고리
          - pg : 페이지 넘버링 번호
          - type : 검색 조건 (상품은 prodName로 고정)
          - keyword : 검색어
     */
    @GetMapping("/market/newlist")
    public String marketList(Model model, MarketPageRequestDTO marketPageRequestDTO){
        MarketPageResponseDTO pageResponseDTO = marketService.selectProducts(marketPageRequestDTO);
        log.info("pageResponseDTO : " + pageResponseDTO.toString());
        model.addAttribute(pageResponseDTO);
        return "/market/newlist";
    }

    /*
        # 장보기 글보기 페이지 매핑 (prodno, cate, type, keyword 받음)
          - prodno : list에서 클릭한 상품의 번호
          - cate, type, keyword : 뒤로가기 버튼 클릭 시 보던 페이지를 유지하기 위함
          - prodno로 product에서 상품 정보 조회
          - pordno로 review에서 리뷰 정보 조회 후 ReviewPageResponseDTO에 저장
          - pordno로 review에서 별점(score)의 avg, sum, count(*), score별 count 조회 (별점평균, 그래프를 위해)
          - 상품 정보, 리뷰 정보, 리뷰 score model로 전송
     */
    @GetMapping("/market/newview")
    public String marketView(Model model, MarketPageRequestDTO marketPageRequestDTO, ReviewPageRequestDTO reviewPageRequestDTO, int prodno){
        // 상품 조회
        ProductDTO productDTO = marketService.selectProduct(prodno);
        // 리뷰 조회
        ReviewPageResponseDTO reviewPage = reviewService.selectReviews(prodno, reviewPageRequestDTO);
        // 리뷰 별점 - 평균, 비율 구하기
        ReviewRatioDTO reviewRatioDTO = reviewService.selectForRatio(prodno);

        model.addAttribute(productDTO);
        model.addAttribute(marketPageRequestDTO);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute(reviewRatioDTO);
        return "/market/newview";
    }

    /*
        # 주문목록 페이지 매핑 (uid, pg 받음)
          - uid로 orderList에서 주문 목록 조회
     */
    @GetMapping("/market/orderList")
    public String getOrderDetails(Model model, @RequestParam("uid") String uid, PageRequestDTO pageRequestDTO) {

        PageResponseDTO pageResponseDTO = marketService.findOrderListByUid(uid, pageRequestDTO);
        log.info("getOrderDetails Cont : " + pageResponseDTO);
        model.addAttribute("pageResponseDTO", pageResponseDTO);

        return "/market/orderList";
    }

    /*
        # 장바구니 목록 페이지 매핑 (uid 받음)
          - uid로 cart에서 cartNo 조회
          - 조회한 cartNo로 cart_product에서 장바구니 목록 조회
     */
    @GetMapping("/market/newcart")
    public String marketCart(Model model, String uid){

        List<ProductDTO> productDTO = marketService.selectCartForMarket(uid);
        model.addAttribute("productDTO", productDTO);
        return "/market/newcart";
    }

    /*
        # 장바구니에서 선택 상품 삭제 (int배열로 cart_prodNo 받음)
          - 삭제할 cart_prodNo의 값을 int배열로 받음
          - 해당 cart_prodNo 값으로 cart_product의 상품 목록 삭제
     */
    @PostMapping("/market/deleteCart")
    public ResponseEntity<?> deleteCart(@RequestBody Map<String, int[]> requestData){
        int[] cart_prodNos = requestData.get("cart_prodNo");
        log.info("controller-cart_prodNos : " + Arrays.toString(cart_prodNos));
        return marketService.deleteCart(cart_prodNos);
    }

    /*
        # 장바구니에서 수량 변경 반영 (int배열로 cart_prodNo와 count 받음)
          - cart_prodNo : 수량 변경할 cart_product의 번호
          - count : 변경할 수량
          - 장바구니에서 구매하기 클릭시 fetch로 이 메서드 호출
          - jsondata에 위의 int배열 2개 넣어서 body로 전달
          - 배열값 하나씩 풀어서 cart_product의 상품 수량 변경
     */
    @PostMapping("/market/modCount")
    public ResponseEntity<?> modifyCount(@RequestBody Map<String, int[]> requestData) {
        int[] cart_prodNos = requestData.get("cart_prodNo");
        int[] counts = requestData.get("count");
        log.info(Arrays.toString(cart_prodNos));
        log.info(Arrays.toString(counts));
        return marketService.modifyCount(cart_prodNos, counts);
    }

    /*
        # 주문하기 페이지 매핑 - 장바구니에서 주문 정보 받기 (cart_prodNo(사용 안함), uid 받음)
          - 위의 수량 변경 컨트롤러를 성공적으로 수행한 후 이 메서드 호출
          - cart_prodNo와 uid를 jsondata로 받아 옴
          - uid로 사용자 정보와 보유 포인트 조회
          - uid로 사용자의 장바구니 상품 목록 조회
          - 조회한 사용자 정보와 장바구니 상품 목록을 httpsession으로 저장
          - fetch로 조회한 내용을 다음 페이지로 넘기기 위해 잠시 httpsession에 저장 함
          - 해당 내용을 uri로 전달하기에는 보안에 민감한 내용을 포함하기 때문
          - 메서드 성공 여부를 ResponseEntity로 리턴
     */
    @PostMapping("/market/order")
    public ResponseEntity<?> marketOrder(HttpSession httpSession,
                                         @RequestBody Map<String, Object> requestMap) {
        // 요청 본문에서 uid와 cart_prodNo를 추출합니다.
        String uid = (String) requestMap.get("uid");
        List<Integer> cart_prodNoList = (List<Integer>) requestMap.get("cart_prodNo");

        // 주문자와 포인트 정보 가져오기
        UserDTO userDTO = userService.selectUserForOrder(uid);
        log.info("주문하기 페이지 Cont 1 : " + userDTO.toString());

        // 상품 정보 가져오기 - 장바구니 목록 불러오기와 같음
        List<ProductDTO> productDTO = marketService.selectCartForMarket(uid);
        log.info("주문하기 페이지 Cont 2 : " + productDTO.toString());

        // session에 데이터 저장
        httpSession.setAttribute("userDTO", userDTO);
        httpSession.setAttribute("productDTO", productDTO);

        Map<String, String> response = new HashMap<>();
        if (userDTO != null && productDTO != null){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /*
        # 주문하기 페이지 매핑 ()
          - 위의 두 메서드 (modifyCount, marketOrder)를 성공적으로 수행 후
          - window.location.href 로 이 메서드 호출
          - 현재 httpsession에 저장된 사용자 정보와 장바구니 목록을 불러옴
          - 불러온 데이터를 model에 담아 브라우저(주문/결제 페이지)로 전달
     */
    @GetMapping("/market/neworder")
    public String marketOrder(HttpSession httpSession, Model model){

        // Post(/market/order)에서 redirectAttributes 로 보낸 데이터 접근
        UserDTO userDTO = (UserDTO) httpSession.getAttribute("userDTO");
        List<ProductDTO> productDTOs = (List<ProductDTO>) httpSession.getAttribute("productDTO");

        log.info("marketOrder GET : " + productDTOs.toString());
        log.info("marketOrder GET : " + userDTO.toString());

        // View 출력을 위해 데이터 넘겨주기
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("productDTOs", productDTOs);
        return "/market/neworder";
    }

    // 이거 안쓰는거 같은데..
    @GetMapping("/product/details")
    public String getProductDetails(Model model) {
        Product product = new Product(); // 여기서 실제로는 제품 정보를 데이터베이스나 다른 소스에서 가져와야 합니다.
        product.setPrice((int) 123456.78); // 예시 가격 설정

        // 숫자 포맷팅
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        String formattedPrice = numberFormat.format(product.getPrice());

        // 모델에 포맷팅된 가격 추가
        model.addAttribute("formattedPrice", formattedPrice);
        return "productDetails"; // 뷰 이름 반환
    }

    /*
        # 머나먼 결제를 위한 여정 Ⅰ
        # 결제하기 버튼 클릭시 받는 사람의 정보를 저장하기 위한 메서드
          - recaddr, reciver, rechp, memo, payment, uid를 jsondata로 받아옴
          - 주문을 받는 사람의 정보를 orders에 저장
          - 저장 후 해당 orders의 orderNo값을 반환 (orderdetail을 만들기 위해)
     */
    @PostMapping("/market/orders")
    public ResponseEntity<?> orders(@RequestBody OrderDTO orderDTO){

        log.info("orderDTO : "+ orderDTO);
        int orderNo = marketService.orders(orderDTO);

        log.info("컨트롤러의 orderNo : " +orderNo);

        Map<String, String> response = new HashMap<>();
        response.put("orderNo",String.valueOf(orderNo));//orderNo를 반환
        return ResponseEntity.ok().body(response);
    }

    /*
        # 머나먼 결제를 위한 여정 Ⅱ
        # 포인트 차감 (uid와 point 받음)
          - uid : 주문한 사용자 = 포인트를 차감할 사용자
          - point : (보유포인트 - 사용한 포인트 + 적립할 포인트) - js에서 미리 계산해서 결과값만 가져옴
          - uid와 point를 이용해 account의 사용자 포인트를 업데이트
     */
    @GetMapping("/market/point/{uid}/{point}")
    public ResponseEntity<?> addPoint(@PathVariable("uid")String uid,@PathVariable("point")int point){

        marketService.point(uid,point);

        Map<String, String> response = new HashMap<>();
        response.put("result","1");
        return ResponseEntity.ok().body(response);
    }

    /*
        # 머나먼 결제를 위한 여정 Ⅲ
        # 결제 후 장바구니에 있는 상품들을 삭제 ()
          - productList Map List에 quantity, prodNo 받아옴
          - quantity : 상품 개수 (사용 안함)
          - prodNo : 장바구니에서 삭제할 cart_product의 번호 (네이밍이 cart_prodNo가 맞음)
          - List안의 map에서 prodNo값을 꺼내 int배열로 변환
          - prodNo값으로 장바구니에서 구매한 상품들 삭제
     */
    @PostMapping("/market/cartProdDelete")
    public ResponseEntity<?> cartDelete(@RequestBody List<Map<String, Integer>> productList) {

        //productList에 담긴 값을 가져와서 int로 변환
        int[] prodNos = productList.stream()
                .mapToInt(map -> (map.get("prodNo")))
                .toArray();

        log.info("controller-cart_prodNos(내 컨트롤러) : " + Arrays.toString(prodNos));
        return marketService.deleteCart(prodNos);
    }

    /*
        # 머나먼 결제를 위한 여정 Ⅳ
        # 결제한 상품들 orderDetail에 저장 (counts, detailNos, orderNo 받음)
          - requestData Map로 counts, detailNos, orderNo 받아옴
          - counts : 구매한 상품 개수
          - detailNos : 구매한 상품의 번호 (prodNo)
          - orderNo : 여정 1 에서 반환한 orders의 orderNo값
          - 위 데이터들을 for문 반복을 통해 OrderDetailDTO에 저장한 후 orderdetail에 저장
          - 성공적으로 저장했다는 뜻으로 1 반환
          - 이후 자바스크립트로 orderList로 연결 시켜 줌
     */
    @PostMapping("/market/saveOrderDetail")
    public ResponseEntity<?> orderDetails(@RequestBody Map<String, Object> requestData) {
        String countsJson = (String) requestData.get("counts");
        String detailNosJson = (String) requestData.get("detailNos");
        String orderNoJson = (String) requestData.get("orderNo");

        System.out.println("counts: " + countsJson);
        System.out.println("detailNos: " + detailNosJson);
        System.out.println("orderNo: " + orderNoJson);

        List<String> counts = Arrays.asList(new Gson().fromJson(countsJson, String[].class));
        List<String> detailNos = Arrays.asList(new Gson().fromJson(detailNosJson, String[].class));
        String orderNo = new Gson().fromJson(orderNoJson, String.class);

        marketService.saveOrderDetails(counts, detailNos, orderNo);

        Map<String, String> response = new HashMap<>();
        response.put("result","1");
        return ResponseEntity.ok().body(response);
    }

    /*
        # 장바구니를 거치지 않고 view에서 order로 넘어가는 메서드 (uid, prodno, prodCount 받음)
          - 이미 만들어진 cart에서 order로 넘어가는 메서드를 재활용하기 위해 같은 값 전달
          - uid : 사용자 아이디
          - prodno : 구매할 상품 번호
          - prodCount : 구매할 상품 개수
          - uid로 주문자의 정보와 포인트 정보 조회
          - prodno로 구매할 상품의 정보 조회
          - 사용자의 정보, 구매할 상품의 정보와 수량을 httpsession에 저장 (marketOrder메서드와 같음)
          - 이후 자바스크립트에서 marketOrder 메서드로 이동
     */
    @PostMapping("/market/moveOrder")
    public ResponseEntity<?> moveOrder(HttpSession httpSession, @RequestBody Map<String, Object> requestMap) {

        String uid = (String) requestMap.get("uid");
        int prodno = Integer.parseInt((String) requestMap.get("prodno"));
        int prodCount = Integer.parseInt((String) requestMap.get("prodCount"));

        // 주문자와 포인트 정보 가져오기
        UserDTO userDTO = userService.selectUserForOrder(uid);

        List<ProductDTO> productDTO = new ArrayList<>();
        ProductDTO product = marketService.selectProduct(prodno);
        product.setCount(prodCount);
        productDTO.add(product);

        // session에 데이터 저장
        httpSession.setAttribute("userDTO", userDTO);
        httpSession.setAttribute("productDTO", productDTO);

        Map<String, String> response = new HashMap<>();
        if (userDTO != null && productDTO != null){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /*
        # market/view에서 장바구니에 품목 추가 (uid, prodno, prodCount 받음)
          - uid : 사용자 아이디
          - prodno : 구매할 상품 번호
          - prodCount : 구매할 상품 개수
          - prodno와 prodCount를 이용해 cart에 상품 저장
          - service단에서 uid와 prodno로 장바구니에서 중복되는 상품이 존재하는지 조회
          - 중복 상품이 존재하면 장바구니에 상품 추가 실패
     */
    @PostMapping("/market/addCart")
    public ResponseEntity<?> addCart(@RequestBody Map<String, Object> requestMap){

        String uid = (String) requestMap.get("uid");
        int prodno = Integer.parseInt((String) requestMap.get("prodno"));
        int prodCount = Integer.parseInt((String) requestMap.get("prodCount"));

        return marketService.addProductForCart(uid, prodno, prodCount);
    }
}