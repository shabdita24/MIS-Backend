package com.mis.eoffice.db4Repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db4Models.FileInventoryBMRL;

@Repository
public interface FileInventoryRepositoryBMRL extends JpaRepository<FileInventoryBMRL, String> {
// File
	public List<FileInventoryBMRL> findByTaskStateAndMisTypeAndDateTimeForwardedBetween(String s,String mistype,Date start,Date end);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndDateTimeRecievedNotNull(String sau,String mistype);
	public Optional<FileInventoryBMRL> findByInitiatedBySauAndMisType(String sau,String mistype);
	public boolean existsByInitiatedBySauAndMisTypeContains(String sau,String misType);
	public List<FileInventoryBMRL> findByToSauAndMisTypeAndDateTimeForwardedIsNull(String sau,String mistype);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateContainsAndDateTimeRecievedBefore(String sau,String mistype,String state,Date date);
	public List<FileInventoryBMRL> findByToSauAndTaskState(String name,String state);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndDateTimeRecievedBefore(String name,String mistype, Date sevendaysbefore);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndDateTimeRecievedBetween(String name,String mistype, Date start, Date end);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateContaining(String sau,String mistype,String state);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedIsNotNull(String sau,String mistype,String state);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskState(String sauName,String mistype, String state, Pageable page);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end, Pageable page);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,String mistype,String state,Date before, Pageable page);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sauName,
			String string, String state, Date fivedaysafter, Pageable page);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskState(String sauName, String string,
			String status);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sauName,
			String string, String status, Date sevendaysbefore, Date threedaysbefore);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,
			String string, String taskstate, Date lastDate);
	public List<FileInventoryBMRL> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sau,
			String string, String taskstate, Date lastDate);
}
