package com.repository;

import com.entity.Role;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long>
{
		Optional<Role> findById(long id);
    List<Role> findRoleByUserLogin (String userLogin);
}
