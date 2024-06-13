package kr.co.farmstory.service;

import com.querydsl.core.Tuple;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import kr.co.farmstory.dto.*;
import kr.co.farmstory.entity.User;
import kr.co.farmstory.mapper.UserMapper;
import kr.co.farmstory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;


    // 회원 등록이 되어 있는지 확인하는 기능(0또는 1)
    public int selectCountUser(String type,String value){

        return userMapper.selectCountUser(type,value);
    }

    // 회원 가입 기능 
    public void insertUser(UserDTO userDTO){

        String encoded = passwordEncoder.encode(userDTO.getPass());

        userDTO.setPass(encoded);

        //먼저 user테이블에 회원정보를 입력
        userMapper.insertUser(userDTO);

        //그다음에 account(포인트)테이블에 회원등록
        userMapper.regiAccount(userDTO.getUid(),1,0);

        //cart테이블에 회원등록
        userMapper.regiCart(userDTO.getUid());
    }

    //소셜로그인 후 추가정보 업데이트
    public void social(UserDTO userDTO){

        userMapper.updateSocial(userDTO.getUid(),userDTO.getHp(),userDTO.getZip(),userDTO.getAddr1(),userDTO.getAddr2());

    }


    
    @Value("${spring.mail.username}")//이메일 보내는 사람 주소
    private String sender;
    //이메일 보내기 기능
    public void sendEmailCode(HttpSession session, String receiver){

        log.info("sender : " + sender);

        // MimeMessage 생성
        MimeMessage message = javaMailSender.createMimeMessage();

        // 인증코드 생성 후 세션 저장
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        session.setAttribute("code", String.valueOf(code));
        log.info("code : " + code);

        String title = "farmstory 인증코드 입니다.";
        String content = "<h1>인증코드는 " + code + "입니다.</h1>";

        try {
            message.setSubject(title);
            message.setFrom(new InternetAddress(sender, "보내는 사람", "UTF-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject(title);
            message.setContent(content, "text/html;charset=UTF-8");

            javaMailSender.send(message);

        }catch(Exception e){
            log.error("sendEmailConde : " + e.getMessage());
        }
    }

    // 아이디 비밀번호 확인후 로그인
    public boolean selectUser(String uid, String pass){
        UserDTO user = userMapper.selectUserByUid(uid);

        if(user != null){
            return passwordEncoder.matches(pass, user.getPass());
        }
        return false;
    }

    // 아이디/비밀번호 찾기
    public String findUserIdByNameAndEmail(String name, String email){
        UserDTO user = userMapper.selectUserByNameAndEmail(name, email);
        return user != null ? user.getUid() : null;
    }

    public UserDTO findById(String uid){
        return userMapper.findById(uid);
    }

    // 비밀번호 변경 - 만드는 중
    public void updateUserPassword(String uid, String newPassword){

        String encodedPass = passwordEncoder.encode(newPassword);
        userMapper.updateUserPassword(uid, encodedPass);

    }

    // 회원목록
    public List<UserDTO> getUserList(PageRequestDTO pageRequestDTO) {
        // UserService 내 getUserList 메소드 수정
        Pageable pageable = pageRequestDTO.getPageable("regDate");
        Page<User> result = userRepository.findByRoleNot("delete", pageable);

        return result.getContent().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    // user 테이블 총 칼럼 수 구하기
    public int getTotalCount(PageRequestDTO pageRequestDTO) {
        // 조건에 맞는 사용자 수를 반환하는 로직을 구현합니다.
        // 예시에서는 간단히 모든 사용자 수를 반환합니다.
        return (int) userRepository.count();
    }

    // 상세페이지(id로 그 유저의 정보를 가져온다)
    public UserDTO getUserByUid(String uid) {
        UserDTO userDTO = userMapper.findById(uid);
        if (userDTO == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userDTO;
    }

    // 주문한 사용자와 포인트 조회
    public UserDTO selectUserForOrder(String uid){

        log.info("주문하기 사용자 조회 서비스 1 :" + uid);
        Tuple result = userRepository.selectUserForOrder(uid);
        log.info("주문하기 사용자 조회 서비스 2 ");
        // Tuple -> Entity
        User user = result.get(0, User.class);
        log.info("주문하기 사용자 조회 서비스 3 :" + user.toString());
        int point = result.get(1, Integer.class);
        user.setPoint(point);
        // Entity -> DTO
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return userDTO;
    }

    // 관리자 페이지에서 유저 등급, 권한 수정
    public void updateUser(UserDTO userDTO) {
        log.info("updateUser....1");
        // 유저 정보 조회
        User user = userRepository.findById(userDTO.getUid())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userDTO.getUid()));

        log.info("updateUser....2" + user.toString());
        // 새 권한, 등급 입력
        user.setLevel(userDTO.getLevel());
        user.setRole(userDTO.getRole());
        log.info("updateUser....3" + user.toString());
        // DB에 정보 수정하기
        userRepository.save(user);
        log.info("updateUser....4 save");

    }
    
    // 유저 삭제하기 - 정보를 남기고 권한을 변경하기
    @Transactional
    public void deleteUser(String uid) {
        // userRepository를 사용하여 사용자를 삭제합니다.
        
        userRepository.deleteById(uid);
    }

    // 관리자 페이지 회원 정보 조회 (all)
    public List<UserDTO> allUser(){

        List<User> users = userRepository.findAll();

        return users.stream().map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    public void updateRole(String uid, String role){
        userMapper.updateRole(uid,role);
    }

}

