package kr.co.farmstory.dto;


import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TermsDTO {

    private String terms;
    private String privacy;
}
