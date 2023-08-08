package com.mis.eoffice.db2Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db2Models.DepartmentConfigInventory;


@Repository
public interface DepartmentConfigRepository extends CrudRepository<DepartmentConfigInventory, String> {

	List<DepartmentConfigInventory> findBydepBranch(String id);
	List<DepartmentConfigInventory> findByDepNameSmallContains(String sau);
	List<DepartmentConfigInventory> findByDepNameSmall(String sau);
	Optional<DepartmentConfigInventory> findByDepBranch(String sau);
}
