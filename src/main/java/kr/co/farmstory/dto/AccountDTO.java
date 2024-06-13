package kr.co.farmstory.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AccountDTO {

    private String uid;
    private int level;
    private int point;

}
