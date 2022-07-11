package com.mis.eoffice.db5Repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db2Models.FileInventory;
import com.mis.eoffice.db5Models.FileInventoryKNH;

@Repository
public interface FileInventoryRepositoryKNH extends JpaRepository<FileInventoryKNH, String> {
// File
	public List<FileInventoryKNH> findByCurrDepAndMisTypeAndTaskState(String sauName,String mistype, String state, Pageable page);
	public List<FileInventoryKNH> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end, Pageable page);
	public List<FileInventoryKNH> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,String mistype,String state,Date before, Pageable page);
	public List<FileInventoryKNH> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sau, String string,
			String taskstate, Date lastDate, Pageable page);
	public List<FileInventoryKNH> findByCurrDepAndMisTypeAndTaskState(String sauName, String string, String status);
	public List<FileInventoryKNH> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sauName,
			String string, String status, Date sevendaysbefore, Date threedaysbefore);
	public List<FileInventoryKNH> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sau, String string,
			String taskstate, Date lastDate);
	public List<FileInventoryKNH> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,
			String string, String taskstate, Date lastDate);
}
