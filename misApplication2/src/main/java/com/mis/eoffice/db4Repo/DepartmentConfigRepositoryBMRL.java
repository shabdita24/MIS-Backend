package com.mis.eoffice.db4Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db4Models.DepartmentConfigInventoryBMRL;


@Repository
public interface DepartmentConfigRepositoryBMRL extends CrudRepository<DepartmentConfigInventoryBMRL, String> {

	List<DepartmentConfigInventoryBMRL> findByDepNameSmallContains(String sau);
	List<DepartmentConfigInventoryBMRL> findByDepNameSmall(String sau);
	Optional<DepartmentConfigInventoryBMRL> findByDepBranch(String sau);
}
