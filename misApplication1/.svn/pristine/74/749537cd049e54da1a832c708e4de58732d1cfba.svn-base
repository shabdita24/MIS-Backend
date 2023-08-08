package com.mis.eoffice.db5Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db5Models.DepartmentConfigInventoryKNH;


@Repository
public interface DepartmentConfigRepositoryKNH extends CrudRepository<DepartmentConfigInventoryKNH, String> {

	List<DepartmentConfigInventoryKNH> findBydepBranch(String id);
	List<DepartmentConfigInventoryKNH> findByDepNameSmallContains(String sau);
	List<DepartmentConfigInventoryKNH> findByDepNameSmall(String sau);
	Optional<DepartmentConfigInventoryKNH> findByDepBranch(String sau);
}
