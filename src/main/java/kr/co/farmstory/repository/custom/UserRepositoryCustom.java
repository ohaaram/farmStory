package kr.co.farmstory.repository.custom;

import com.querydsl.core.Tuple;

public interface UserRepositoryCustom {

    public Tuple selectUserForOrder(String uid);
}
