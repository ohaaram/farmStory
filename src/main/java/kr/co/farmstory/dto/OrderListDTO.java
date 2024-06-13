package kr.co.farmstory.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderListDTO {

    private int orderNO;
    private LocalDateTime rdate;
    private String name;
    private int count;
    private String prodname;
    private int price;
    private int delCost;
    private int amount;
    private int sum;


}

