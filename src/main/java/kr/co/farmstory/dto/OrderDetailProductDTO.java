package kr.co.farmstory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderDetailProductDTO {
    private OrderDetailDTO orderDetailDTO;
    private String prodName;
    private int price;
    private int totalPrice;
    private LocalDateTime rdate;
}

