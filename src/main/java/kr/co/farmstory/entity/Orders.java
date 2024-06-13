package kr.co.farmstory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderNo;
    private String uid;
    private String reciver;
    private String rechp;
    private String recaddr;
    private String payment;
    private String memo;
    private String status;

    @CreationTimestamp
    private LocalDateTime rdate;


}

