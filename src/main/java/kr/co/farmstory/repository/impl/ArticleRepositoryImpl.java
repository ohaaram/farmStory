package kr.co.farmstory.repository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.farmstory.dto.PageRequestDTO;
import kr.co.farmstory.entity.QArticle;
import kr.co.farmstory.entity.QUser;
import kr.co.farmstory.repository.custom.ArticleRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private QArticle qArticle = QArticle.article;
    private QUser qUser = QUser.user;

    // 기본 글 목록 조회
    @Override
    public Page<Tuple> selectArticles(PageRequestDTO pageRequestDTO, Pageable pageable){
        String cate = pageRequestDTO.getCate();

        long total = 0;
        // article 테이블과 User 테이블을 Join해서 article목록, 닉네임을 select
        QueryResults<Tuple> results = jpaQueryFactory
                .select(qArticle, qUser.nick)
                .from(qArticle)
                .where(qArticle.cate.eq(cate))
                .join(qUser)
                .on(qArticle.writer.eq(qUser.uid))
                .orderBy(qArticle.ano.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Tuple> content = results.getResults();
        total = jpaQueryFactory.selectFrom(qArticle).where(qArticle.cate.eq(cate)).fetchCount();

        // 페이지 처리용 page 객체 리턴
        return new PageImpl<>(content, pageable, total);
    }

    // 검색 글 목록 조회 동적 쿼리
    @Override
    public Page<Tuple> searchArticles(PageRequestDTO pageRequestDTO, Pageable pageable) {
        log.info("키워드 검색 impl : " + pageRequestDTO.getKeyword());
        String cate = pageRequestDTO.getCate();
        String type = pageRequestDTO.getType();
        String keyword = pageRequestDTO.getKeyword();

        BooleanExpression expression = null;

        // 검색 종류에 따른 where절 표현식 생성
        if(type.equals("title")){
            expression = qArticle.cate.eq(cate).and(qArticle.title.contains(keyword));
            log.info("제목 검색 : " + expression);

        }else if(type.equals("content")){
            expression = qArticle.cate.eq(cate).and(qArticle.content.contains(keyword));
            log.info("내용 검색 : " + expression);

        }else if(type.equals("title_content")){
            BooleanExpression titleContains = qArticle.cate.eq(cate).and(qArticle.title.contains(keyword));
            BooleanExpression contentContains = qArticle.cate.eq(cate).and(qArticle.content.contains(keyword));
            expression = qArticle.cate.eq(cate).and(titleContains).or(contentContains);
            log.info("제목+내용 검색 : " + expression);

        }else if(type.equals("writer")){
            expression = qArticle.cate.eq(cate).and(qUser.nick.contains(keyword));
            log.info("작성자 검색 : " + expression);
        }
        // select * from article where `cate`= ? and `type` contains(k) limit 0,10;
        QueryResults<Tuple> results = jpaQueryFactory
                .select(qArticle, qUser.nick)
                .from(qArticle)
                .join(qUser)
                .on(qArticle.writer.eq(qUser.uid))
                .where(expression)
                .orderBy(qArticle.ano.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        log.info("키워드 검색 5 "+results.getResults().toString());
        List<Tuple> content = results.getResults();
        log.info("키워드 검색 6 ");
        long total = results.getTotal();
        log.info("키워드 검색 7 ");
        // 페이지 처리용 page 객체 리턴
        return new PageImpl<>(content, pageable, total);
    }
    // 기본 글 상세 조회
    @Override
    public Tuple selectArticleAndNick(int ano){
        
        // user 조인 조회
        Tuple results = jpaQueryFactory
                .select(qArticle, qUser.nick)
                .from(qArticle)
                .where(qArticle.ano.eq(ano))
                .join(qUser)
                .on(qArticle.writer.eq(qUser.uid))
                .fetchOne();

        return results;
    }
}
