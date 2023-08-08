package com.mis.eoffice.serviceImpls;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mis.eoffice.db1Models.FileSauInventory;
import com.mis.eoffice.db1Models.HierarchyDataInventory;
import com.mis.eoffice.db1Repo.DataSauInventoryRepository;
import com.mis.eoffice.db1Repo.FileSauInventoryRepository;
import com.mis.eoffice.service.OperationsDataService;
import com.mis.eoffice.service.SauDataService;
import com.mis.eoffice.serviceImplsBMRL.OperationsDataServiceImplBMRL;
import com.mis.eoffice.serviceImplsKNH.OperationsDataServiceImplKNH;

@Service
public class SauDataServiceImpl implements SauDataService{
	private static final Logger logger = LoggerFactory.getLogger(SauDataServiceImpl.class);

	@Value("${command}")
	String command;

	
	@Autowired
	private DataSauInventoryRepository hierarchyInventoryRepository;

	@Autowired
	private FileSauInventoryRepository fileSauInventoryRepository;

	@Autowired
	private OperationsDataService operationsDataService;
	
	@Autowired
	private OperationsDataServiceImplBMRL operationsDataServicebmrl;
	
	@Autowired
	private OperationsDataServiceImplKNH operationsDataServiceknh;
	
	
	@Override
	public String mapSauData1()
	{
		//fileSauInventoryRepository.deleteAll();
		//String command=Messages.getString("HierarchyDataServiceImpl.CMD");
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
			Iterable<HierarchyDataInventory> sauDat1 = hierarchyInventoryRepository.findByCommand(command);
			for(HierarchyDataInventory sauDatas:sauDat1)
			{
				Integer tenDayCount ;
				Integer fiveDayNTenDayCount;
				Integer zeroDayNFiveDayCount ;
				Integer inboxFilesCount ;
				if(command.equalsIgnoreCase("AIRHQ")) {
				 tenDayCount = operationsDataService.getGrtTenFile(sauDatas.getSauName());
				 fiveDayNTenDayCount = operationsDataService.getGrtFiveLessTenFile(sauDatas.getSauName());
				 zeroDayNFiveDayCount = operationsDataService.getBeforeFiveFile(sauDatas.getSauName());
				 inboxFilesCount = operationsDataService.getTotalFileInbox(sauDatas.getSauName());}
				else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) {
					 tenDayCount = operationsDataServicebmrl.getGrtTenFile(sauDatas.getSauName());
					 fiveDayNTenDayCount = operationsDataServicebmrl.getGrtFiveLessTenFile(sauDatas.getSauName());
					 zeroDayNFiveDayCount = operationsDataServicebmrl.getBeforeFiveFile(sauDatas.getSauName());
					 inboxFilesCount = operationsDataServicebmrl.getTotalFileInbox(sauDatas.getSauName());
				}
				else {
					 tenDayCount = operationsDataServiceknh.getGrtTenFile(sauDatas.getSauName());
					 fiveDayNTenDayCount = operationsDataServiceknh.getGrtFiveLessTenFile(sauDatas.getSauName());
					 zeroDayNFiveDayCount = operationsDataServiceknh.getBeforeFiveFile(sauDatas.getSauName());
					 inboxFilesCount = operationsDataServiceknh.getTotalFileInbox(sauDatas.getSauName());
				}
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