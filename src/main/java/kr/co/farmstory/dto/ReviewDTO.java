package kr.co.farmstory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ReviewDTO {
    private int rno;
    private String uid;
    private String comment;
    private int prodno;
    private int score;
    private String thumbnail;

    private LocalDateTime rdate;

    // 리뷰 조회용
    private String nick;
}
