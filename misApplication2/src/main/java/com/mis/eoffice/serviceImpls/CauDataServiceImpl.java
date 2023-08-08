package com.mis.eoffice.serviceImpls;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mis.eoffice.db1Models.FileSauBranchInventory;
import com.mis.eoffice.db1Models.FileSauInventory;
import com.mis.eoffice.db1Repo.FileSauBranchInventoryRepository;
import com.mis.eoffice.db1Repo.FileSauInventoryRepository;
import com.mis.eoffice.service.CauDataService;

@Service
public class CauDataServiceImpl implements CauDataService {

	private static final Logger logger = LoggerFactory.getLogger(CauDataServiceImpl.class);

	@Autowired
	private FileSauBranchInventoryRepository fileBranchSauInventoryRepository;

	@Autowired
	private FileSauInventoryRepository fileSauInventoryRepository;


	@Value("${command}")
	String command;

	@Override
	public String mapCauData() {
		//fileBranchSauInventoryRepository.deleteAll();
		//String command=Messages.getString("HierarchyDataServiceImpl.CMD");
		logger.info("Entered Command "+command);
		fileBranchSauInventoryRepository.deleteByCommand(command);
		List<FileSauInventory> sauDatas1 = fileSauInventoryRepository.findAll();

		List<FileSauInventory> leafNodes = fileSauInventoryRepository.findAllByHasChildrenAndCommand(0, command);
		for (FileSauInventory fileSauInventory : leafNodes) {
			FileSauBranchInventory fileSauBranchInventory = new FileSauBranchInventory();
			fileSauBranchInventory.setHasChildren(fileSauInventory.getHasChildren());
			fileSauBranchInventory.setParentId(fileSauInventory.getParentId());
			fileSauBranchInventory.setRObjectId(fileSauInventory.getRObjectId());
			fileSauBranchInventory.setSauBranch(fileSauInventory.getSauBranch());
			fileSauBranchInventory.setSauDisplayName(fileSauInventory.getSauDisplayName());
			fileSauBranchInventory.setSauId(fileSauInventory.getSauId());
			fileSauBranchInventory.setSauParent(fileSauInventory.getSauParent());
			fileSauBranchInventory.setTotalFilesPendingFiveTen(fileSauInventory.getTotalFilesPendingFiveTen());
			fileSauBranchInventory.setTotalFileProcessedTenDays(fileSauInventory.getTotalFileProcessedTenDays());
			fileSauBranchInventory.setTotalFilesPendingFive(fileSauInventory.getTotalFilesPendingFive());
			fileSauBranchInventory.setTotalFileInbox(fileSauInventory.getTotalFileInbox());
			fileSauBranchInventory.setCommand(command);

			fileBranchSauInventoryRepository.save(fileSauBranchInventory);
		}
		List<FileSauBranchInventory> allbranches = fileBranchSauInventoryRepository.findAll();
		mapFileCauData(allbranches,command);
		logger.info("Parsing End for Hierarchical Data Operations.");
		return "OK";
	}
	
	
	public void mapFileCauData(List<FileSauBranchInventory> sauDatas, String command) {
		System.out.println("mApfile");
		for (int i = 0; i < sauDatas.size(); i++) {
			System.out.println(sauDatas.get(i).getSauBranch());
			System.out.println("size"+sauDatas.size());
			Optional<FileSauInventory> doesParentExist = fileSauInventoryRepository.findBySauIdAndCommand(sauDatas.get(i).getParentId(),command);
//			System.out.println(doesParentExist.get().getSauBranch());
			if (doesParentExist.isPresent()) {
				List<FileSauInventory> childs = fileSauInventoryRepository.findByParentIdAndCommand(doesParentExist.get().getSauId(),command);
				List<FileSauBranchInventory> cauData = fileBranchSauInventoryRepository.findAll();
				int count=0;
				Optional<FileSauBranchInventory> parentInCau = fileBranchSauInventoryRepository.findBySauIdAndCommand(doesParentExist.get().getSauId(),command);
				boolean parentInCauPresent=false;
				if(parentInCau.isPresent()) {
					parentInCauPresent=true;
				}
				for (FileSauInventory fileSauInventory : childs) {//A,B,D,E
					System.err.println("check = "+fileSauInventory.getSauId());
					for (FileSauBranchInventory fileSauBranchInventory : cauData) {//S, Z, B, P, J, E, G
						System.out.println(fileSauBranchInventory.getSauId());
						
						if (fileSauInventory.getSauId().equals(fileSauBranchInventory.getSauId())) {
							count++;
							System.out.println("count == "+count);
						}
					}
				}
				System.out.println("does parent exist "+parentInCau.isPresent());
				int total=0;
				int zeroToFive=0;
				int fiveToTen=0;
				int grtThanTen=0;
				
				if(!parentInCauPresent && childs.size()==count) {
					for (FileSauInventory fileSauBranchInventory : childs) {
						FileSauBranchInventory fileSauBranchInventory2 = fileBranchSauInventoryRepository.findBySauIdAndCommand(fileSauBranchInventory.getSauId(),command).get();
						total+=fileSauBranchInventory2.getTotalFileInbox();
						fiveToTen+=fileSauBranchInventory2.getTotalFilesPendingFiveTen();
						zeroToFive+=fileSauBranchInventory2.getTotalFilesPendingFive();
						grtThanTen+=fileSauBranchInventory2.getTotalFileProcessedTenDays();
					}
					FileSauBranchInventory fileSauBranchInventory = new FileSauBranchInventory();
					fileSauBranchInventory.setHasChildren(doesParentExist.get().getHasChildren());
					fileSauBranchInventory.setParentId(doesParentExist.get().getParentId());
					fileSauBranchInventory.setRObjectId(doesParentExist.get().getRObjectId());
					fileSauBranchInventory.setSauBranch(doesParentExist.get().getSauBranch());
					fileSauBranchInventory.setSauDisplayName(doesParentExist.get().getSauDisplayName());
					fileSauBranchInventory.setSauId(doesParentExist.get().getSauId());
					fileSauBranchInventory.setSauParent(doesParentExist.get().getSauParent());
					fileSauBranchInventory.setTotalFilesPendingFive(doesParentExist.get().getTotalFilesPendingFive()+zeroToFive);
					fileSauBranchInventory.setTotalFilesPendingFiveTen(doesParentExist.get().getTotalFilesPendingFiveTen()+fiveToTen);
					fileSauBranchInventory.setTotalFileProcessedTenDays(doesParentExist.get().getTotalFileProcessedTenDays()+grtThanTen);
					fileSauBranchInventory.setTotalFileInbox(doesParentExist.get().getTotalFileInbox()+total);
					fileSauBranchInventory.setCommand(command);
					fileBranchSauInventoryRepository.save(fileSauBranchInventory);
					sauDatas.add(fileSauBranchInventory);
					
					
				}
			}
		}		
	}
}