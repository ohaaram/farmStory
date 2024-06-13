package kr.co.farmstory.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewPageResponseDTO {

    private List<ReviewDTO> dtoList;
    private int rpg;
    private int size;
    private int total;
    private int last;
    private int startNo;
    private int start, end;
    private boolean prev, next;

    @Builder
    public ReviewPageResponseDTO(ReviewPageRequestDTO pageRequestDTO, List<ReviewDTO> dtoList , int total){
        this.rpg = pageRequestDTO.getRpg();
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        this.startNo = total - ((rpg - 1) * size);
        this.end = (int) (Math.ceil(this.rpg / 10.0)) * 10;
        this.start = this.end - 9;

        int last = (int) (Math.ceil(total / (double) size));
        this.last = last;
        this.end = end > last ? last : end;
        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }


}