package kr.co.farmstory.repository;

import kr.co.farmstory.entity.User;
import kr.co.farmstory.repository.custom.UserRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {
    Page<User> findByRoleNot(String role, Pageable pageable);
}
