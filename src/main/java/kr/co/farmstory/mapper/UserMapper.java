package kr.co.farmstory.mapper;

import kr.co.farmstory.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserMapper {

    UserDTO selectUserByUid(String uid);
    public void insertUser(UserDTO userDTO);
    public int selectCountUser(@Param("type") String type, @Param("value") String value);
    UserDTO selectUserByNameAndEmail(@Param("name") String name, @Param("email") String email);
    void updateUserPassword(@Param("uid") String uid, @Param("password") String password);
    public UserDTO findById(String uid);
    public void regiAccount(String uid, int level, int point);
    public void regiCart(String uid);
    public void updateSocial(String uid,String hp,String zip,String addr1,String addr2);
    public void updateRole(String uid, String role);

}
