package kr.co.farmstory.dto;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserDTO {
    private String uid;
    private String pass;
    private String name;
    private String email;
    private String nick;
    private String hp;
    private String role;
    private String level;

    private String zip;
    private String addr1;
    private String addr2;
    private String regip;
    private LocalDateTime regDate;
    private LocalDateTime leaveDate;
    private String provider;
    // account 칼럼 join 조회용
    @Builder.Default
    private int point = 0;
}
