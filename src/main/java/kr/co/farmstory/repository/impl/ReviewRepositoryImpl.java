package kr.co.farmstory.repository.impl;


import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.farmstory.dto.ReviewPageRequestDTO;
import kr.co.farmstory.dto.ReviewRatioDTO;
import kr.co.farmstory.entity.QReview;
import kr.co.farmstory.entity.QUser;
import kr.co.farmstory.repository.custom.ReviewRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QReview qReview = QReview.review;
    private final QUser qUser = QUser.user;

    // market/newview 리뷰 목록 조회
    @Override
    public Page<Tuple> selectReviewsAndNick(int prodno, ReviewPageRequestDTO pageRequestDTO, Pageable pageable){
        // review 테이블과 User 테이블을 Join해서 리뷰 목록, 닉네임을 select
        QueryResults<Tuple> results = jpaQueryFactory
                .select(qReview, qUser.nick)
                .from(qReview)
                .where(qReview.prodno.eq(prodno))
                .join(qUser)
                .on(qReview.uid.eq(qUser.uid))
                .orderBy(qReview.rno.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        long total = results.getTotal();

        List<Tuple> reviews = results.getResults();

        // 페이지 처리용 page 객체 리턴
        return new PageImpl<>(reviews, pageable, total);
    }
    // market/newview 리뷰 avg, sum, count(*) 조회
    public Tuple selectForRatio(int prodno){

        Tuple result = jpaQueryFactory
                .select(qReview.count(),
                        qReview.score.avg(),
                        qReview.score.sum())
                .from(qReview)
                .where(qReview.prodno.eq(prodno))
                .fetchOne();
        return result;
    }
    // market/newview 리뷰 score별 count 조회
    public List<Tuple> selectScoreCount(int prodno){

        // SELECT score, COUNT(*) FROM review WHERE prodno = ? GROUP BY score ORDER BY score;
        List<Tuple> results = jpaQueryFactory
                .select(qReview.score ,qReview.count())
                .from(qReview)
                .where(qReview.prodno.eq(prodno))
                .groupBy(qReview.score)
                .orderBy(qReview.score.asc())
                .fetch();
        log.info("리뷰 score별 count 조회 : " + results);
        return results;
    }

}
