package com.mis.eoffice.serviceImpls;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mis.eoffice.db1Models.FileSauBranchInventory;
import com.mis.eoffice.db1Models.FileSauInventory;
import com.mis.eoffice.db1Repo.FileSauBranchInventoryRepository;
import com.mis.eoffice.db1Repo.FileSauInventoryRepository;
import com.mis.eoffice.dto.SauBranchFiltDto;
import com.mis.eoffice.service.MainTableDataService;

@Service
public class MainTableDataServiceImpl implements MainTableDataService{

	private static final Logger logger = LoggerFactory.getLogger(MainTableDataServiceImpl.class);

	@Autowired
	private FileSauBranchInventoryRepository fileBranchSauInventoryRepository;

	@Autowired
	private FileSauInventoryRepository fileSauInventoryRepository;

	
	@Override
	public List<SauBranchFiltDto> parseSauBranches(String sau,String sauId,String command) {
		int sauIdd = Integer.parseInt(sauId);
		System.out.println("sauId == "+sauIdd);
		logger.info("Parsing Start for Department :"+ sau);
		List<SauBranchFiltDto> newData = new ArrayList<SauBranchFiltDto>();
		Optional<FileSauInventory> sauData = fileSauInventoryRepository.findBySauBranchAndCommand(sau,command);
		Optional<FileSauBranchInventory> branchData = fileBranchSauInventoryRepository.findBySauBranchAndCommand(sau,command);
		String parentid=branchData.get().getParentId().toString();

		if(sauIdd==0)
		{
			if(branchData.isPresent())
			{
				System.out.println("------> "+branchData.get());
				System.out.println("-------> "+sauData.get());
				List<FileSauBranchInventory> listData = fileBranchSauInventoryRepository.findByParentIdAndCommand(branchData.get().getParentId(),command);
				if(!listData.isEmpty())
				{
					branchData.get().setParentId(0);
					boolean ye = true;
					if(branchData.get().getHasChildren() == 1)
					{

						ye = true;
					}
					if(branchData.get().getHasChildren() == 0)
					{
						ye = false;
					}
					SauBranchFiltDto neww = SauBranchFiltDto.builder().fileId(branchData.get().getFileId())
							.parentId(0)
							.sauBranch(branchData.get().getSauBranch())
							.sauDisplayName(sauData.get().getSauDisplayName()+" BRANCH")
							.sauId(branchData.get().getSauId())
							.sauParent(branchData.get().getSauParent())
						    .rObjectId(parentid)

							.totalFilesPendingFiveTenCau(branchData.get().getTotalFilesPendingFiveTen())
							.totalFilePendingTenDaysCau(branchData.get().getTotalFileProcessedTenDays())
							.totalFilesPendingFiveCau(branchData.get().getTotalFilesPendingFive())
							.totalCau(branchData.get().getTotalFileInbox())
							.totalFilePendingTenDaysSau(sauData.get().getTotalFileProcessedTenDays())
							.totalFilesPendingFiveTenSau(sauData.get().getTotalFilesPendingFiveTen())
							.totalFilesPendingFiveSau(sauData.get().getTotalFilesPendingFive())
							.totalSau(sauData.get().getTotalFileInbox())
							.hasItems(ye).build();

					newData.add(neww);

					return newData;
				}
			}
		}
		else
		{
			
			List<FileSauInventory> listData = fileSauInventoryRepository.findByParentIdAndCommand(sauIdd,command);
			Optional<FileSauInventory> sausData = fileSauInventoryRepository.findBySauIdAndCommand(Integer.parseInt(sauId),command);
			if(!listData.isEmpty())
			{

				List<SauBranchFiltDto> newData1 =  listData.stream().map(data ->{
					
					System.out.println(data.getSauDisplayName());
					SauBranchFiltDto neww = SauBranchFiltDto.builder().fileId(data.getFileId())
							.parentId(data.getParentId())
						    .rObjectId(parentid)

							.sauBranch(data.getSauBranch())
							.sauDisplayName(data.getSauDisplayName())
							.sauId(data.getSauId())
							.sauParent(data.getSauParent())
							.totalFilesPendingFiveSau(data.getTotalFilesPendingFive())
							.totalFilesPendingFiveTenSau(data.getTotalFilesPendingFiveTen())
							.totalFilePendingTenDaysSau(data.getTotalFileProcessedTenDays())
							.totalSau(data.getTotalFileInbox())
							.build();

					Optional<FileSauBranchInventory> branchesOptional = fileBranchSauInventoryRepository.findBySauIdAndCommand(data.getSauId(),command);
					if(branchesOptional.isPresent()) {
						neww.setHasItems(branchesOptional.get().getHasChildren()==1);
						neww.setTotalCau(branchesOptional.get().getTotalFileInbox());
						neww.setTotalFilesPendingFiveCau(branchesOptional.get().getTotalFilesPendingFive());
						neww.setTotalFilePendingTenDaysCau(branchesOptional.get().getTotalFileProcessedTenDays());
						neww.setTotalFilesPendingFiveTenCau(branchesOptional.get().getTotalFilesPendingFiveTen());
					}
					
					return neww;
				}).collect(Collectors.toList());
				logger.info("Parsing End for Department :"+ sau);
				return newData1;
			}
		}
		logger.info("Parsing Error for Department :"+ sau);
		return  new ArrayList<SauBranchFiltDto>();
	}
}