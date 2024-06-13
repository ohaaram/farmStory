package kr.co.farmstory.repository.custom;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.*;
import kr.co.farmstory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MarketRepositoryCustom {

    // market/list 페이지 products 조회
    public Page<Tuple> selectProducts(MarketPageRequestDTO marketPageRequestDTO, Pageable pageable);
    // market/view 페이지 product 조회
    public List<Tuple> selectProduct(int prodno);

    public Page<Tuple> findOrderListByUid(String userId, PageRequestDTO pageRequestDTO, Pageable pageable);

    // admin/order/list 페이지 조회
    public Page<Tuple> orderList(PageRequestDTO pageRequestDTO, Pageable pageable);

    // market/cart 페이지 cart_product 조회
    public List<Tuple> selectCartForMarket(String uid);

    // market/cart 페이지에서 market/order 넘어가면서 장바구니 count 변경
    public boolean modifyCount(int[] cart_prodNos, int[] counts);

    // market/cart 페이지에서 선택 상품 cart_prodNo 테이블에서 삭제
    public boolean deleteCart(int[] cart_prodNos);


    // main 페이지에서 띄울 상품 16개
    public List<Tuple> selectProductsForMain(String cate);

    // market/view에서 장바구니에 품목 추가
    public Integer addProductForCart(String uid, int prodno, int prodCount);
}