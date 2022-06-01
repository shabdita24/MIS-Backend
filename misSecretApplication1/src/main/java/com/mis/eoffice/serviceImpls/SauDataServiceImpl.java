package com.mis.eoffice.serviceImpls;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mis.eoffice.db1Models.FileSauInventory;
import com.mis.eoffice.db1Models.HierarchyDataInventory;
import com.mis.eoffice.db1Repo.DataSauInventoryRepository;
import com.mis.eoffice.db1Repo.FileSauInventoryRepository;
import com.mis.eoffice.service.OperationsDataService;
import com.mis.eoffice.service.SauDataService;

@Service
public class SauDataServiceImpl implements SauDataService{

	private static final Logger logger = LoggerFactory.getLogger(SauDataServiceImpl.class);

	@Autowired
	private DataSauInventoryRepository hierarchyInventoryRepository;

	@Autowired
	private FileSauInventoryRepository fileSauInventoryRepository;

	@Autowired
	private OperationsDataService operationsDataService;
	
	@Override
	public String mapSauData1()
	{
		//fileSauInventoryRepository.deleteAll();
		String command=Messages.getString("HierarchyDataServiceImpl.CMD");
		logger.info("Entered Command "+command);
		fileSauInventoryRepository.deleteByCommand(command);

		logger.info("Parsing End for Hierarchical Data Operations.");
		this.mapFileSauData1(command);
		return "OK";
	}

	public String mapFileSauData1(String command)
	{
		try
		{
			Iterable<HierarchyDataInventory> sauDat1 = hierarchyInventoryRepository.findAll();
			for(HierarchyDataInventory sauDatas:sauDat1)
			{
				Integer tenDayCount = operationsDataService.getGrtTenFile(sauDatas.getSauName());
				Integer fiveDayNTenDayCount = operationsDataService.getGrtFiveLessTenFile(sauDatas.getSauName());
				Integer zeroDayNFiveDayCount = operationsDataService.getBeforeFiveFile(sauDatas.getSauName());
				Integer inboxFilesCount = operationsDataService.getTotalFileInbox(sauDatas.getSauName());
				List<HierarchyDataInventory> childs = hierarchyInventoryRepository.findByParentNodeIdAndCommand(sauDatas.getSauId(),command);
				FileSauInventory newSau = FileSauInventory.builder().sauBranch(sauDatas.getSauName())
						.sauDisplayName(sauDatas.getDepFullName()).command(command).totalFileInbox(inboxFilesCount)
						.totalFileProcessedTenDays(tenDayCount)
						.totalFilesPendingFiveTen(fiveDayNTenDayCount).sauId(sauDatas.getSauId())
						.totalFilesPendingFive(zeroDayNFiveDayCount)
						.parentId(sauDatas.getParentNodeId())
						.build();
				if(childs.isEmpty()) {
					newSau.setHasChildren(0);
				}else {
					newSau.setHasChildren(1);
				}
				fileSauInventoryRepository.save(newSau);
			}
		}
		catch(StackOverflowError t)
		{
			logger.info("Parsing Completed for Sau Data Operations.");
		}
		return "OK";
	}
}