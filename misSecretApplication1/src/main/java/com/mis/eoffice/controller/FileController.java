package com.mis.eoffice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mis.eoffice.db1Repo.DataSauInventoryRepository;
import com.mis.eoffice.dto.DetailedTable;
import com.mis.eoffice.dto.SauBranchFiltDto;
import com.mis.eoffice.service.CauDataService;
import com.mis.eoffice.service.DashboardDataService;
import com.mis.eoffice.service.DetailedTableService;
import com.mis.eoffice.service.HierarchyDataService;
import com.mis.eoffice.service.MainTableDataService;
import com.mis.eoffice.service.SauDataService;
import com.mis.eoffice.serviceImplsBMRL.DetailedTableServiceImplBMRL;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired 
	private DetailedTableService ds;
	@Autowired 
	private DetailedTableServiceImplBMRL dsbmrl;
	@Autowired
	private HierarchyDataService hierarchyDataService;

	@Autowired
	private SauDataService sauDataService;

	@Autowired
	private CauDataService cauDataService;

	@Autowired
	private MainTableDataService mainTableDataService;

	@Autowired
	private DashboardDataService dashboardDataService;
	@Autowired
	private DataSauInventoryRepository dataSauInventoryRepository;

	// Already Pushed

	// First Order
	@GetMapping("/hierarchyData")
	private ResponseEntity<JSONObject> pushSauData()
	{
		JSONObject json = hierarchyDataService.InsertSauDataInventory();
		logger.info("**********Hierarchy Data Pushed**********");
		return ResponseEntity.ok(json);
	}

	// Third Order
	@GetMapping("/pushCauData")
	public ResponseEntity<String> getCauData()
	{
		cauDataService.mapCauData();
		logger.info("**********Data Pushed**********");
		return ResponseEntity.ok("OK");
	}
	// Second Order
	@GetMapping("/pushSauData")
	public ResponseEntity<String> getSauData()
	{
		sauDataService.mapSauData1();
		logger.info("**********Data Pushed**********");
		return ResponseEntity.ok("OK");
	}

	//	Get Data
	//	Main Table Data //table 
	@GetMapping("/getSauData")
	public ResponseEntity<List<SauBranchFiltDto>> getSauData(HttpServletRequest request)
	{
		String grpName = (String) request.getHeader("groupName");
		String sauId = (String) request.getHeader("sauId");
		String command=dataSauInventoryRepository.findCommandbySauName(grpName.toUpperCase());
		logger.info("command "+command);
		List<SauBranchFiltDto> json = mainTableDataService.parseSauBranches(grpName,sauId,command);
		JSONObject json1 = new JSONObject();
		for (SauBranchFiltDto sauBranchFiltDto : json) {
			System.out.println("---"+sauBranchFiltDto);
		}
		json1.put("data",json);
		return ResponseEntity.ok(json);
	}
	@GetMapping("/getCauData")
	public ResponseEntity<List<SauBranchFiltDto>> getCauData(HttpServletRequest request)
	{
		String grpName = (String) request.getHeader("groupName");
		String sauId = (String) request.getHeader("sauId");
		String command=dataSauInventoryRepository.findCommandbySauName(grpName.toUpperCase());
		logger.info("command "+command);
		List<SauBranchFiltDto> json = mainTableDataService.parseSauBranches(grpName,sauId,command);
		JSONObject json1 = new JSONObject();
		for (SauBranchFiltDto sauBranchFiltDto : json) {
			System.out.println("---"+sauBranchFiltDto);
		}
		json1.put("data",json);
		return ResponseEntity.ok(json);
	}
	//	DashBoard API
	//	
	@GetMapping("/getMainData")
	public ResponseEntity<JSONObject> getMainData(HttpServletRequest request)
	{
		String grpName = (String) request.getHeader("groupName");
		String sauId = (String) request.getHeader("sauId");
		//		List<SauBranchFiltDto> json = dashboardDataService.parseMainData(grpName,sauId);
		List<SauBranchFiltDto>  mainData= dashboardDataService.parseMainData(grpName,sauId);
		JSONObject json1 = new JSONObject();
		json1.put("data",mainData);
		return ResponseEntity.ok(json1);
	}

	//	DashBoard API	//Card
	@GetMapping("/getDeptData")
	public ResponseEntity<Map<String,String>> getDeptData(HttpServletRequest request)
	{
		String grpName = (String) request.getHeader("groupName");
		String sauId = (String) request.getHeader("sauId");
		Map<String,String> json = dashboardDataService.parseDeptData(grpName,sauId);
		return ResponseEntity.ok(json);
	}

	//  Shabdita
	@PostMapping("/gettotalfiles")
	private ResponseEntity<JSONObject> gettotalfiles(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
		List<DetailedTable> dt=new ArrayList<DetailedTable>();
		String command=dataSauInventoryRepository.findCommandbySauName(sau.toUpperCase());
		logger.info("command "+command);
		logger.info("********Detailed Table********");

		if(column.equals("totalSau") && num>0) 
		{		
			if(command.equalsIgnoreCase("AIRHQ") || command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				dt=ds.getdetailedtableInboxFile(sau.toUpperCase(),num,command);
			else
				dt=dsbmrl.getdetailedtableInboxFile(sau.toUpperCase(),num,command);
		}
		else if(column.equalsIgnoreCase("totalFilesPendingFiveTenSau") && num>0)
		{
			if(command.equalsIgnoreCase("AIRHQ") || command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				dt=ds.getdetailedtablependingatfiveten(sau.toUpperCase(),num,command);
			else
				dt=dsbmrl.getdetailedtablependingatfiveten(sau.toUpperCase(),num,command);
		}
		else if(column.equalsIgnoreCase("totalFilePendingTenDaysSau") && num>0)
		{

			if(command.equalsIgnoreCase("AIRHQ") || command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				dt=ds.getdetailedtablependingatten(sau.toUpperCase(),num,command);
			else
				dt=dsbmrl.getdetailedtablependingatten(sau.toUpperCase(),num,command);
		}
		else if(column.equalsIgnoreCase("totalFilesPendingFiveSau") && num>0)
		{

			if(command.equalsIgnoreCase("AIRHQ") || command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				dt=ds.getdetailedtablependingatfive(sau.toUpperCase(),num,command);
			else
				dt=dsbmrl.getdetailedtablependingatfive(sau.toUpperCase(),num,command);

		}
		else if(column.equals("totalCau") && num>0) 
		{

			if(command.equalsIgnoreCase("AIRHQ") || command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				dt=ds.getdetailedtableInboxFileCau(sau.toUpperCase(),num,command);
			else
				dt=dsbmrl.getdetailedtableInboxFileCau(sau.toUpperCase(),num,command);
		}
		else if(column.equalsIgnoreCase("totalFilesPendingFiveTenCau") && num>0)
		{	

			if(command.equalsIgnoreCase("AIRHQ") || command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				dt=ds.getdetailedtablepend37(sau.toUpperCase(),num,command);
			else
				dt=dsbmrl.getdetailedtablepend37(sau.toUpperCase(),num,command);
		}
		else if(column.equalsIgnoreCase("totalFilePendingTenDaysCau") && num>0)
		{

			if(command.equalsIgnoreCase("AIRHQ") || command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				dt=ds.getdetailedtablepro30days(sau.toUpperCase(),num,command);
			else
				dt=dsbmrl.getdetailedtablepro30days(sau.toUpperCase(),num,command);

		}
		else if(column.equalsIgnoreCase("totalFilesPendingFiveCau") && num>0)
		{	
			if(command.equalsIgnoreCase("AIRHQ") || command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				dt=ds.getdetailedtablependingatzerofive(sau.toUpperCase(),num,command);
			else
				dt=dsbmrl.getdetailedtablependingatzerofive(sau.toUpperCase(),num,command);
		}
		else
		{
			dt=new ArrayList<DetailedTable>();
		}
		System.out.println("dt2 size "+dt.size());
		logger.info("********Detailed Table Processed********");
		JSONObject json = new JSONObject();
		json.put("status", HttpStatus.OK);
		json.put("Data",dt );
		json.put("size", dt.size());
		return ResponseEntity.ok(json);

	}

}
