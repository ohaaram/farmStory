package kr.co.farmstory.service;

import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.*;
import kr.co.farmstory.dto.ImagesDTO;
import kr.co.farmstory.dto.MarketPageRequestDTO;
import kr.co.farmstory.dto.MarketPageResponseDTO;
import kr.co.farmstory.dto.ProductDTO;
import kr.co.farmstory.entity.*;
import kr.co.farmstory.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MarketService {

    private final MarketRepository marketRepository;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final Cart_productRepository cart_productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AccountRepository accountRepository;


    // 장보기 글목록 페이지 - 장보기 목록 출력
    public MarketPageResponseDTO selectProducts(MarketPageRequestDTO marketPageRequestDTO){
        log.info("selectProducts Service 1");
        Pageable pageable = marketPageRequestDTO.getPageable("no");

        log.info("selectProducts Service 2 pageable : " + pageable.toString());
        log.info("selectProducts Service 2 pageable : " + marketPageRequestDTO.toString());

        // select * from `product` order by no desc limit (0, 10) + 사진
        Page<Tuple> productList = marketRepository.selectProducts(marketPageRequestDTO, pageable);

        log.info("productList : " + productList.toString());

            List<ProductDTO> productDTOs = productList.getContent().stream()
                    .map(tuple -> {
                                Product product = tuple.get(0, Product.class);
                                String thumb240 = tuple.get(1, String.class);

                                ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                                productDTO.setTitleImg(thumb240);
                                return productDTO;
                            }
                    )
                    .toList();

        log.info("productDTO : " + productDTOs.toString());

        int total = (int) productList.getTotalElements();

        return MarketPageResponseDTO.builder()
                .pageRequestDTO(marketPageRequestDTO)
                .dtoList(productDTOs)
                .total(total)
                .build();
    }

    // 장보기 글보기 페이지 - 장보기 게시글 출력
    public ProductDTO selectProduct(int prodno){
        List<Tuple> joinProduct = marketRepository.selectProduct(prodno);
        // List에서 Product, Images 엔티티 꺼낸 후 ProductDTO로 병합
        ProductDTO joinProductDTO = joinProduct.stream()
                .map(tuple ->
                        {
                            Product product = tuple.get(0, Product.class);
                            Images images = tuple.get(1, Images.class);
                            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                            ImagesDTO imagesDTO = modelMapper.map(images, ImagesDTO.class);
                            productDTO.setTitleImg(imagesDTO.getThumb240());
                            productDTO.setContentImg(imagesDTO.getThumb750());
                            return productDTO;
                        }
                    )
                .findFirst()
                .orElse(null);
    return joinProductDTO;
    }

    // 주문 목록 조회
    public PageResponseDTO findOrderListByUid(String userId, PageRequestDTO pageRequestDTO) {

        log.info("findOrderListByUid Serv ...1");
        Pageable pageable = pageRequestDTO.getPageable("no");
        log.info("findOrderListByUid Serv ...2 " + pageable.toString());
        Page<Tuple> results = marketRepository.findOrderListByUid(userId, pageRequestDTO, pageable);

        // Page<Tuple>을 List<OrderDetailProductDTO>로 변환
        List<OrderDetailProductDTO> orderDetailList = results.getContent().stream()
                .map(tuple -> {
                // Tuple 에서 Entity GET
                OrderDetail orderDetail = tuple.get(0, OrderDetail.class);
                String prodName = tuple.get(1, String.class);
                Integer price = tuple.get(2, Integer.class);
                LocalDateTime orderDate = tuple.get(3, LocalDateTime.class);

                // 제품별 주문수량 * 가격
                int totalPrice = price * (orderDetail.getCount());
                log.info("findOrderListByUid Serv ...3 : " + totalPrice);

                // Entity -> DTO
                OrderDetailProductDTO dto = new OrderDetailProductDTO();
                OrderDetailDTO orderDetailDTO = modelMapper.map(orderDetail, OrderDetailDTO.class);

                // OrderDetailProductDTO에 데이터 입력
                dto.setOrderDetailDTO(orderDetailDTO);
                dto.setProdName(prodName);
                dto.setRdate(orderDate);
                dto.setPrice(price);
                dto.setTotalPrice(totalPrice);
                log.info("findOrderListByUid Serv ...4 : " + dto.toString());
                return dto;

        }).toList();
        log.info("findOrderListByUid Serv ...5 : " + orderDetailList.toString());

        // List<articleDTO>와 page 정보 리턴
        int total = (int) results.getTotalElements();
        PageResponseDTO pageResponseDTO = PageResponseDTO.builder()
                .pageRequestDTO(pageRequestDTO)
                .total(total)
                .build();
        pageResponseDTO.setOrderDetailList(orderDetailList);
        log.info("findOrderListByUid Serv ...6 : " + pageResponseDTO.toString());
        return pageResponseDTO;
    }

    // 장바구니 목록
    public List<ProductDTO> selectCartForMarket(String uid){
        log.info("marketCartService1");
        List<Tuple> qProductList = marketRepository.selectCartForMarket(uid);
        log.info("marketCartService2-qProductList : " + qProductList.toString());
        // 참조 List
        List<ProductDTO> productDTOs = qProductList.stream()
                .map(tuple ->
                    {
                        Cart_product cart_product = tuple.get(0, Cart_product.class);
                        Product product = tuple.get(1, Product.class);
                        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                        productDTO.setCount(cart_product.getCount());
                        productDTO.setCart_prodNo(cart_product.getCart_prodNo());
                        return productDTO;
                    }
                )
            .toList();
        log.info("marketCartService3-productDTOs : " + productDTOs.toString());
        return productDTOs;
    }

    // 장바구니 count 수정
    public ResponseEntity<?> modifyCount(int[] cart_prodNos, int[] counts){
        boolean result = marketRepository.modifyCount(cart_prodNos, counts);
        Map<String, String> response = new HashMap<>();
        if (result){
            response.put("data","수량 변경 성공");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else {
            response.put("data","수량 변경 실패");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 장바구니에서 선택 상품 삭제
    public ResponseEntity<?> deleteCart(int[] cart_prodNos){
        log.info("1" + Arrays.toString(cart_prodNos));
        boolean result = marketRepository.deleteCart(cart_prodNos);
        log.info("2" + result);
        Map<String, String> response = new HashMap<>();
        if (result){
            response.put("data","삭제 성공");
            log.info(response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else {
            response.put("data","삭제 실패");
            log.info(response.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //받는 사람 정보 + uid 를 orders에 저장
    public int orders(OrderDTO orderDTO){

        Orders orders = modelMapper.map(orderDTO, Orders.class);
        orders.setStatus("준비중");

        orderRepository.save(orders);

        log.info("orders"+orders.getOrderNo());

        return orders.getOrderNo();//orderNo를 반환한다.

    }

    //포인트 차감
    public void point(String uid, int usingPoint){

        Account account = accountRepository.findById(uid).orElse(null);

        log.info("원래 포인트 : "+account.getPoint());
        log.info("더하는 포인트 : " + usingPoint);

        if (account != null) {
            // 계정의 포인트를 업데이트
            log.info("account.getPoint()"+account.getPoint());
            account.setPoint(usingPoint);

            // 업데이트된 정보를 저장
            accountRepository.save(account);
        } else {
            // 사용자를 찾을 수 없는 경우 예외 처리
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

    }
/*
    //orderNo를 찾기위한 여정
    public int selectOrderNo(String uid){
        int orderNo = marketRepository.findOrderNo(uid);

        log.info("orderNo : "+orderNo);

        return orderNo;
    }

 */

    //결제한 상품 목록 orderDetail에 저장
    public void saveOrderDetails(List<String> counts, List<String> detailNos, String orderNo) {

        if (counts.size() != detailNos.size()) {
            throw new IllegalArgumentException("Counts and detailNos lists must have the same size.");
        }

        for (int i = 0; i < counts.size(); i++) {
            OrderDetailDTO orderDetail = new OrderDetailDTO();
            orderDetail.setCount(Integer.parseInt(counts.get(i)));
            orderDetail.setProdno(Integer.parseInt(detailNos.get(i)));
            orderDetail.setOrderNo(Integer.parseInt(orderNo));

            // 데이터베이스에 저장
            OrderDetail orderDetails = modelMapper.map(orderDetail, OrderDetail.class);
            orderDetailRepository.save(orderDetails);
        }
    }

    // 메인 페이지에서 띄울 상품들
    public List<ProductDTO> selectProductsForMain(String cate) {
        List<Tuple> qProduct = null;
        if (cate.equals("전체")) {
            qProduct = marketRepository.selectProductsForMain("");
        } else {
            qProduct = marketRepository.selectProductsForMain(cate);
        }

        List<ProductDTO> productDTOs = qProduct.stream()
                .map(tuple -> {
                            Product product = tuple.get(0, Product.class);
                            String thumb240 = tuple.get(1, String.class);

                            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                            productDTO.setTitleImg(thumb240);
                            return productDTO;
                        }
                )
                .toList();
        return productDTOs;
    }

    // market/view에서 장바구니에 품목 추가
    public ResponseEntity<?> addProductForCart(String uid, int prodno, int prodCount){
        Integer result = marketRepository.addProductForCart(uid, prodno, prodCount);
        Cart_product cartProduct = new Cart_product();
        Cart_product newCartProduct = new Cart_product();
        if (result > 0){
            cartProduct.setCartNo(result);
            cartProduct.setCount(prodCount);
            cartProduct.setProdNo(prodno);
            newCartProduct = cart_productRepository.save(cartProduct);
        }

        Map<String, String> response = new HashMap<>();
        if (newCartProduct.getCart_prodNo() != 0){
            response.put("data","추가 성공");
            log.info(response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else {
            response.put("data","추가 실패");
            log.info(response.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
}
