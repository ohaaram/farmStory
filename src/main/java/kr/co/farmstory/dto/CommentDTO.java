package kr.co.farmstory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommentDTO {

    private int cno;
    private int ano;
    private String uid;
    private String content;
    private LocalDateTime rdate;

    private String nick;

}

