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
@Table(name = "terms")
public class Terms {

    @Id
    private String terms;
    private String privacy;
}