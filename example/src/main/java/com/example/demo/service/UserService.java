package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务类
 * 演示如何使用Mini MyBatis进行业务操作
 */
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取用户详情
     */
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
    
    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
    
    /**
     * 根据名称搜索用户
     */
    public List<User> searchUsersByName(String name) {
        return userMapper.findByName("%" + name + "%");
    }
    
    /**
     * 根据年龄范围查询用户
     */
    public List<User> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        return userMapper.findByAgeRange(minAge, maxAge);
    }
    
    /**
     * 创建用户
     */
    public boolean createUser(String name, String email, Integer age) {
        int result = userMapper.insert(name, email, age);
        return result > 0;
    }
    
    /**
     * 更新用户信息
     */
    public boolean updateUser(Long id, String name, String email, Integer age) {
        int result = userMapper.update(name, email, age, id);
        return result > 0;
    }
    
    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        int result = userMapper.deleteById(id);
        return result > 0;
    }
    
    /**
     * 统计用户总数
     */
    public Long getUserCount() {
        return userMapper.count();
    }
} 