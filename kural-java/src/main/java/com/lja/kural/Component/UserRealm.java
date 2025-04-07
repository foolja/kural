package com.lja.kural.Component;

import com.lja.kural.Bean.User;
import com.lja.kural.Mapper.UserMapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 本系统只有身份验证,没有权限之分
 */
@Component
public class UserRealm  extends AuthorizingRealm  {
    @Autowired
    private UserMapper userMapper;
    /**
     * 认证
     * 1.获取用户信息
     * 2.获取数据库层的用户信息
     * 3.非空判断
     * 4.将数据封装返回
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 获取用户名和密码
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        // 根据用户名查询用户信息
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UnknownAccountException("账户不存在!");
        }
        if (!password.equals(user.getPassword())) {
            throw new UnknownAccountException("密码错误!");
        }
        return new SimpleAuthenticationInfo(token.getPrincipal(), password, getName());
    }

    /**
     * 授权
     * @param principals the primary identifying principals of the AuthorizationInfo that should be retrieved.
     * @return
     */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }
}
