package com.entity;

import com.enums.RoleEnum;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="roles")
public class Role implements Serializable
{
		private static final long serialVersionUID = 1L;

		@Id
		@GeneratedValue(strategy= GenerationType.AUTO)
		@Column(name="role_id")
		private long roleId;

		@Column(name = "name", columnDefinition = "enum('Admin', 'Operator', 'Analyst', 'Moderator', 'Editor')")
		@Enumerated(EnumType.STRING)
		private RoleEnum name;

		@Column(name="user_login")
		private String userLogin;

		public String getUserLogin()
		{
				return userLogin;
		}

		public void setUserLogin(String userLogin)
		{
				this.userLogin = userLogin;
		}

		public long getRoleId()
		{
				return roleId;
		}

		public void setRoleId(long roleId)
		{
				this.roleId = roleId;
		}

		public RoleEnum getName()
		{
				return name;
		}

		public void setName(RoleEnum name)
		{
				this.name = name;
		}


}
