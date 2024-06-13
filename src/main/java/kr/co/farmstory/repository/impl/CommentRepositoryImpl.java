package kr.co.farmstory.repository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.farmstory.entity.Comment;
import kr.co.farmstory.entity.QComment;
import kr.co.farmstory.entity.QUser;
import kr.co.farmstory.repository.custom.CommentRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.provider.QueryComment;
import org.springframework.stereotype.Repository;

import java.util.List;
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QComment qComment = QComment.comment;
    private QUser qUser = QUser.user;

    // 댓글 목록 조회
    public List<Tuple> selectComments(int ano){
        
        // SELECT comment.*,user.nick from comment join user on comment.uid = user.uid where comment.ano= ?;
        QueryResults<Tuple> results = jpaQueryFactory
                .select(qComment, qUser.nick)
                .from(qComment)
                .where(qComment.ano.eq(ano))
                .join(qUser)
                .on(qComment.uid.eq(qUser.uid))
                .orderBy(qComment.cno.desc())
                .fetchResults();
        // List<Tuple> 리턴
        return results.getResults();
    }
    // 댓글 작성 후 불러오기
    public Tuple selectCommentAndNick(int cno){

        Tuple results = jpaQueryFactory
                                .select(qComment, qUser.nick)
                                .from(qComment)
                                .where(qComment.cno.eq(cno))
                                .join(qUser)
                                .on(qComment.uid.eq(qUser.uid))
                                .fetchOne();

        return results;
    }
}
