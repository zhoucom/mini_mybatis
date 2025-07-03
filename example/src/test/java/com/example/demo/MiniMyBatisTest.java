package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mini MyBatis功能测试
 */
@SpringBootTest
public class MiniMyBatisTest {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserService userService;
    
    /**
     * 测试查询单个用户
     */
    @Test
    public void testFindById() {
        User user = userMapper.findById(1L);
        assertNotNull(user);
        assertEquals("张三", user.getName());
        System.out.println("查询用户: " + user);
    }
    
    /**
     * 测试查询所有用户
     */
    @Test
    public void testFindAll() {
        List<User> users = userMapper.findAll();
        assertNotNull(users);
        assertTrue(users.size() > 0);
        System.out.println("所有用户: " + users);
    }
    
    /**
     * 测试根据名称模糊查询
     */
    @Test
    public void testFindByName() {
        List<User> users = userMapper.findByName("%张%");
        assertNotNull(users);
        assertTrue(users.size() > 0);
        System.out.println("名称包含'张'的用户: " + users);
    }
    
    /**
     * 测试根据年龄范围查询
     */
    @Test
    public void testFindByAgeRange() {
        List<User> users = userMapper.findByAgeRange(25, 30);
        assertNotNull(users);
        System.out.println("25-30岁的用户: " + users);
    }
    
    /**
     * 测试插入用户
     */
    @Test
    public void testInsert() {
        int result = userMapper.insert("测试用户", "test@example.com", 26);
        assertEquals(1, result);
        System.out.println("插入用户成功，影响行数: " + result);
    }
    
    /**
     * 测试更新用户
     */
    @Test
    public void testUpdate() {
        int result = userMapper.update("更新的名称", "updated@example.com", 27, 1L);
        assertEquals(1, result);
        System.out.println("更新用户成功，影响行数: " + result);
        
        // 验证更新结果
        User updatedUser = userMapper.findById(1L);
        assertEquals("更新的名称", updatedUser.getName());
    }
    
    /**
     * 测试删除用户
     */
    @Test
    public void testDelete() {
        // 先插入一个用户
        userMapper.insert("待删除用户", "delete@example.com", 20);
        
        // 查询用户总数
        Long countBefore = userMapper.count();
        
        // 删除最后一个用户 (假设ID为最大值)
        int result = userMapper.deleteById(countBefore);
        assertEquals(1, result);
        
        // 验证删除结果
        Long countAfter = userMapper.count();
        assertEquals(countBefore - 1, countAfter);
        System.out.println("删除用户成功，删除前用户数: " + countBefore + ", 删除后用户数: " + countAfter);
    }
    
    /**
     * 测试统计功能
     */
    @Test
    public void testCount() {
        Long count = userMapper.count();
        assertNotNull(count);
        assertTrue(count > 0);
        System.out.println("用户总数: " + count);
    }
    
    /**
     * 测试Service层功能
     */
    @Test
    public void testUserService() {
        // 测试获取所有用户
        List<User> allUsers = userService.getAllUsers();
        assertNotNull(allUsers);
        System.out.println("Service - 所有用户: " + allUsers.size() + " 个");
        
        // 测试搜索用户
        List<User> searchUsers = userService.searchUsersByName("李");
        assertNotNull(searchUsers);
        System.out.println("Service - 搜索包含'李'的用户: " + searchUsers.size() + " 个");
        
        // 测试创建用户
        boolean createResult = userService.createUser("Service测试用户", "service@example.com", 30);
        assertTrue(createResult);
        System.out.println("Service - 创建用户: " + createResult);
        
        // 测试统计
        Long totalCount = userService.getUserCount();
        assertNotNull(totalCount);
        System.out.println("Service - 用户总数: " + totalCount);
    }
    
    /**
     * 测试动态代理是否正常工作
     */
    @Test
    public void testMapperProxy() {
        // 验证Mapper是否为代理对象
        String mapperClass = userMapper.getClass().getName();
        assertTrue(mapperClass.contains("Proxy") || mapperClass.contains("$"));
        System.out.println("Mapper代理类: " + mapperClass);
        
        // 验证代理对象能正常工作
        User user = userMapper.findById(1L);
        assertNotNull(user);
        System.out.println("代理对象调用成功: " + user.getName());
    }
} 