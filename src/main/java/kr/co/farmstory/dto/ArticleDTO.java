package kr.co.farmstory.dto;

import kr.co.farmstory.entity.File;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ArticleDTO {
    private int ano;
    private String cate;
    private String title;
    private String content;
    private String writer;
    private String thumbnail;
    private int file;
    private int hit;
    private int prodno;
    private LocalDateTime rdate;

    private String nick;

    private List<MultipartFile> files;
    private List<File> fileList;
}
