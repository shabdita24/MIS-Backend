package com.mis.eoffice.db2Repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db2Models.FileInventory;

@Repository
public interface FileInventoryRepository extends JpaRepository<FileInventory, String> {
//	Operations Services used queries and Detailed Table used Queries
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskState(String sauName,String mistype, String state);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,String mistype,String state,Date before);
	public List<FileInventory> findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sauName,
			String string, String status, Date lastDate);

}
