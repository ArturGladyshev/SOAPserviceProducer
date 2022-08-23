package com.endpoints;

import com.entity.Role;
import com.entity.User;
import com.enums.RoleEnum;
import com.gs_ws.*;
import com.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Endpoint
public class UserEndpoint
{
		private static final String NAMESPACE_URI = "http://www.soap/user-ws";

		private final UserService userService;

		@Autowired
		public UserEndpoint(UserService userService)
		{
				this.userService = userService;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserByLoginRequest")
		@ResponsePayload
		public GetUserByLoginResponse getUser(@RequestPayload GetUserByLoginRequest request)
		{
				GetUserByLoginResponse response = new GetUserByLoginResponse();
				UserRolesInfo userRolesInfo = new UserRolesInfo();
				Optional<User> user = userService.getUserByLogin(request.getLogin());
				if(user.isPresent() || user.get().getRoles() != null)
						user.get().getRoles().stream().forEach((role) -> {
								RoleInfo roleInfo = new RoleInfo();
								roleInfo.setName(role.getName().name());
								userRolesInfo.getRoles().add(roleInfo);
						});
				BeanUtils.copyProperties(user, userRolesInfo);
				response.setUserRolesInfo(userRolesInfo);
				return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllUsersRequest")
		@ResponsePayload
		public GetAllUsersResponse getAllUsers()
		{
				GetAllUsersResponse response = new GetAllUsersResponse();
				List<UserInfo> userInfoList = new ArrayList<>();
				List<User> userList = userService.getAllUsers();
				for(int i = 0; i < userList.size(); i++)
				{
						UserInfo userInfo = new UserInfo();
						BeanUtils.copyProperties(userList.get(i), userInfo);
						userInfoList.add(userInfo);
				}
				response.getUserInfo().addAll(userInfoList);
				return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addUserRequest")
		@ResponsePayload
		public AddUserResponse addUser(@RequestPayload AddUserRequest request)
		{
				AddUserResponse response = new AddUserResponse();
				ServiceStatus serviceStatus = new ServiceStatus();
				User user = new User();
				user.setLogin(request.getUserRolesInfo().getLogin());
				user.setName(request.getUserRolesInfo().getName());
				user.setPassword(request.getUserRolesInfo().getPassword());
				user.setRoles(this.getRoleListFromRolesInfoList(request.getUserRolesInfo().getRoles(), user));
				if(!performUserValidation(user))
				{
						serviceStatus.setStatusCode("CONFLICT");
						serviceStatus.setMessage("The User being added is incorrect");
				}
				else
						if(!userService.addUser(user))
						{
								serviceStatus.setStatusCode("CONFLICT");
								serviceStatus.setMessage("Content Already Available");
						}
						else
						{
								BeanUtils.copyProperties(user, request.getUserRolesInfo());
								response.setUserRolesInfo(request.getUserRolesInfo());
								serviceStatus.setStatusCode("SUCCESS");
								serviceStatus.setMessage("Content Added Successfully");
						}
				response.setServiceStatus(serviceStatus);
				return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateUserRequest")
		@ResponsePayload
		public UpdateUserResponse updateUser(@RequestPayload UpdateUserRequest request)
		{
				ServiceStatus serviceStatus = new ServiceStatus();
				UpdateUserResponse response = new UpdateUserResponse();
				User user = new User();
				user.setLogin(request.getUserRolesInfo().getLogin());
				user.setName(request.getUserRolesInfo().getName());
				user.setPassword(request.getUserRolesInfo().getPassword());
						if(request.getUserRolesInfo().getRoles() != null)
						user.setRoles(this.getRoleListFromRolesInfoList(request.getUserRolesInfo().getRoles(), user));
				if(!performUserValidation(user))
				{
						serviceStatus.setStatusCode("CONFLICT");
						serviceStatus.setMessage("The User being updated is incorrect");
				}
						else if(userService.updateUser(user))
				{
						UserRolesInfo userRolesInfo = new UserRolesInfo();
						if(user.getRoles() != null)
								user.getRoles().stream().forEach((role) -> {
										RoleInfo roleInfo = new RoleInfo();
										roleInfo.setName(role.getName().name());
										userRolesInfo.getRoles().add(roleInfo);
								});
						BeanUtils.copyProperties(user, userRolesInfo);
						serviceStatus.setStatusCode("SUCCESS");
						serviceStatus.setMessage("Content Updated Successfully");
				}
				else
				{
						serviceStatus.setStatusCode("ERROR");
						serviceStatus.setMessage("Data entered incorrectly");
				}
				response.setServiceStatus(serviceStatus);
				return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteUserRequest")
		@ResponsePayload
		public DeleteUserResponse deleteUser(@RequestPayload DeleteUserRequest request)
		{
				Optional<User> user = userService.getUserByLogin(request.getLogin());
				ServiceStatus serviceStatus = new ServiceStatus();
				if(user.isEmpty())
				{
						serviceStatus.setStatusCode("FAIL");
						serviceStatus.setMessage("Content Not Available");
				}
				else
				{
						userService.deleteUser(user.get().getLogin());
						serviceStatus.setStatusCode("SUCCESS");
						serviceStatus.setMessage("Content Deleted Successfully");
				}
				DeleteUserResponse response = new DeleteUserResponse();
				response.setServiceStatus(serviceStatus);
				return response;
		}

		private List<Role> getRoleListFromRolesInfoList(List<RoleInfo> rolesInfoList, User user)
		{
				List<Role> roleList = new ArrayList<>();
				rolesInfoList.stream().forEach(roleInfo -> {
						Role role = new Role();
						role.setName(RoleEnum.valueOf(roleInfo.getName()));
						role.setUserLogin(user.getLogin());
						roleList.add(role);
				});
				return roleList;
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
