package kr.co.farmstory.repository.custom;

import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.PageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {

    // 댓글 목록 조회
    public List<Tuple> selectComments(int ano);
    public Tuple selectCommentAndNick(int ano);

}
