package club.tulane.rpcfx.demo.provider;

import club.tulane.rpcfx.demo.api.User;
import club.tulane.rpcfx.demo.api.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
