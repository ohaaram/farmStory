package kr.co.farmstory.controller;

import kr.co.farmstory.dto.CommentDTO;
import kr.co.farmstory.entity.Comment;
import kr.co.farmstory.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;
/*
    // 댓글 목록 조회
    @GetMapping("/comment/{ano}")
    public ResponseEntity<List<CommentDTO>> commentList(@PathVariable("ano") int ano){
        log.info("commentList : "+ano);
        return commentService.selectComments(ano);
    }
 */
    // 댓글 작성
    @PostMapping("/comment")
    public ResponseEntity<Comment> commentWrite(@RequestBody CommentDTO commentDTO) {
        log.info("commentWrite : " + commentDTO);

        ResponseEntity<Comment> commentResponseEntity = commentService.insertComment(commentDTO);
        log.info("commentWrite ...2 : ");
        log.info(commentResponseEntity.getBody().toString());
        return commentResponseEntity;
    }
    // 댓글 삭제
    @DeleteMapping("/comment/{cno}")
    public ResponseEntity<?> deleteComment(@PathVariable("cno") int cno){
        return commentService.deleteComment(cno);
    }
    // 댓글 수정
    @PutMapping("/comment")
    public ResponseEntity<?> modifyComment(@RequestBody CommentDTO commentDTO){
        log.info("modifyComment : " +commentDTO.toString());
        return commentService.updateComment(commentDTO);
    }
}
