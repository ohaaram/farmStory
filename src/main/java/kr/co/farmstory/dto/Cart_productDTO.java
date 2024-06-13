package kr.co.farmstory.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Cart_productDTO {
    private int cart_prodNo;
    private int count;
    private int cartNo;
    private int prodNo;

}
