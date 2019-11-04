package top.jimxu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.jimxu.entity.User;

import java.util.List;

public interface UserService extends IService<User>{
    int insertUser( User user );
    int updateUser( User user );
    int deleteUser( User user );
    User findUserById( int id );
    List<User> selectList(User user);
}
