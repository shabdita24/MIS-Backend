package com.mis.eoffice.db2Repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.mis.eoffice.db2Models.FileInventory;

@Repository
public interface FileInventoryRepository extends JpaRepository<FileInventory, String> {
// File
	public List<FileInventory> findByTaskStateAndMisTypeAndDateTimeForwardedBetween(String s,String mistype,Date start,Date end);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndDateTimeRecievedNotNull(String sau,String mistype);
	public Optional<FileInventory> findByInitiatedBySauAndMisType(String sau,String mistype);
	public boolean existsByInitiatedBySauAndMisTypeContains(String sau,String misType);
	public List<FileInventory> findByToSauAndMisTypeAndDateTimeForwardedIsNull(String sau,String mistype);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateContainsAndDateTimeRecievedBefore(String sau,String mistype,String state,Date date);
	public List<FileInventory> findByToSauAndTaskState(String name,String state);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndDateTimeRecievedBefore(String name,String mistype, Date sevendaysbefore);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndDateTimeRecievedBetween(String name,String mistype, Date start, Date end);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateContaining(String sau,String mistype,String state);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedIsNotNull(String sau,String mistype,String state);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskState(String sauName,String mistype, String state, Pageable page);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end, Pageable page);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,String mistype,String state,Date before, Pageable page);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sauName,
			String string, String state, Date fivedaysafter, Pageable page);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sauName,
			String string, String status, Date lastDate);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskState(String sauName, String string, String status);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sauName,
			String string, String status, Date sevendaysbefore, Date threedaysbefore);
	public List<FileInventory> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sau,
			String string, String taskstate, Date lastDate);
}
