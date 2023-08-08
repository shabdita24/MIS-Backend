package com.mis.eoffice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.jasypt.encryption.StringEncryptor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;



@CrossOrigin("*")
@RestController
@RequestMapping("/apis")
public class MasterDataController {
	private static final Logger logger = LoggerFactory.getLogger(MasterDataController.class);

	 @Autowired
	 @Qualifier("jasyptStringEncryptor")
	 private StringEncryptor encryptor;
	 
	@GetMapping("/getEncryptedValue")
	public String encryptString(@PathParam("value") String value) {
		String result=encryptor.encrypt(value);
		logger.info("received value "+value);
		logger.info("encrypted value "+result);
		return result;
	}
	@GetMapping("/getDecryptedValue")
	public String decryptString(@PathParam("value") String value) {
		//logger.info("encryptor "+encryptor);
		String result=encryptor.decrypt(value);
		logger.info("received value "+value);
		logger.info("decrypted value "+result);
		return result;
	}
//
//	@Autowired
//	private KeycloakRolsRepository keycloakRolsRepository;
//
//	@Autowired
//	private KeycloakClientRepository keycloakClientRepository;
//
//	@Autowired
//	private KeycloakGroupRepository keycloakGroupRepository;
//
//	@Autowired
//	private KeycloakGroupRoleMappingRepository keycloakGroupRoleMappingRepository;

	
	@GetMapping("/getUserRolesTest")
	public ResponseEntity<JSONObject> getUserRolesTest(HttpServletRequest request) {
		String serviceNumber = request.getHeader("username"); 
		logger.info("serviceNumber recieved in username field "+serviceNumber);
		List<String> rolesList = new ArrayList<String>();

//		Map<String, Object> params = new HashMap<>();
//		params.put("service_number", serviceNumber);
//		RestTemplate restTemplate = new RestTemplate();
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MasterDataController.APIURL")); //$NON-NLS-1$
//		for (Entry<String, Object> entry : params.entrySet()) {
//			builder.queryParam(entry.getKey(), entry.getValue());
//		}
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Accept", "application/json");
//		logger.info("params"+ params); 
//		ResponseEntity<List<JSONObject>> response = restTemplate.exchange(
//				builder.toUriString(), 
//				HttpMethod.GET, 
//				new HttpEntity(headers),
//				new ParameterizedTypeReference<List<JSONObject>>() {});
//		List<JSONObject> resbody=response.getBody();
//		logger.info("resbody "+resbody);
//	
//		for(JSONObject jo:resbody) {
//			logger.info("sectionId "+jo.get("sectionId"));
//			String grpName=(String) jo.get("sectionId"); 
//			logger.info("sectionId received "+grpName); 
//			rolesList.add(grpName);
//
//		}
//		Optional<KeycloakGroups> grpData = keycloakGroupRepository.findBygrpName(serviceNumber);
//
//		if(grpData.isPresent())
//		{
//			logger.info("grpData is present in keycloak "+grpData.get().getGrpId()); //$NON-NLS-1$
//
//			List<KeycloakGroupRoleMapping> grpsData = keycloakGroupRoleMappingRepository.findByGroupId(grpData.get().getGrpId());
//
//			for (KeycloakGroupRoleMapping roleData:grpsData)
//			{
//				logger.info("roleData is present in keycloak "+roleData.getRoleId()); //$NON-NLS-1$
//
//				Optional<KeycloakRoles> rolesData = keycloakRolsRepository.findById(roleData.getRoleId());
//				rolesList.add(rolesData.get().getDescription());
//			}
//		}
		rolesList.add("7WG.HRC");
		rolesList.add("7WG.METO");
		rolesList.add("7WG.ACT");		
		rolesList.add("7WG.CAD");
		rolesList.add("7WG.JAK");
		rolesList.add("7WG.ABCD");
		rolesList.add("7WG.MNOP");
		rolesList.add("9WG.WXY");

		logger.info("rolesList "+rolesList);

		JSONObject json = new JSONObject();
		json.put("status", HttpStatus.OK); //$NON-NLS-1$
		json.put("data", rolesList); //$NON-NLS-1$
		return ResponseEntity.ok(json);

	}

}
