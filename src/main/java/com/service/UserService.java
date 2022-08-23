package com.service;

import com.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService
{
		List<User> getAllUsers();
		Optional<User> getUserByLogin(String login);
		boolean addUser(User user);
		boolean updateUser(User user);
		void deleteUser(String login);
}
