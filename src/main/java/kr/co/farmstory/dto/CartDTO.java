package kr.co.farmstory.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CartDTO {
    private int cartNo;
    private String uid;
}
