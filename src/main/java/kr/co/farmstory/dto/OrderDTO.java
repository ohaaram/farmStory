package kr.co.farmstory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderDTO {
    private int orderNo;
    private String uid;
    private String reciver;
    private String rechp;
    private String recaddr;
    private String payment;
    private String memo;
    private String status;
    private LocalDateTime rdate;


}

