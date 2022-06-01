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
	public Optional<HierarchyDataInventory> findBySauNameAndCommand(String sauName,String command);
	public List<HierarchyDataInventory> findByParentNodeIdAndCommand(int parentId,String command);
	public Optional<HierarchyDataInventory> findByrObjectIdAndCommand(String sauName,String command);
	@Modifying      // to mark delete or update query
	@Transactional
	@Query(value = "DELETE FROM mis_hierarchy_data_inventory e WHERE e.command = :command",nativeQuery = true)       // it will delete all the record with specific name
	int deleteByCommand(@Param("command") String command);
	
	@Query(value="SELECT command FROM mis_hierarchy_data_inventory e WHERE e.sau_name = :sau", nativeQuery = true)
	public String findCommandbySauName(@Param("sau") String sau);
}
