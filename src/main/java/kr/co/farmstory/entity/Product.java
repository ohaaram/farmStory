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
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int prodno;
    private String prodname;
    private int amount;
    @Column(name = "thumb")
    private String thumb;
    private String cate;
    private int price;
    private int stock;
    private int recount;
    private int discount;
    private int delCost;
    private int delType;
    private String etc;
    @CreationTimestamp
    private LocalDateTime rdate;
}

