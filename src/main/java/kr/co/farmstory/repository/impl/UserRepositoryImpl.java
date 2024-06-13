package kr.co.farmstory.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.farmstory.entity.QAccount;
import kr.co.farmstory.entity.QUser;
import kr.co.farmstory.repository.custom.UserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private QUser qUser = QUser.user;
    private QAccount qAccount = QAccount.account;

    // 주문한 사용자와 포인트 조회
    @Override
    public Tuple selectUserForOrder(String uid){
        log.info("selectUserForOrder impl ...1 " + uid);
        // select user.*, account.point from user join account on user.uid = account.uid where uid=?;
        Tuple result = jpaQueryFactory
                .select(qUser, qAccount.point)
                .from(qUser)
                .where(qUser.uid.eq(uid))
                .join(qAccount)
                .on(qUser.uid.eq(qAccount.uid))
                .fetchOne();
        log.info("selectUserForOrder impl ...2 " + result.toString());
        return result;
    }
}
