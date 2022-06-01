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

Optional<FileSauBranchInventory> findBySauBranchAndCommand(String sau,String cmd);
	
	Optional<FileSauBranchInventory> findBySauIdAndCommand(int sau,String cmd);
	List<FileSauBranchInventory> findByParentIdAndCommand(int sau,String cmd);
	List<FileSauBranchInventory> findBySauParentAndCommand(String sau,String cmd);
	public Optional<FileSauBranchInventory> findByrObjectIdAndCommand(String rObjectId,String cmd);
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update FileSauBranchInventory u set u.totalFileInbox = :inboxFilesCount1 , u.totalFilesPendingFiveTen= :sevenDayNThreeDayCount1, u.totalFileProcessedTenDays= :sevenDayCount1,u.hasChildren=:hasChild where u.sauId= :sauId")
	void updateFileSauBranch(@Param("inboxFilesCount1") int inboxFilesCount1,
			@Param("sevenDayNThreeDayCount1") int sevenDayNThreeDayCount1,
			
			@Param("sevenDayCount1") int sevenDayCount1,
			@Param("sauId") int sauId,
			@Param("hasChild") int hasChild);

	@Modifying      // to mark delete or update query
	@Transactional
	@Query(value = "DELETE FROM mis_my_file_sau_branch_inventory e WHERE e.command = :command",nativeQuery = true)       // it will delete all the record with specific name
	int deleteByCommand(@Param("command") String command);
}
