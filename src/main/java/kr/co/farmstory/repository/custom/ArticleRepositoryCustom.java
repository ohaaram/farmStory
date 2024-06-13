package kr.co.farmstory.repository.custom;

import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.PageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {

    // 글 목록 조회
    public Page<Tuple> selectArticles(PageRequestDTO pageRequestDTO, Pageable pageable);

    // 검색 글 목록
    public Page<Tuple> searchArticles(PageRequestDTO pageRequestDTO, Pageable pageable);

    // 글 상세 조회
    public Tuple selectArticleAndNick(int ano);
}
