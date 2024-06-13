package kr.co.farmstory.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ReviewPageRequestDTO {

    @Builder.Default
    private int no = 1;

    @Builder.Default
    private int rpg = 1;

    @Builder.Default
    private int size = 10;

    public Pageable getPageable(String sort){
        return PageRequest.of(this.rpg - 1, this.size, Sort.by(sort).descending());
    }

}
