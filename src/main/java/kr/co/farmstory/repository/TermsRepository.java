package kr.co.farmstory.repository;

import kr.co.farmstory.entity.Terms;
import kr.co.farmstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsRepository extends JpaRepository<Terms, String> {
}
