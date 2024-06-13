package kr.co.farmstory.entity;

import jakarta.persistence.*;
import kr.co.farmstory.dto.UserDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name="user")
public class User{
    @Id
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

    @CreationTimestamp
    private LocalDateTime regDate;
    private LocalDateTime leaveDate;
    private String provider;
    // account 칼럼 join 조회용
    @Transient
    private int point;

 }
