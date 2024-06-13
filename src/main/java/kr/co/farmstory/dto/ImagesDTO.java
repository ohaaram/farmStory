package kr.co.farmstory.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImagesDTO {
    private int imgNo;
    private int prodno;
    private String thumb240;
    private String thumb750;
}
