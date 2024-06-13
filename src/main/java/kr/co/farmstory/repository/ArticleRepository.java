package kr.co.farmstory.repository;

import kr.co.farmstory.entity.Article;
import kr.co.farmstory.repository.custom.ArticleRepositoryCustom;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer>, ArticleRepositoryCustom {

    // 글 조회수 업
    @Modifying
    @Query("UPDATE Article a SET a.hit = a.hit + 1 WHERE a.ano = :ano")
    void incrementHitByAno(@Param("ano") int ano);
}
