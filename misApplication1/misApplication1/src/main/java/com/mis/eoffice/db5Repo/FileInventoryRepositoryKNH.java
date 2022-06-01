package com.mis.eoffice.db5Repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db5Models.FileInventoryKNH;

@Repository
public interface FileInventoryRepositoryKNH extends JpaRepository<FileInventoryKNH, String> {
// File
	public List<FileInventoryKNH> findByTaskStateAndMisTypeAndDateTimeForwardedBetween(String s,String mistype,Date start,Date end);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndDateTimeRecievedNotNull(String sau,String mistype);
	public Optional<FileInventoryKNH> findByInitiatedBySauAndMisType(String sau,String mistype);
	public boolean existsByInitiatedBySauAndMisTypeContains(String sau,String misType);
	public List<FileInventoryKNH> findByToSauAndMisTypeAndDateTimeForwardedIsNull(String sau,String mistype);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndTaskStateContainsAndDateTimeRecievedBefore(String sau,String mistype,String state,Date date);
	public List<FileInventoryKNH> findByToSauAndTaskState(String name,String state);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndDateTimeRecievedBefore(String name,String mistype, Date sevendaysbefore);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndDateTimeRecievedBetween(String name,String mistype, Date start, Date end);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndTaskStateContaining(String sau,String mistype,String state);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedIsNotNull(String sau,String mistype,String state);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndTaskState(String sauName,String mistype, String state);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween(String sau,String mistype,String state,Date start,Date end);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(String sau,String mistype,String state,Date before);
	public List<FileInventoryKNH> findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter(String sauName,
			String string, String state, Date fivedaysafter);
}
