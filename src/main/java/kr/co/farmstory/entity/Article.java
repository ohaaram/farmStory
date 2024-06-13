package kr.co.farmstory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ano;
    private String cate;
    private String title;
    private String content;
    private String writer;
    private String thumbnail;
    private int file;
    private int hit;
    private Integer prodno;

    @CreationTimestamp
    private LocalDateTime rdate;

    @Transient
    private String nick;

    @OneToMany(mappedBy = "ano")
    private List<File> fileList;
}
