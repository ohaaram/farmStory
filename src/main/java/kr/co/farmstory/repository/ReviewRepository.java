package kr.co.farmstory.repository;

import kr.co.farmstory.entity.Review;
import kr.co.farmstory.repository.custom.ReviewRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, ReviewRepositoryCustom {
}
