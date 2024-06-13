package kr.co.farmstory.repository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.farmstory.dto.*;
import kr.co.farmstory.entity.*;
import kr.co.farmstory.repository.custom.MarketRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MarketRepositoryImpl implements MarketRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QProduct qProduct = QProduct.product;
    private final QImages qImages = QImages.images;
    private final QOrders qOrders = QOrders.orders;
    private final QOrderDetail qOrderDetail = QOrderDetail.orderDetail;
    private final QUser qUser = QUser.user;
    private final QCart qCart = QCart.cart;
    private final QCart_product qCart_product = QCart_product.cart_product;

    // 장보기 게시판 목록 출력 (market/list)
    @Override
    public Page<Tuple> selectProducts(MarketPageRequestDTO marketPageRequestDTO, Pageable pageable) {

        QueryResults<Tuple> productList = null;
        long total = 0;
        if (marketPageRequestDTO.getKeyword() == null){
            if ((marketPageRequestDTO.getCate() == null || marketPageRequestDTO.getCate().isEmpty()) && marketPageRequestDTO.getType() == null) {
                // 1. cate값 없음 + keyword값 없음
                productList = jpaQueryFactory
                        .select(qProduct, qImages.thumb240)
                        .from(qProduct)
                        .join(qImages)
                        .on(qProduct.prodno.eq(qImages.prodno))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(qProduct.prodno.desc())
                        .fetchResults();

                total = jpaQueryFactory.selectFrom(qProduct).fetchCount();
            } else if (marketPageRequestDTO.getCate() != null && marketPageRequestDTO.getType() == null) {
                // 2. cate값 있음 + keyword값 없음
                productList = jpaQueryFactory
                        .select(qProduct, qImages.thumb240)
                        .from(qProduct)
                        .join(qImages)
                        .on(qProduct.prodno.eq(qImages.prodno))
                        .where(qProduct.cate.eq(marketPageRequestDTO.getCate()))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(qProduct.prodno.desc())
                        .fetchResults();

                total = jpaQueryFactory.selectFrom(qProduct).where(qProduct.cate.eq(marketPageRequestDTO.getCate())).fetchCount();
            }
        } else {
            if ((marketPageRequestDTO.getCate() == null || marketPageRequestDTO.getCate().isEmpty())) {
                // 3. cate값 없음 + keyword값 있음
                        productList = jpaQueryFactory
                        .select(qProduct, qImages.thumb240)
                        .from(qProduct)
                        .join(qImages)
                        .on(qProduct.prodno.eq(qImages.prodno))
                        .where(qProduct.prodname.contains(marketPageRequestDTO.getKeyword()))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(qProduct.prodno.desc())
                        .fetchResults();


                total = jpaQueryFactory.selectFrom(qProduct)
                        .where(qProduct.prodname.contains(marketPageRequestDTO.getKeyword())).fetchCount();
            }else {
                // 4. cate값 있음 + keyword값 있음
                productList = jpaQueryFactory
                        .select(qProduct, qImages.thumb240)
                        .from(qProduct)
                        .join(qImages)
                        .on(qProduct.prodno.eq(qImages.prodno))
                        .where(qProduct.cate.eq(marketPageRequestDTO.getCate()))
                        .where(qProduct.prodname.contains(marketPageRequestDTO.getKeyword()))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(qProduct.prodno.desc())
                        .fetchResults();

                total = jpaQueryFactory.selectFrom(qProduct).where(qProduct.cate.eq(marketPageRequestDTO.getCate()))
                        .where(qProduct.prodname.contains(marketPageRequestDTO.getKeyword())).fetchCount();
            }
        }

        List<Tuple> content = productList.getResults();
        log.info("content : " + content.toString());
        log.info("pageable : " + pageable);
        log.info("total : " + total);
        return new PageImpl<>(content, pageable, total);
    }


    // 장보기 게시판 게시글 출력 (market/view)
    @Override
    public List<Tuple> selectProduct(int prodno) {
        // select * from `product` as a join `images` as b on a.prodno = b.prodno where a`prodno` = ?
        List<Tuple> joinProduct = jpaQueryFactory
                .select(qProduct, qImages)
                .from(qProduct)
                .join(qImages)
                .on(qProduct.prodno.eq(qImages.prodno))
                .where(qProduct.prodno.eq(prodno))
                .fetch();

        log.info("results : " + joinProduct);
        return joinProduct;

    }

    ;

    // 주문 목록 조회
    @Override
    public Page<Tuple> findOrderListByUid(String uid, PageRequestDTO pageRequestDTO, Pageable pageable) {
        log.info("findOrderList Impl 1 : " + uid);

        // select *, product.prodname, qProduct.price , order.rdate form product join ~
        QueryResults<Tuple> results = jpaQueryFactory
                .select(qOrderDetail, qProduct.prodname, qProduct.price , qOrders.rdate)
                .from(qOrderDetail)
                .join(qOrders).on(qOrderDetail.orderNo.eq(qOrders.orderNo))
                .join(qProduct).on(qOrderDetail.prodno.eq(qProduct.prodno))
                .where(qOrders.uid.eq(uid))
                .orderBy(qOrderDetail.orderNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        log.info("findOrderList Impl 2 : " + results.toString());
        long total = results.getTotal();
        log.info("findOrderList Impl 3 : " + total);
        List<Tuple> orderList = results.getResults();
        // 페이지 처리용 page 객체 리턴
        return new PageImpl<>(orderList, pageable, total);
    }

    //사용자가 주문한 목록을 조회(admin - order - list)
    @Override
    public Page<Tuple> orderList(PageRequestDTO pageRequestDTO, Pageable pageable) {

        QueryResults<Tuple> results = jpaQueryFactory
                .select(
                        qOrders.orderNo,
                        qOrders.rdate,
                        qUser.name,
                        qOrderDetail.count,
                        qProduct.prodname,
                        qProduct.price,
                        qProduct.delCost,
                        qProduct.amount)
                .from(qUser)
                .join(qOrders).on(qUser.uid.eq(qOrders.uid))
                .join(qOrderDetail).on(qOrders.orderNo.eq(qOrderDetail.orderNo))
                .join(qProduct).on(qOrderDetail.prodno.eq(qProduct.prodno))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qOrders.orderNo.desc())
                .fetchResults();


        log.info("orderList - results : " + results.toString());

        List<Tuple> orderList = results.getResults();

        log.info("orderList - results :" + orderList);

        long total = results.getTotal();

        return new PageImpl<>(orderList, pageable, total);
    }

    // 장바구니 목록 출력
    @Override
    public List<Tuple> selectCartForMarket(String uid) {
        // select * from `cart` where uid = ?
        Integer cartNo = jpaQueryFactory
                .select(qCart.cartNo)
                .from(qCart)
                .where(qCart.uid.eq(uid))
                .fetchOne();
        log.info("selectCartForMarket1-cartNoList : " + cartNo);
        List<Tuple> productList = new ArrayList<>();
        // SELECT * FROM `cart_product` AS a  JOIN `product` AS b ON a.prodno = b.prodno WHERE `cartNo` = ?;
        if (cartNo != null) {
            productList = jpaQueryFactory
                    .select(qCart_product, qProduct)
                    .from(qCart_product)
                    .join(qProduct)
                    .on(qCart_product.prodNo.eq(qProduct.prodno))
                    .where(qCart_product.cartNo.eq(cartNo))
                    .fetch();
        }
        log.info("selectCartForMarket2-productList : " + productList.toString());
        return productList;
    }

    // 장바구니 count 변경
    @Transactional
    @Override
    public boolean modifyCount(int[] cart_prodNos, int[] counts) {
        try {
            for (int i = 0; i < cart_prodNos.length; i++) {
                long result = jpaQueryFactory
                        .update(qCart_product)
                        .set(qCart_product.count, counts[i])
                        .where(qCart_product.cart_prodNo.eq(cart_prodNos[i]))
                        .execute();
                // update 실패시 false 반환
                if (result == 0) {
                    return false;
                }
            }
            // for문의 update 모두 성공하면 ture 반환
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 장바구니에서 선택 상품 삭제
    @Transactional
    @Override
    public boolean deleteCart(int[] cart_prodNos) {
        try {
            for (int i = 0; i < cart_prodNos.length; i++) {
                long result = jpaQueryFactory
                        .delete(qCart_product)
                        .where(qCart_product.cart_prodNo.eq(cart_prodNos[i]))
                        .execute();
                // update 실패시 false 반환
                if (result == 0) {
                    return false;
                }
            }
            // for문의 update 모두 성공하면 ture 반환
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // main 페이지에서 띄울 상품 16개
    @Override
    public List<Tuple> selectProductsForMain(String cate){
        // select a.*, b.thumb240 form `product` as a join `images` as b on a.prodno = b.prodno where a.cate = ?? order by desc limit(0, 16);

        BooleanExpression expression = null;//where 조건을 만드는 표현객체

        QueryResults<Tuple> results = null;

        if (cate.isEmpty()) {
            results = jpaQueryFactory
                    .select(
                            qProduct,
                            qImages.thumb240)
                    .from(qProduct)
                    .join(qImages).on(qProduct.prodno.eq(qImages.prodno))
                    .orderBy(qProduct.prodno.desc())
                    .limit(16)
                    .fetchResults();
        }else {
            results = jpaQueryFactory
                    .select(
                            qProduct,
                            qImages.thumb240)
                    .from(qProduct)
                    .join(qImages).on(qProduct.prodno.eq(qImages.prodno))
                    .where(qProduct.cate.eq(cate))
                    .orderBy(qProduct.prodno.desc())
                    .limit(16)
                    .fetchResults();
        }



            // QUERYRESULT [ LIST [ TUPLE [ qProduct, qImages.thumb240 ] ] ]

            List<Tuple> orderList = results.getResults();
            return orderList;
    }


    // market/view에서 장바구니에 품목 추가
    @Override
    @Transactional
    public Integer addProductForCart(String uid, int prodno, int prodCount){
        Integer cartNo = jpaQueryFactory
                            .select(qCart.cartNo)
                            .from(qCart)
                            .where(qCart.uid.eq(uid))
                            .fetchOne();

        List<Cart_product> result = jpaQueryFactory
                                    .selectFrom(qCart_product)
                                    .where(qCart_product.cartNo.eq(cartNo).and(qCart_product.prodNo.eq(prodno)))
                                    .fetch();

        if (result.isEmpty()){
            return cartNo;
        }else {
            return -1; // 이미 존재하는 상품
        }
    }
}
