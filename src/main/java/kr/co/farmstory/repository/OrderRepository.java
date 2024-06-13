package kr.co.farmstory.repository;

import kr.co.farmstory.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer>{
    Orders findOrderNoByUid(String uid);
}
