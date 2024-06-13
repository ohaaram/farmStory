package kr.co.farmstory.repository.custom;


import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.ReviewPageRequestDTO;
import kr.co.farmstory.dto.ReviewRatioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewRepositoryCustom {

    public Page<Tuple> selectReviewsAndNick(int prodno, ReviewPageRequestDTO pageRequestDTO, Pageable pageable);

    public Tuple selectForRatio(int prodno);

    public List<Tuple> selectScoreCount(int prodno);
}
