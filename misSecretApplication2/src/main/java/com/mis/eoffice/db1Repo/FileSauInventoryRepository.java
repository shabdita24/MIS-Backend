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
	Optional<FileSauInventory> findBySauBranch(String sau);

	Optional<FileSauInventory> findBySauId(int sau);

	List<FileSauInventory> findByParentId(int sau);

	List<FileSauInventory> findAllByHasChildren(int i);

	@Modifying      // to mark delete or update query
	@Transactional
	@Query(value = "DELETE FROM mis_incoming_file_sau_inventory e WHERE e.command = :command",nativeQuery = true)       // it will delete all the record with specific name
	int deleteByCommand(@Param("command") String command);

}
