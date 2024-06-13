package kr.co.farmstory.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDTO {
    private int prodno;
    private String prodname;
    private int amount;
    private String thumb;
    private String cate;
    private int price;
    private int stock;
    private int recount;
    private int delCost;
    private int delType;
    private int discount;
    private String etc;
    private LocalDateTime rdate;

    // 포맷팅된 가격 필드 추가
    // private String formattedPrice; // 포맷팅된 정가
    // private String formattedDiscountPrice; // 포맷팅된 할인 가격
    // private String rewardPoints; // 적립 포인트

    // 상품 사진 출력을 위한 추가 필드
    private String titleImg; // thumb240
    private String contentImg; // thumb750
    private int cart_prodNo;
    private int count;
}

