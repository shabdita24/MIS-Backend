package com.mis.eoffice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.mis.eoffice.db1Repo.DataSauInventoryRepository;
import com.mis.eoffice.dto.DetailedTable;
import com.mis.eoffice.dto.ResponseDetailTable;
import com.mis.eoffice.dto.SauBranchFiltDto;
import com.mis.eoffice.service.CauDataService;
import com.mis.eoffice.service.DashboardDataService;
import com.mis.eoffice.service.DetailedTableService;
import com.mis.eoffice.service.HierarchyDataService;
import com.mis.eoffice.service.MainTableDataService;
import com.mis.eoffice.service.SauDataService;
import com.mis.eoffice.serviceImplsBMRL.DetailedTableServiceImplBMRL;
import com.mis.eoffice.serviceImplsKNH.DetailedTableServiceImplKNH;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class FileController {
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@Value("${userRoles.apiurl}")
	String apiUrl;
	@Value("${userRoles.pathparam}")
	String pathParam;
	
	@Autowired 
	private DetailedTableService ds;

	@Autowired 
	private DetailedTableServiceImplBMRL dsbmrl;

	@Autowired 
	private DetailedTableServiceImplKNH dsknh;

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
	
	@GetMapping("/getUserRoles")
	public ResponseEntity<JSONObject> getUserRoles(HttpServletRequest request) {
		String token = (String) request.getHeader("Authorization");
		//logger.info("token "+token);
		String serviceNumber = request.getHeader("username");  //$NON-NLS-1$
		logger.info("serviceNumber recieved in username field "+serviceNumber); //$NON-NLS-1$
		Map<String, Object> params = new HashMap<>();
		params.put(pathParam, serviceNumber); //$NON-NLS-1$
		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl); //$NON-NLS-1$
		for (Entry<String, Object> entry : params.entrySet()) {
			builder.queryParam(entry.getKey(), entry.getValue());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		headers.set("Authorization", token); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		//$NON-NLS-1$ //$NON-NLS-2$
		logger.info("params"+ params);  //$NON-NLS-1$
		ResponseEntity<List<JSONObject>> response = restTemplate.exchange(
				builder.toUriString(), 
				HttpMethod.GET, 
				new HttpEntity(headers),
				new ParameterizedTypeReference<List<JSONObject>>() {});
		List<JSONObject> resbody=response.getBody();
		logger.info("resbody "+resbody); //$NON-NLS-1$
	
		List<String> rolesList = new ArrayList<String>();
		for(JSONObject jo:resbody) {
			logger.info("sectionId "+jo.get("sectionId")); //$NON-NLS-1$ //$NON-NLS-2$
			String grpName=(String) jo.get("sectionId");  //$NON-NLS-1$
			logger.info("sectionId received "+grpName);  //$NON-NLS-1$
			rolesList.add(grpName);

		}
		logger.info("rolesList "+rolesList); //$NON-NLS-1$
		
		JSONObject json = new JSONObject();
		json.put("status", HttpStatus.OK); //$NON-NLS-1$
		json.put("data", rolesList); //$NON-NLS-1$
		return ResponseEntity.ok(json);

	}
	@GetMapping("/hello")
	private ResponseEntity<List<JSONObject>> greeting(@PathParam("ser_no") String ser_no,HttpServletRequest request){
		logger.info("ser_no "+ser_no);
		JSONObject json = new JSONObject();
		List<JSONObject> list=new ArrayList<JSONObject>();
		json.put("sectionId",ser_no);
		list.add(json);
		return ResponseEntity.ok(list);
	}

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
		logger.info("SauBranchFiltDto List "+mainData);
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
	private ResponseEntity<JSONObject> gettotalfiles(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num,
			@PathParam("currentPage") Integer currentPage,@PathParam("pageSize") Integer pageSize){
		List<DetailedTable> dt=new ArrayList<DetailedTable>();
		ResponseDetailTable rdt=new ResponseDetailTable();
		String command=dataSauInventoryRepository.findCommandbySauName(sau.toUpperCase());
		logger.info("command "+command);
		logger.info("********Detailed Table********");

		if(column.equals("totalSau") && num>0) 
		{
			if(command.equalsIgnoreCase("AIRHQ")) 
				rdt=ds.getdetailedtableInboxFile(sau.toUpperCase(),num,command,currentPage,pageSize);
			else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				rdt=dsbmrl.getdetailedtableInboxFile(sau.toUpperCase(),num,command,currentPage,pageSize);
			else
				rdt=dsknh.getdetailedtableInboxFile(sau.toUpperCase(),num,command,currentPage,pageSize);
		}
		else if(column.equalsIgnoreCase("totalFilesPendingFiveTenSau") && num>0)
		{
			if(command.equalsIgnoreCase("AIRHQ")) 
				rdt=ds.getdetailedtablependingat1020(sau.toUpperCase(),num,command,currentPage,pageSize);
			else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				rdt=dsbmrl.getdetailedtablependingat1020(sau.toUpperCase(),num,command,currentPage,pageSize);
			else
				rdt=dsknh.getdetailedtablependingat1020(sau.toUpperCase(),num,command,currentPage,pageSize);
		}
		else if(column.equalsIgnoreCase("totalFilePendingTenDaysSau") && num>0)
		{
			if(command.equalsIgnoreCase("AIRHQ")) 
				rdt=ds.getdetailedtablependingatten(sau.toUpperCase(),num,command,currentPage,pageSize);
			else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				rdt=dsbmrl.getdetailedtablependingatten(sau.toUpperCase(),num,command,currentPage,pageSize);
			else
				rdt=dsknh.getdetailedtablependingatten(sau.toUpperCase(),num,command,currentPage,pageSize);
		}
		else if(column.equalsIgnoreCase("totalFilesPendingFiveSau") && num>0)
		{
			if(command.equalsIgnoreCase("AIRHQ")) 
				rdt=ds.getdetailedtablependingatfive(sau.toUpperCase(),num,command,currentPage,pageSize);
			else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				rdt=dsbmrl.getdetailedtablependingatfive(sau.toUpperCase(),num,command,currentPage,pageSize);
			else
				rdt=dsknh.getdetailedtablependingatfive(sau.toUpperCase(),num,command,currentPage,pageSize);
			
		}
		else if(column.equals("totalCau") && num>0) 
		{
			if(command.equalsIgnoreCase("AIRHQ")) 
				rdt=ds.getdetailedtableInboxFileCau(sau.toUpperCase(),num,command,currentPage,pageSize);
			else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				rdt=dsbmrl.getdetailedtableInboxFileCau(sau.toUpperCase(),num,command,currentPage,pageSize);
			else
				rdt=dsknh.getdetailedtableInboxFileCau(sau.toUpperCase(),num,command,currentPage,pageSize);
		}
		else if(column.equalsIgnoreCase("totalFilesPendingFiveTenCau") && num>0)
		{	
			if(command.equalsIgnoreCase("AIRHQ")) 
				rdt=ds.getdetailedtablepend37(sau.toUpperCase(),num,command,currentPage,pageSize);
			else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				rdt=dsbmrl.getdetailedtablepend37(sau.toUpperCase(),num,command,currentPage,pageSize);
			else
				rdt=dsknh.getdetailedtablepend37(sau.toUpperCase(),num,command,currentPage,pageSize);
		}
		else if(column.equalsIgnoreCase("totalFilePendingTenDaysCau") && num>0)
		{
			if(command.equalsIgnoreCase("AIRHQ")) 
				rdt=ds.getdetailedtablepro30days(sau.toUpperCase(),num,command,currentPage,pageSize);
			else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				rdt=dsbmrl.getdetailedtablepro30days(sau.toUpperCase(),num,command,currentPage,pageSize);
			else
				rdt=dsknh.getdetailedtablepro30days(sau.toUpperCase(),num,command,currentPage,pageSize);
			
		}
		else if(column.equalsIgnoreCase("totalFilesPendingFiveCau") && num>0)
		{
			if(command.equalsIgnoreCase("AIRHQ")) 
				rdt=ds.getdetailedtablependingGreater20(sau.toUpperCase(),num,command,currentPage,pageSize);
			else if(command.equalsIgnoreCase("WAC") || command.equalsIgnoreCase("EAC") || command.equalsIgnoreCase("CAC") ) 
				rdt=dsbmrl.getdetailedtablependingGreater20(sau.toUpperCase(),num,command,currentPage,pageSize);
			else
				rdt=dsknh.getdetailedtablependingGreater20(sau.toUpperCase(),num,command,currentPage,pageSize);
		}
		else
		{
			dt=new ArrayList<DetailedTable>();
		}
		System.out.println("dt2 size "+dt.size());
		logger.info("********Detailed Table Processed********");
		JSONObject json = new JSONObject();
		json.put("status", HttpStatus.OK);
		json.put("Data",rdt.getDt() );
		json.put("size", rdt.getSizeDt());
		return ResponseEntity.ok(json);

	}

}
