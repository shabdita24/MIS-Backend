package com.mis.eoffice.db1Repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mis.eoffice.db1Models.FileSauInventory;



@Repository
public interface FileSauInventoryRepository extends JpaRepository<FileSauInventory, String> {
Optional<FileSauInventory> findBySauBranchAndCommand(String sau,String command);
	
	Optional<FileSauInventory> findBySauIdAndCommand(int sau,String command);
	List<FileSauInventory> findByParentIdAndCommand(int sau,String command);

	List<FileSauInventory> findAllByHasChildrenAndCommand(int i,String command);
	@Modifying      // to mark delete or update query
	@Transactional
	@Query(value = "DELETE FROM mis_incoming_file_sau_inventory e WHERE e.command = :command",nativeQuery = true)       // it will delete all the record with specific name
	int deleteByCommand(@Param("command") String command);
}
