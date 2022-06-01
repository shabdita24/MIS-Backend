package com.mis.eoffice.db4Repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db2Models.FileInventory;
import com.mis.eoffice.db4Models.FileInventoryBMRL;

@Repository
public interface FileInventoryRepositoryBMRL extends JpaRepository<FileInventoryBMRL, String> {
// File
	public List<FileInventoryBMRL> findByCurrDepAndMisTypeAndTaskState(String sauName,String mistype, String state);
	public List<FileInventoryBMRL> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end);
	public List<FileInventoryBMRL> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,String mistype,String state,Date before);
	public List<FileInventoryBMRL> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sau, String string,
			String taskstate, Date lastDate);
	
}
