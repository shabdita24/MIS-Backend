package com.mis.eoffice.serviceImpls;

import java.util.List;
import java.util.Optional;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.mis.eoffice.db1Models.HierarchyDataInventory;
import com.mis.eoffice.db1Repo.DataSauInventoryRepository;
import com.mis.eoffice.db2Models.DepartmentConfigInventory;
import com.mis.eoffice.db2Repo.DepartmentConfigRepository;
import com.mis.eoffice.service.HierarchyDataService;

@Service
public class HierarchyDataServiceImpl implements HierarchyDataService{

	private static final Logger logger = LoggerFactory.getLogger(HierarchyDataServiceImpl.class);

	@Value("${command}")
	String command;

	
	@Autowired
	private DepartmentConfigRepository departmentConfigRepository;

	@Autowired
	private DataSauInventoryRepository dataSauInventoryRepository;

	@Override
	public JSONObject InsertSauDataInventory()
	{
		//dataSauInventoryRepository.deleteAll();
		// Query Fetch iaf_department_config_s table with where condition dep_name_small='AIRHQ'
		//String command=Messages.getString("HierarchyDataServiceImpl.CMD");
		logger.info("Entered Command "+command);
		dataSauInventoryRepository.deleteByCommand(command);

		List<DepartmentConfigInventory> dataNew = departmentConfigRepository.findByDepNameSmallContains(command); //$NON-NLS-1$
		logger.info("Parsing Data for Hierachical Table Start."); //$NON-NLS-1$
		JSONObject json = new JSONObject();
		//Recursion
		for(int i=0;i<dataNew.size();i++)
			logger.info("data in list "+dataNew.get(i));
		this.getSauBranchInventory(dataNew,command);
		logger.info("Parsing Data for Hierachical Table Error."); //$NON-NLS-1$
		json.put("status", HttpStatus.OK); //$NON-NLS-1$
		return json;
	}

	//Getting Sub Branches
	public void getSauBranchInventory(List<DepartmentConfigInventory> dataNew, String command)
	{
		if(dataNew.iterator().hasNext())
		{
			dataNew.forEach((result)->
			{
				// If any data does not return r-object_id then its considered as parent
				Optional<HierarchyDataInventory> chkData1 = dataSauInventoryRepository.findByrObjectIdAndCommand(result.getRObjectId(),command);
				if(!chkData1.isPresent())
				{
					System.out.println("inside hierarchy");
					logger.info("ObjectId "+result.getRObjectId());
					//Checking parent by dep_name_small from iaf_department_config_s table
					logger.info("DepName "+result.getDepNameSmall().toUpperCase());
					Optional<HierarchyDataInventory> chkParent = dataSauInventoryRepository.findBySauNameAndCommand(result.getDepNameSmall().toUpperCase(),command);
					if(!chkParent.isPresent())
					{
						logger.info(" sauName is not present in hierarchy "+result.getDepNameSmall().toUpperCase());

						// Checking if parent dep_name_small is not empty Case
						if(!result.getDepNameSmall().equals(" ") && !result.getDepNameSmall().equals("")) //$NON-NLS-1$ //$NON-NLS-2$
						{
							logger.info("Parsing Data for Sau inside hierarchy");
							Optional<DepartmentConfigInventory> fullName = departmentConfigRepository.findByDepBranch(result.getDepNameSmall());
							// Checking if its Branch corresponding to dep_name_small is existing or not
							if(fullName.isPresent())
							{
								logger.info(result.getDepNameSmall()+" sau branch is present in iaf department config ");

								// Saving Parent Data with parent_node_id = 0 sequence = 0 and r_object_id =null
								HierarchyDataInventory sauDataCreate =  HierarchyDataInventory.builder().rObjectId(null).command(command).sauName(result.getDepNameSmall().toUpperCase()).parentNodeId(0)
										.sequence(0).depFullName(fullName.get().getDepFullName()).build();
								dataSauInventoryRepository.save(sauDataCreate);
							}
							else
							{
								logger.info(result.getDepNameSmall()+" sau branch is not present in iaf department config ");

								// If fullname is not existing in iaf_department_config_s table then Save empty 
								HierarchyDataInventory sauDataCreate =  HierarchyDataInventory.builder().rObjectId(null).command(command).sauName(result.getDepNameSmall().toUpperCase()).parentNodeId(0)
										.sequence(0).depFullName("").build(); //$NON-NLS-1$
								dataSauInventoryRepository.save(sauDataCreate);
							}
						}
					}
					Optional<HierarchyDataInventory> chkParent1 = dataSauInventoryRepository.findBySauNameAndCommand(result.getDepNameSmall().toUpperCase(),command);
					// If parent is present then checking its Child 
					logger.info(" If parent is present then checking its Child ");

					if(chkParent1.isPresent())
					{
						logger.info("Parsing Data for Sau."); //$NON-NLS-1$
						//Checking child by dep_branch_name from iaf_department_config_s table
						Optional<HierarchyDataInventory> chkChild1 = dataSauInventoryRepository.findBySauNameAndCommand(result.getDepBranch(),command);
						logger.info(result.getDepBranch());
						if(chkChild1.isPresent())
						{
							logger.info("Child is present.");

							// If child is present then save the data and check for the sauId with respect to parent saved in hierarchy_data_inventory
							//and parent_node_id as sauId of parent sau and setting sequence counter for particular child under parent
							if(chkChild1.get().getParentNodeId() == 0)
							{
								//Checking if already parent of some child
								List<HierarchyDataInventory> seqs = dataSauInventoryRepository.findByParentNodeIdAndCommand(chkParent1.get().getSauId(),command);

								chkChild1.get().setParentNodeId(chkParent1.get().getSauId());
								chkChild1.get().setSequence(seqs.size()+1);

								dataSauInventoryRepository.save(chkChild1.get());	
							}
						}
						if(!chkChild1.isPresent())
						{
							logger.info("Parsing Data for Sau Branch."); //$NON-NLS-1$
							//
							List<HierarchyDataInventory> seqs = dataSauInventoryRepository.findByParentNodeIdAndCommand(chkParent1.get().getSauId(),command);
							logger.info("Check 1."); //$NON-NLS-1$
							HierarchyDataInventory sauChildCreate =  HierarchyDataInventory.builder().rObjectId(result.getRObjectId()).sauName(result.getDepBranch().toUpperCase()).parentNodeId(chkParent1.get().getSauId())
									.sequence(seqs.size()+1).command(command).depFullName(result.getDepFullName()).build();
							logger.info("Check 2."); //$NON-NLS-1$
							dataSauInventoryRepository.save(sauChildCreate);
							logger.info("Check 3."); //$NON-NLS-1$
						}
						List<DepartmentConfigInventory> newData = departmentConfigRepository.findByDepNameSmall(result.getDepBranch());
						this.getSauBranchInventory(newData,command);
					}
				}
			});
			logger.info("Parsing Data for Hierachical Table End."); //$NON-NLS-1$
		}
	}

}