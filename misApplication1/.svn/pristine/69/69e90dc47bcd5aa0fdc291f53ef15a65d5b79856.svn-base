package com.mis.eoffice.serviceImpls;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mis.eoffice.db2Models.FileInventory;
import com.mis.eoffice.db2Repo.FileInventoryRepository;
import com.mis.eoffice.service.OperationsDataService;

@Service
public class OperationsDataServiceImpl implements OperationsDataService{

	private static final Logger logger = LoggerFactory.getLogger(OperationsDataServiceImpl.class);

	@Autowired
	private FileInventoryRepository fileInventoryRepository;
	
//	//	Total Files In Inbox
	String state=Messages.getString("OperationsDataServiceImpl.FILESTATUS"); //$NON-NLS-1$
	@Override
	public Integer getTotalFileInbox(String sauName)
	{
		logger.info("Computing Inbox Files Count for Department:"+ sauName); //$NON-NLS-1$
		List<FileInventory> inboxFiles = fileInventoryRepository.findByInitiatedBySauAndMisTypeAndTaskState(sauName,"FILE",state); //$NON-NLS-1$
		logger.info("number Of total files  ("+sauName+") = "+inboxFiles.size()); //$NON-NLS-1$ //$NON-NLS-2$
		for (FileInventory fileInventory : inboxFiles) {
			logger.info("File == "+fileInventory.getFileNumber()); //$NON-NLS-1$
		}
		return inboxFiles.size();
	}

	//	//	//	Greater Than 5 OR Less Than 10
	@Override
	public Integer getGrtFiveLessTenFile(String sauName)
	{
		logger.info("Computing Greater Than 5 OR Less Than 10 Count for Department:"+ sauName); //$NON-NLS-1$
		LocalDate currentDate = LocalDate.now();
		LocalDate threeDays = currentDate.plusDays(-10);
		LocalDate sevenDays = currentDate.plusDays(-20);
		Date threedaysbefore = Date.from(threeDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> sauData = fileInventoryRepository.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween( sauName,"FILE",state,sevendaysbefore,threedaysbefore); //$NON-NLS-1$
		logger.info("number Of files 5-10 ("+sauName+")= "+sauData.size()); //$NON-NLS-1$ //$NON-NLS-2$
		for (FileInventory fileInventory : sauData) {
			logger.info("File == "+fileInventory.getFileNumber()); //$NON-NLS-1$
		}
		List<FileInventory> sauDataFilter = new ArrayList<FileInventory>();
		sauDataFilter = sauData.stream().map(data -> {
			return data;

		}).collect(Collectors.toList());
		sauDataFilter.removeAll(Collections.singletonList(null));
		return sauDataFilter.size();
	}

	@Override
	public Integer getBeforeFiveFile(String sauName)
	{
		logger.info("Computing 0-5 Count for Department:"+ sauName); //$NON-NLS-1$
		
		LocalDate currentDate = LocalDate.now();
		LocalDate fiveDays = currentDate.plusDays(-10);
		Date fivedaysafter = Date.from(fiveDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date today=Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> sauData = fileInventoryRepository.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter( sauName,"FILE",state,fivedaysafter); //$NON-NLS-1$
		logger.info("number Of files 0-5("+sauName+") = "+sauData.size()); //$NON-NLS-1$ //$NON-NLS-2$
		for (FileInventory fileInventory : sauData) {
			logger.info("File == "+fileInventory.getFileNumber()); //$NON-NLS-1$
		}
		List<FileInventory> sauDataFilter = new ArrayList<FileInventory>();
		if(!sauData.isEmpty())
		{
			sauDataFilter = sauData.stream().map(data -> {

				return data;
			}).collect(Collectors.toList());
			sauDataFilter.removeAll(Collections.singletonList(null));
			return sauDataFilter.size();
		}
		return 0;
	}
	
	//	//  Greater Than 10
	@Override
	public Integer getGrtTenFile(String sauName)
	{
		logger.info("Computing Greater Than 10 Count for Department:"+ sauName); //$NON-NLS-1$
		LocalDate currentDate = LocalDate.now();
		LocalDate sevenDays = currentDate.plusDays(-20);
		Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		Date today=Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> sauData = fileInventoryRepository.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore( sauName,"FILE",state,sevendaysbefore); //$NON-NLS-1$
		logger.info("number Of files >10("+sauName+") = "+sauData.size()); //$NON-NLS-1$ //$NON-NLS-2$
		for (FileInventory fileInventory : sauData) {
			logger.info("File == "+fileInventory.getFileNumber()); //$NON-NLS-1$
		}
		List<FileInventory> sauDataFilter = new ArrayList<FileInventory>();
		if(!sauData.isEmpty())
		{
			sauDataFilter = sauData.stream().map(data -> {

				return data;
			}).collect(Collectors.toList());
			sauDataFilter.removeAll(Collections.singletonList(null));
			return sauDataFilter.size();
		}
		return 0;
	}


}