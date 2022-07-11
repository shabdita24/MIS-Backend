package com.mis.eoffice.db2Repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db2Models.FileInventory;

@Repository
public interface FileInventoryRepository extends JpaRepository<FileInventory, String> {
//	Operations Services used queries and Detailed Table used Queries
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskState(String sauName,String mistype, String state, Pageable page);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end, Pageable page);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,String mistype,String state,Date before, Pageable page);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sauName,
			String string, String status, Date lastDate, Pageable page);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskState(String sauName, String string, String status);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sau, String string,
			String status, Date sevendaysbefore, Date threedaysbefore);

	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau, String string,
			String state, Date sevendaysbefore);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sau, String string,
			String taskstate, Date lastDate);

}
