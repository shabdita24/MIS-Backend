package com.mis.eoffice.db1Repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mis.eoffice.db1Models.HierarchyDataInventory;



@Repository
public interface DataSauInventoryRepository extends CrudRepository<HierarchyDataInventory, Integer> {
	public Boolean existsByrObjectId(String sauName);
	public Optional<HierarchyDataInventory> findBySauName(String sauName);
	public List<HierarchyDataInventory> findByParentNodeId(int parentId);

	public Optional<HierarchyDataInventory> findByrObjectId(String sauName);

	@Modifying      // to mark delete or update query
	@Transactional
	@Query(value = "DELETE FROM mis_hierarchy_data_inventory e WHERE e.command = :command",nativeQuery = true)       // it will delete all the record with specific name
	int deleteByCommand(@Param("command") String command);
}
