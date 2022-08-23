package com.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements Serializable
{
		private static final long serialVersionUID = 1L;

		@Id
		@Column(name = "login")
		private String login;

		@Column(name = "name")
		private String name;

		@Column(name = "password")
		private String password;

		@OneToMany
		@JoinColumn(name = "user_login")
		private List<Role> roles;

		public String getLogin()
		{
				return login;
		}

		public void setLogin(String login)
		{
				this.login = login;
		}

		public String getName()
		{
				return name;
		}

		public void setName(String name)
		{
				this.name = name;
		}

		public String getPassword()
		{
				return password;
		}

		public void setPassword(String password)
		{
				this.password = password;
		}

		public List<Role> getRoles()
		{
				return roles;
		}

		public void setRoles(List<Role> roles)
		{
				this.roles = roles;
		}
}


