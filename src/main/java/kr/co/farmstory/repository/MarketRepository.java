package kr.co.farmstory.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.farmstory.dto.OrderListDTO;
import kr.co.farmstory.entity.*;
import kr.co.farmstory.repository.custom.MarketRepositoryCustom;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketRepository extends JpaRepository<Product, Integer>, MarketRepositoryCustom {

}

