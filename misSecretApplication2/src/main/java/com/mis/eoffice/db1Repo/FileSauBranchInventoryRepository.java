package com.mis.eoffice.db1Repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mis.eoffice.db1Models.FileSauBranchInventory;

@Repository
public interface FileSauBranchInventoryRepository extends JpaRepository<FileSauBranchInventory, String> {
	Optional<FileSauBranchInventory> findBySauId(int sau);
	Optional<FileSauBranchInventory> findBySauBranch(String sau);
	List<FileSauBranchInventory> findByParentId(int sau);
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update FileSauBranchInventory u set u.totalFileInbox = :inboxFilesCount1 , u.totalFilesPendingFiveTen= :tenDayNFiveDayCount1, u.totalFileProcessedTenDays= :tenDayCount1,u.hasChildren=:hasChild where u.sauId= :sauId")
	void updateFileSauBranch(@Param("inboxFilesCount1") int inboxFilesCount1,
			@Param("tenDayNFiveDayCount1") int tenDayNFiveDayCount1,
			@Param("tenDayCount1") int tenDayCount1,
			@Param("sauId") int sauId,
			@Param("hasChild") int hasChild);
	@Modifying      // to mark delete or update query
	@Transactional
	@Query(value = "DELETE FROM mis_incoming_file_sau_branch_inventory e WHERE e.command = :command",nativeQuery = true)       // it will delete all the record with specific name
	int deleteByCommand(@Param("command") String command);


}
