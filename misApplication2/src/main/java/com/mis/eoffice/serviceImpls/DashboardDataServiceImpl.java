package com.mis.eoffice.serviceImpls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mis.eoffice.db1Models.FileSauBranchInventory;
import com.mis.eoffice.db1Models.FileSauInventory;
import com.mis.eoffice.db1Repo.DataSauInventoryRepository;
import com.mis.eoffice.db1Repo.FileSauBranchInventoryRepository;
import com.mis.eoffice.db1Repo.FileSauInventoryRepository;
import com.mis.eoffice.dto.SauBranchFiltDto;
import com.mis.eoffice.service.DashboardDataService;

@Service
public class DashboardDataServiceImpl implements DashboardDataService{

	private static final Logger logger = LoggerFactory.getLogger(DashboardDataServiceImpl.class);

	@Autowired
	private DataSauInventoryRepository dataSauInventoryRepository;

	@Autowired
	private FileSauInventoryRepository fileSauInventoryRepository;

	Map<String,String> sauData = new HashMap<String,String>();
	
	@Override
	public Map<String,String> parseDeptData(String sau,String sauId) {
		sauData.clear();
		String command=dataSauInventoryRepository.findCommandbySauName(sau);
		logger.info("command "+command);
		int sauIdd = Integer.parseInt(sauId);
		logger.info("Parsing Start for Department (Dashboard):"+ sau);
		if(sauIdd==0)
		{
			Optional<FileSauInventory> sauuData = fileSauInventoryRepository.findBySauBranchAndCommand(sau,command);
			if( sauuData.isPresent())
			{
				sauData.put("sauBranch",sau);
				sauData.put("sauDisplayName",sauuData.get().getSauDisplayName());
				sauData.put("totalCau",sauuData.get().getTotalFileInbox().toString());
				sauData.put("totalFilePendingTenDaysCau",sauuData.get().getTotalFileProcessedTenDays().toString());
				sauData.put("totalFilesPendingFiveTenCau",sauuData.get().getTotalFilesPendingFiveTen().toString());
				sauData.put("totalFilesPendingFiveCau",sauuData.get().getTotalFilesPendingFive().toString());
				
				logger.info("Parsing End for Department (Dashboard):"+ sau);
				return sauData;
			}
		}
		logger.info("Parsing Error for Department (Dashboard):"+ sau);
		return  new HashMap<String,String>();
	}

	@Override
	public List<SauBranchFiltDto> parseMainData(String sau,String sauId) {
		int sauIdd = Integer.parseInt(sauId);
		String command=dataSauInventoryRepository.findCommandbySauName(sau);
		logger.info("command "+command);
		logger.info("Parsing Start for Department (Dashboard):"+ sau);
		List<SauBranchFiltDto> newData = new ArrayList<SauBranchFiltDto>();
		
		Optional<FileSauInventory> sauData = fileSauInventoryRepository.findBySauBranchAndCommand(sau, command);
		if(sauData.isPresent()) {
			System.out.println("total = "+sauData.get().getTotalFileInbox());
			SauBranchFiltDto neww = SauBranchFiltDto.builder().fileId(sauData.get().getFileId())
			.parentId(0)
			.sauBranch(sauData.get().getSauBranch())
			.sauDisplayName(sauData.get().getSauDisplayName())
			.sauId(sauData.get().getSauId())		
			.sauParent(sauData.get().getSauParent())
			.totalSau(sauData.get().getTotalFileInbox())
			.hasItems(sauData.get().getHasChildren()==1).build();
			newData.add(neww);
			return newData;
		}
		logger.info("Parsing Error for Department (Dashboard):"+ sau);
		return  new ArrayList<SauBranchFiltDto>();
	}
}