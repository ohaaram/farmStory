package kr.co.farmstory.repository;

import kr.co.farmstory.entity.Comment;
import kr.co.farmstory.repository.custom.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>, CommentRepositoryCustom {

    void deleteCommentByAno(int ano);
}
