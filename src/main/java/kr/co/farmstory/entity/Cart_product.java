package kr.co.farmstory.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "cart_product")
public class Cart_product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cart_prodNo;
    private int count;
    private int cartNo;
    private int prodNo;

}
