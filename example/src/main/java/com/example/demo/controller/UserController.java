package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 提供用户管理的REST API
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取所有用户
     */
    @GetMapping
    public Map<String, Object> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", users);
        result.put("total", users.size());
        return result;
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("success", true);
            result.put("data", user);
        } else {
            result.put("success", false);
            result.put("message", "用户不存在");
        }
        return result;
    }
    
    /**
     * 根据名称搜索用户
     */
    @GetMapping("/search")
    public Map<String, Object> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", users);
        result.put("total", users.size());
        return result;
    }
    
    /**
     * 根据年龄范围查询用户
     */
    @GetMapping("/age-range")
    public Map<String, Object> getUsersByAgeRange(@RequestParam Integer minAge, 
                                                  @RequestParam Integer maxAge) {
        List<User> users = userService.getUsersByAgeRange(minAge, maxAge);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", users);
        result.put("total", users.size());
        return result;
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public Map<String, Object> createUser(@RequestBody User user) {
        boolean success = userService.createUser(user.getName(), user.getEmail(), user.getAge());
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "用户创建成功" : "用户创建失败");
        return result;
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        boolean success = userService.updateUser(id, user.getName(), user.getEmail(), user.getAge());
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "用户更新成功" : "用户更新失败");
        return result;
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "用户删除成功" : "用户删除失败");
        return result;
    }
    
    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getUserStats() {
        Long totalCount = userService.getUserCount();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("totalUsers", totalCount);
        return result;
    }
} 