package kr.co.farmstory.service;

import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.CommentDTO;
import kr.co.farmstory.entity.Comment;
import kr.co.farmstory.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    // 댓글 목록 조회
    public List<CommentDTO> selectComments(int ano){
        log.info("selectComments...1" + ano);
        // 조인 결과값 받아오기
        List<Tuple> results = commentRepository.selectComments(ano);
        log.info("selectComments...2");
        // DTO List로 변환
        List<CommentDTO> dtoList = results.stream()
                .map(tuple ->{
                    log.info("selectComments...3 tuple :" + tuple);
                    Comment comment = tuple.get(0, Comment.class);
                    String nick = tuple.get(1, String.class);
                    comment.setNick(nick);
                    log.info("selectComments...4" + comment);
                    return modelMapper.map(comment, CommentDTO.class);
                })
                .toList();

        return dtoList;
    }
    // 댓글 작성
    public ResponseEntity<Comment> insertComment(CommentDTO commentDTO){
        // DTO -> Entity
        Comment comment = modelMapper.map(commentDTO, Comment.class);
        log.info("insertComment : " + comment);

        // DB insert 후 저장한 객체 반환 //////////
        // DB insert 시 저장한 객체 Pk 반환
        int cno = commentRepository.save(comment).getCno();
        log.info("insertComment cno : " + cno);
        // user join 해서 nick 가져오기
        Tuple saveTuple =  commentRepository.selectCommentAndNick(cno);
        log.info("insertComment saveTuple : " + saveTuple.get(0, Comment.class));
        log.info("insertComment saveTuple : " + saveTuple.get(1, String.class));
        // tuple -> Entity
        Comment saveComment = saveTuple.get(0, Comment.class);
        String nick = saveTuple.get(1, String.class);
        saveComment.setNick(nick);

        log.info("insertComment saveComment : " + saveComment.toString());
        return ResponseEntity.ok().body(saveComment);
    }
    // 댓글 삭제
    public ResponseEntity<?> deleteComment(int cno) {

        log.info("deleteComment no :" + cno);

        Optional<Comment> optComment = commentRepository.findById(cno);

        log.info("deleteComment optArticle :" + optComment);

        // 댓글이 아직 있으면
        if(optComment.isPresent()){
            // 댓글 삭제
            commentRepository.deleteById(cno);
            return ResponseEntity.ok().body(optComment.get());
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
    }
    // 댓글 수정
    public ResponseEntity<?> updateComment(CommentDTO commentDTO){
        log.info("updateComment ...1 : " +commentDTO.toString());
        Optional<Comment> optComment = commentRepository.findById(commentDTO.getCno());

        log.info("updateComment ...2 optArticle :" + optComment);

        if(optComment.isPresent()){
            // 댓글 수정
            Comment comment = optComment.get();
            comment.setContent(commentDTO.getContent());

            log.info("updateComment ...3 : "+ comment);

            Comment modifiedComment = commentRepository.save(comment);
            log.info("updateComment ...4 : "+ modifiedComment);
            // 수정 후 데이터 반환
            return ResponseEntity.ok().body(modifiedComment);

        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
    }
}
