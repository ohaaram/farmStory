package kr.co.farmstory.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReviewRatioDTO {
    private long count;
    private long sum;
    private double avg;
    private int percent1;
    private int percent2;
    private int percent3;
    private int percent4;
    private int percent5;

    @Builder
    public ReviewRatioDTO(long count, long sum, double avg, int oneScore, int twoScore, int threeScore, int fourScore, int fiveScore) {
        this.count = count;
        this.sum = sum;
        this.avg = avg;

        // 각 스코어 별 퍼센트 구하기
        this.percent1 = (int) Math.round((double) oneScore / count * 100);
        this.percent2 = (int) Math.round((double) twoScore / count * 100);
        this.percent3 = (int) Math.round((double) threeScore / count * 100);
        this.percent4 = (int) Math.round((double) fourScore / count * 100);
        this.percent5 = (int) Math.round((double) fiveScore / count * 100);
    }
}

