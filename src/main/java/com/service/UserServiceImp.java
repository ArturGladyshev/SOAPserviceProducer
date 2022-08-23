package com.service;

import com.entity.Role;
import com.entity.User;
import com.repository.RoleRepository;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService
{
		private final UserRepository userRepository;

		private final RoleRepository roleRepository;

		@Autowired
		public UserServiceImp(RoleRepository roleRepository, UserRepository userRepository)
		{
				this.roleRepository = roleRepository;
				this.userRepository = userRepository;
		}

		@Override
		public List<User> getAllUsers()
		{
				List<User> list = new ArrayList<>();
				userRepository.findAll().forEach(user -> list.add(user));
				return list;
		}

		@Override
		public Optional<User> getUserByLogin(String login)
		{
				Optional<User> user = userRepository.findByLogin(login);
				List<Role> roleList = roleRepository.findRoleByUserLogin(login);
				if(user.isPresent())
						user.get().setRoles(roleList);
				return user;
		}

		@Override
		public boolean addUser(User user)
		{
				Optional<User> foundUser = getUserByLogin(user.getLogin());
				if(foundUser.isPresent())
						return false;
				if(user.getRoles() != null)
						user.getRoles().forEach(role -> {
								role.setUserLogin(user.getLogin());
								roleRepository.save(role);
						});
				userRepository.save(user);
				return true;
		}

		@Override
		public boolean updateUser(User user)
		{
				Optional<User> foundUser = getUserByLogin(user.getLogin());
				if(foundUser.isPresent())
				{
						if(user.getRoles() == null && foundUser.get().getRoles() != null)
						{
								foundUser.get().getRoles().stream().forEach(role -> roleRepository.delete(role));
						}
						if(user.getRoles() != null)
						{
								user.getRoles().stream().forEach(role -> role.setUserLogin(user.getLogin()));
								if(foundUser.get().getRoles() != null)
								{
										List<Role> roles = user.getRoles().stream().filter(role -> foundUser.get().getRoles().
											contains(role) || !foundUser.get().getRoles().contains(role)).collect(Collectors.toList());
										user.setRoles(roles);
										foundUser.get().getRoles().stream().forEach(role -> {
												if(!roles.contains(role))
														roleRepository.delete(role);
										});
								}
								user.getRoles().stream().forEach(role -> roleRepository.save(role));
						}
						userRepository.save(user);
						return true;
				}
				return false;
		}

		@Override
		public void deleteUser(String login)
		{
				Optional<User> user = getUserByLogin(login);
				if(user.isPresent())
				{
						userRepository.delete(user.get());
						if(user.get().getRoles() != null)
								user.get().getRoles().forEach(role -> roleRepository.delete(role));
				}
		}

		private boolean performUserValidation(User user)
		{
				if(user == null)
						return false;
				if(user.getLogin() == null || user.getPassword() == null || user.getName() == null)
						return false;
				if(!Character.isUpperCase(user.getPassword().charAt(0)) || !Pattern.compile("[0-9]").matcher(user.getPassword()).find())
						return false;
				return true;
		}
}