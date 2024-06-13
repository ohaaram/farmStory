package kr.co.farmstory.dto;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderDetailDTO {

    private int detailno;
    private int prodno;
    private int orderNo;
    private int count;

}

