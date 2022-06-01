package com.mis.eoffice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.mis.eoffice.Dto.SauBranchFiltDto;



@CrossOrigin("*")
@RestController
@RequestMapping("/apis")
public class MisController {
	private static final Logger log = LoggerFactory.getLogger(MisController.class);

	@GetMapping("/getUserRoles")
	public ResponseEntity<JSONObject> getUserRoles(HttpServletRequest request) {
		String serviceNumber = request.getHeader("username");  //$NON-NLS-1$
		log.info("serviceNumber recieved in username field "+serviceNumber); //$NON-NLS-1$
		Map<String, Object> params = new HashMap<>();
		params.put(Messages.getString("MisController.PATHPARAM"), serviceNumber); //$NON-NLS-1$
		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MasterDataController.APIURL")); //$NON-NLS-1$
		for (Entry<String, Object> entry : params.entrySet()) {
			builder.queryParam(entry.getKey(), entry.getValue());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		log.info("params"+ params);  //$NON-NLS-1$
		ResponseEntity<List<JSONObject>> response = restTemplate.exchange(
				builder.toUriString(), 
				HttpMethod.GET, 
				new HttpEntity(headers),
				new ParameterizedTypeReference<List<JSONObject>>() {});
		List<JSONObject> resbody=response.getBody();
		log.info("resbody "+resbody); //$NON-NLS-1$
	
		List<String> rolesList = new ArrayList<String>();
		for(JSONObject jo:resbody) {
			log.info("sectionId "+jo.get("sectionId")); //$NON-NLS-1$ //$NON-NLS-2$
			String grpName=(String) jo.get("sectionId");  //$NON-NLS-1$
			log.info("sectionId received "+grpName);  //$NON-NLS-1$
			rolesList.add(grpName);

		}
		log.info("rolesList "+rolesList); //$NON-NLS-1$
		
		JSONObject json = new JSONObject();
		json.put("status", HttpStatus.OK); //$NON-NLS-1$
		json.put("data", rolesList); //$NON-NLS-1$
		return ResponseEntity.ok(json);

	}

	//For misApplication1
	@GetMapping("/getSauDataAIRHQ")
	public ResponseEntity<List<SauBranchFiltDto>> getSauData(HttpServletRequest request){
		String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
		String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("groupName", grpName);	     //$NON-NLS-1$
		headers.add("sauId", sauId); //$NON-NLS-1$
		HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
		ResponseEntity<List<SauBranchFiltDto>> response = restTemplate.exchange(Messages.getString("MisController.4"), //$NON-NLS-1$
				HttpMethod.GET,
				httpEntityRequest,
				new ParameterizedTypeReference<List<SauBranchFiltDto>>() {});
		log.info("Response "+response.getBody()); //$NON-NLS-1$
		return response;
	}


	//	DashBoard API
	//	
	@GetMapping("/getMainDataAIRHQ")
	public ResponseEntity<JSONObject> getMainData(HttpServletRequest request)
	{
		String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
		String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("groupName", grpName);	     //$NON-NLS-1$
		headers.add("sauId", sauId); //$NON-NLS-1$
		HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
		ResponseEntity<JSONObject> response = restTemplate.exchange(
				Messages.getString("MisController.10"), //$NON-NLS-1$
				HttpMethod.GET,
				httpEntityRequest,
				JSONObject.class);
		log.info("Response "+response.getBody()); //$NON-NLS-1$
		return response;
	}

	//	DashBoard API	//Card
	@GetMapping("/getDeptDataAIRHQ")
	public ResponseEntity<Map<String,String>> getDeptData(HttpServletRequest request)
	{
		String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
		String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("groupName", grpName);	     //$NON-NLS-1$
		headers.add("sauId", sauId); //$NON-NLS-1$
		HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
		ResponseEntity<Map<String,String>> response = restTemplate.exchange(
				Messages.getString("MisController.16"), //$NON-NLS-1$
				HttpMethod.GET,
				httpEntityRequest,
				new ParameterizedTypeReference<Map<String,String>>() {});
		return response;
	}

	//  Shabdita
	@PostMapping("/gettotalfilesAIRHQ")
	private ResponseEntity<JSONObject> gettotalfiles(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
		log.info("********Detailed Table********"); //$NON-NLS-1$
		Map<String, Object> params = new HashMap<>();
		params.put("sau", sau); //$NON-NLS-1$
		params.put("column", column); //$NON-NLS-1$
		params.put("num", num); //$NON-NLS-1$
		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MisController.21")); //$NON-NLS-1$
		for (Entry<String, Object> entry : params.entrySet()) {
			builder.queryParam(entry.getKey(), entry.getValue());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity(headers), JSONObject.class);
		log.info("Response "+response.getBody()); //$NON-NLS-1$
		return response;

	}

	//For misApplicationWAC1
		@GetMapping("/getSauDataWAC")
		public ResponseEntity<List<SauBranchFiltDto>> getSauData1(HttpServletRequest request){
			String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
			String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("groupName", grpName);	     //$NON-NLS-1$
			headers.add("sauId", sauId); //$NON-NLS-1$
			HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
			ResponseEntity<List<SauBranchFiltDto>> response = restTemplate.exchange(Messages.getString("MisController.29"), //$NON-NLS-1$
					HttpMethod.GET,
					httpEntityRequest,
					new ParameterizedTypeReference<List<SauBranchFiltDto>>() {});
			log.info("Response "+response.getBody()); //$NON-NLS-1$
			return response;
		}


		//	DashBoard API
		//	
		@GetMapping("/getMainDataWAC")
		public ResponseEntity<JSONObject> getMainData1(HttpServletRequest request)
		{
			String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
			String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("groupName", grpName);	     //$NON-NLS-1$
			headers.add("sauId", sauId); //$NON-NLS-1$
			HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
			ResponseEntity<JSONObject> response = restTemplate.exchange(
					Messages.getString("MisController.35"), //$NON-NLS-1$
					HttpMethod.GET,
					httpEntityRequest,
					JSONObject.class);
			log.info("Response "+response.getBody()); //$NON-NLS-1$
			return response;
		}

		//	DashBoard API	//Card
		@GetMapping("/getDeptDataWAC")
		public ResponseEntity<Map<String,String>> getDeptData1(HttpServletRequest request)
		{
			String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
			String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("groupName", grpName);	     //$NON-NLS-1$
			headers.add("sauId", sauId); //$NON-NLS-1$
			HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
			ResponseEntity<Map<String,String>> response = restTemplate.exchange(
					Messages.getString("MisController.41"), //$NON-NLS-1$
					HttpMethod.GET,
					httpEntityRequest,
					new ParameterizedTypeReference<Map<String,String>>() {});
			return response;
		}

		//  Shabdita
		@PostMapping("/gettotalfilesWAC")
		private ResponseEntity<JSONObject> gettotalfiles1(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
			log.info("********Detailed Table********"); //$NON-NLS-1$
			Map<String, Object> params = new HashMap<>();
			params.put("sau", sau); //$NON-NLS-1$
			params.put("column", column); //$NON-NLS-1$
			params.put("num", num); //$NON-NLS-1$
			RestTemplate restTemplate = new RestTemplate();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MisController.46")); //$NON-NLS-1$
			for (Entry<String, Object> entry : params.entrySet()) {
				builder.queryParam(entry.getKey(), entry.getValue());
			}
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
			ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity(headers), JSONObject.class);
			log.info("Response "+response.getBody()); //$NON-NLS-1$
			return response;

		}
		//For misApplication2
		@GetMapping("/getSauDataIncomingAIRHQ")
		public ResponseEntity<List<SauBranchFiltDto>> getSauData3(HttpServletRequest request){
			String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
			String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("groupName", grpName);	     //$NON-NLS-1$
			headers.add("sauId", sauId); //$NON-NLS-1$
			HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
			ResponseEntity<List<SauBranchFiltDto>> response = restTemplate.exchange(Messages.getString("MisController.54"), //$NON-NLS-1$
					HttpMethod.GET,
					httpEntityRequest,
					new ParameterizedTypeReference<List<SauBranchFiltDto>>() {});
			log.info("Response "+response.getBody()); //$NON-NLS-1$
			return response;
		}


		//	DashBoard API
		//	
		@GetMapping("/getMainDataIncomingAIRHQ")
		public ResponseEntity<JSONObject> getMainData3(HttpServletRequest request)
		{
			String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
			String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("groupName", grpName);	     //$NON-NLS-1$
			headers.add("sauId", sauId); //$NON-NLS-1$
			HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
			ResponseEntity<JSONObject> response = restTemplate.exchange(
					Messages.getString("MisController.60"), //$NON-NLS-1$
					HttpMethod.GET,
					httpEntityRequest,
					JSONObject.class);
			log.info("Response "+response.getBody()); //$NON-NLS-1$
			return response;
		}

		//	DashBoard API	//Card
		@GetMapping("/getDeptDataIncomingAIRHQ")
		public ResponseEntity<Map<String,String>> getDeptData3(HttpServletRequest request)
		{
			String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
			String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("groupName", grpName);	     //$NON-NLS-1$
			headers.add("sauId", sauId); //$NON-NLS-1$
			HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
			ResponseEntity<Map<String,String>> response = restTemplate.exchange(
					Messages.getString("MisController.66"), //$NON-NLS-1$
					HttpMethod.GET,
					httpEntityRequest,
					new ParameterizedTypeReference<Map<String,String>>() {});
			return response;
		}

		//  Shabdita
		@PostMapping("/gettotalfilesIncomingAIRHQ")
		private ResponseEntity<JSONObject> gettotalfiles3(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
			log.info("********Detailed Table********"); //$NON-NLS-1$
			Map<String, Object> params = new HashMap<>();
			params.put("sau", sau); //$NON-NLS-1$
			params.put("column", column); //$NON-NLS-1$
			params.put("num", num); //$NON-NLS-1$
			RestTemplate restTemplate = new RestTemplate();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MisController.71")); //$NON-NLS-1$
			for (Entry<String, Object> entry : params.entrySet()) {
				builder.queryParam(entry.getKey(), entry.getValue());
			}
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
			ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity(headers), JSONObject.class);
			log.info("Response "+response.getBody()); //$NON-NLS-1$
			return response;

		}

		//For misApplicationWAC2
			@GetMapping("/getSauDataIncomingWAC")
			public ResponseEntity<List<SauBranchFiltDto>> getSauData4(HttpServletRequest request){
				String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
				String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("groupName", grpName);	     //$NON-NLS-1$
				headers.add("sauId", sauId); //$NON-NLS-1$
				HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
				ResponseEntity<List<SauBranchFiltDto>> response = restTemplate.exchange(Messages.getString("MisController.79"), //$NON-NLS-1$
						HttpMethod.GET,
						httpEntityRequest,
						new ParameterizedTypeReference<List<SauBranchFiltDto>>() {});
				log.info("Response "+response.getBody()); //$NON-NLS-1$
				return response;
			}


			//	DashBoard API
			//	
			@GetMapping("/getMainDataIncomingWAC")
			public ResponseEntity<JSONObject> getMainData4(HttpServletRequest request)
			{
				String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
				String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("groupName", grpName);	     //$NON-NLS-1$
				headers.add("sauId", sauId); //$NON-NLS-1$
				HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
				ResponseEntity<JSONObject> response = restTemplate.exchange(
						Messages.getString("MisController.85"), //$NON-NLS-1$
						HttpMethod.GET,
						httpEntityRequest,
						JSONObject.class);
				log.info("Response "+response.getBody()); //$NON-NLS-1$
				return response;
			}

			//	DashBoard API	//Card
			@GetMapping("/getDeptDataIncomingWAC")
			public ResponseEntity<Map<String,String>> getDeptData4(HttpServletRequest request)
			{
				String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
				String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("groupName", grpName);	     //$NON-NLS-1$
				headers.add("sauId", sauId); //$NON-NLS-1$
				HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
				ResponseEntity<Map<String,String>> response = restTemplate.exchange(
						Messages.getString("MisController.91"), //$NON-NLS-1$
						HttpMethod.GET,
						httpEntityRequest,
						new ParameterizedTypeReference<Map<String,String>>() {});
				return response;
			}

			//  Shabdita
			@PostMapping("/gettotalfilesIncomingWAC")
			private ResponseEntity<JSONObject> gettotalfiles4(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
				log.info("********Detailed Table********"); //$NON-NLS-1$
				Map<String, Object> params = new HashMap<>();
				params.put("sau", sau); //$NON-NLS-1$
				params.put("column", column); //$NON-NLS-1$
				params.put("num", num); //$NON-NLS-1$
				RestTemplate restTemplate = new RestTemplate();
				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MisController.96")); //$NON-NLS-1$
				for (Entry<String, Object> entry : params.entrySet()) {
					builder.queryParam(entry.getKey(), entry.getValue());
				}
				HttpHeaders headers = new HttpHeaders();
				headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
				ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity(headers), JSONObject.class);
				log.info("Response "+response.getBody()); //$NON-NLS-1$
				return response;

			}
			//For misApplicationSecret1
			@GetMapping("/getSauDataSecretAIRHQ")
			public ResponseEntity<List<SauBranchFiltDto>> getSauData5(HttpServletRequest request){
				String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
				String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("groupName", grpName);	     //$NON-NLS-1$
				headers.add("sauId", sauId); //$NON-NLS-1$
				HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
				ResponseEntity<List<SauBranchFiltDto>> response = restTemplate.exchange(Messages.getString("MisController.104"), //$NON-NLS-1$
						HttpMethod.GET,
						httpEntityRequest,
						new ParameterizedTypeReference<List<SauBranchFiltDto>>() {});
				log.info("Response "+response.getBody()); //$NON-NLS-1$
				return response;
			}


			//	DashBoard API
			//	
			@GetMapping("/getMainDataSecretAIRHQ")
			public ResponseEntity<JSONObject> getMainData5(HttpServletRequest request)
			{
				String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
				String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("groupName", grpName);	     //$NON-NLS-1$
				headers.add("sauId", sauId); //$NON-NLS-1$
				HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
				ResponseEntity<JSONObject> response = restTemplate.exchange(
						Messages.getString("MisController.110"), //$NON-NLS-1$
						HttpMethod.GET,
						httpEntityRequest,
						JSONObject.class);
				log.info("Response "+response.getBody()); //$NON-NLS-1$
				return response;
			}

			//	DashBoard API	//Card
			@GetMapping("/getDeptDataSecretAIRHQ")
			public ResponseEntity<Map<String,String>> getDeptData5(HttpServletRequest request)
			{
				String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
				String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("groupName", grpName);	     //$NON-NLS-1$
				headers.add("sauId", sauId); //$NON-NLS-1$
				HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
				ResponseEntity<Map<String,String>> response = restTemplate.exchange(
						Messages.getString("MisController.116"), //$NON-NLS-1$
						HttpMethod.GET,
						httpEntityRequest,
						new ParameterizedTypeReference<Map<String,String>>() {});
				return response;
			}

			//  Shabdita
			@PostMapping("/gettotalfilesSecretAIRHQ")
			private ResponseEntity<JSONObject> gettotalfiles5(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
				log.info("********Detailed Table********"); //$NON-NLS-1$
				Map<String, Object> params = new HashMap<>();
				params.put("sau", sau); //$NON-NLS-1$
				params.put("column", column); //$NON-NLS-1$
				params.put("num", num); //$NON-NLS-1$
				RestTemplate restTemplate = new RestTemplate();
				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MisController.121")); //$NON-NLS-1$
				for (Entry<String, Object> entry : params.entrySet()) {
					builder.queryParam(entry.getKey(), entry.getValue());
				}
				HttpHeaders headers = new HttpHeaders();
				headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
				ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity(headers), JSONObject.class);
				log.info("Response "+response.getBody()); //$NON-NLS-1$
				return response;

			}

			//For misApplicationSecretWAC1
				@GetMapping("/getSauDataSecretWAC")
				public ResponseEntity<List<SauBranchFiltDto>> getSauData6(HttpServletRequest request){
					String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
					String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("groupName", grpName);	     //$NON-NLS-1$
					headers.add("sauId", sauId); //$NON-NLS-1$
					HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
					ResponseEntity<List<SauBranchFiltDto>> response = restTemplate.exchange(Messages.getString("MisController.129"), //$NON-NLS-1$
							HttpMethod.GET,
							httpEntityRequest,
							new ParameterizedTypeReference<List<SauBranchFiltDto>>() {});
					log.info("Response "+response.getBody()); //$NON-NLS-1$
					return response;
				}


				//	DashBoard API
				//	
				@GetMapping("/getMainDataSecretWAC")
				public ResponseEntity<JSONObject> getMainData6(HttpServletRequest request)
				{
					String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
					String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("groupName", grpName);	     //$NON-NLS-1$
					headers.add("sauId", sauId); //$NON-NLS-1$
					HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
					ResponseEntity<JSONObject> response = restTemplate.exchange(
							Messages.getString("MisController.135"), //$NON-NLS-1$
							HttpMethod.GET,
							httpEntityRequest,
							JSONObject.class);
					log.info("Response "+response.getBody()); //$NON-NLS-1$
					return response;
				}

				//	DashBoard API	//Card
				@GetMapping("/getDeptDataSecretWAC")
				public ResponseEntity<Map<String,String>> getDeptData6(HttpServletRequest request)
				{
					String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
					String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("groupName", grpName);	     //$NON-NLS-1$
					headers.add("sauId", sauId); //$NON-NLS-1$
					HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
					ResponseEntity<Map<String,String>> response = restTemplate.exchange(
							Messages.getString("MisController.141"), //$NON-NLS-1$
							HttpMethod.GET,
							httpEntityRequest,
							new ParameterizedTypeReference<Map<String,String>>() {});
					return response;
				}

				//  Shabdita
				@PostMapping("/gettotalfilesSecretWAC")
				private ResponseEntity<JSONObject> gettotalfiles6(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
					log.info("********Detailed Table********"); //$NON-NLS-1$
					Map<String, Object> params = new HashMap<>();
					params.put("sau", sau); //$NON-NLS-1$
					params.put("column", column); //$NON-NLS-1$
					params.put("num", num); //$NON-NLS-1$
					RestTemplate restTemplate = new RestTemplate();
					UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MisController.146")); //$NON-NLS-1$
					for (Entry<String, Object> entry : params.entrySet()) {
						builder.queryParam(entry.getKey(), entry.getValue());
					}
					HttpHeaders headers = new HttpHeaders();
					headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
					ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity(headers), JSONObject.class);
					log.info("Response "+response.getBody()); //$NON-NLS-1$
					return response;

				}
				//For misApplicationSecret2
				@GetMapping("/getSauDataSecretIncomingAIRHQ")
				public ResponseEntity<List<SauBranchFiltDto>> getSauData7(HttpServletRequest request){
					String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
					String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("groupName", grpName);	     //$NON-NLS-1$
					headers.add("sauId", sauId); //$NON-NLS-1$
					HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
					ResponseEntity<List<SauBranchFiltDto>> response = restTemplate.exchange(Messages.getString("MisController.154"), //$NON-NLS-1$
							HttpMethod.GET,
							httpEntityRequest,
							new ParameterizedTypeReference<List<SauBranchFiltDto>>() {});
					log.info("Response "+response.getBody()); //$NON-NLS-1$
					return response;
				}


				//	DashBoard API
				//	
				@GetMapping("/getMainDataSecretIncomingAIRHQ")
				public ResponseEntity<JSONObject> getMainData7(HttpServletRequest request)
				{
					String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
					String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("groupName", grpName);	     //$NON-NLS-1$
					headers.add("sauId", sauId); //$NON-NLS-1$
					HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
					ResponseEntity<JSONObject> response = restTemplate.exchange(
							Messages.getString("MisController.160"), //$NON-NLS-1$
							HttpMethod.GET,
							httpEntityRequest,
							JSONObject.class);
					log.info("Response "+response.getBody()); //$NON-NLS-1$
					return response;
				}

				//	DashBoard API	//Card
				@GetMapping("/getDeptDataSecretIncomingAIRHQ")
				public ResponseEntity<Map<String,String>> getDeptData7(HttpServletRequest request)
				{
					String grpName = (String) request.getHeader("department"); //$NON-NLS-1$
					String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("groupName", grpName);	     //$NON-NLS-1$
					headers.add("sauId", sauId); //$NON-NLS-1$
					HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
					ResponseEntity<Map<String,String>> response = restTemplate.exchange(
							Messages.getString("MisController.166"), //$NON-NLS-1$
							HttpMethod.GET,
							httpEntityRequest,
							new ParameterizedTypeReference<Map<String,String>>() {});
					return response;
				}

				//  Shabdita
				@PostMapping("/gettotalfilesSecretIncomingAIRHQ")
				private ResponseEntity<JSONObject> gettotalfiles7(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
					log.info("********Detailed Table********"); //$NON-NLS-1$
					Map<String, Object> params = new HashMap<>();
					params.put("sau", sau); //$NON-NLS-1$
					params.put("column", column); //$NON-NLS-1$
					params.put("num", num); //$NON-NLS-1$
					RestTemplate restTemplate = new RestTemplate();
					UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MisController.171")); //$NON-NLS-1$
					for (Entry<String, Object> entry : params.entrySet()) {
						builder.queryParam(entry.getKey(), entry.getValue());
					}
					HttpHeaders headers = new HttpHeaders();
					headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
					ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity(headers), JSONObject.class);
					log.info("Response "+response.getBody()); //$NON-NLS-1$
					return response;

				}

				//For misApplicationSecretWAC2
					@GetMapping("/getSauDataSecretIncomingWAC")
					public ResponseEntity<List<SauBranchFiltDto>> getSauData8(HttpServletRequest request){
						String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
						String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
						RestTemplate restTemplate = new RestTemplate();
						HttpHeaders headers = new HttpHeaders();
						headers.add("groupName", grpName);	     //$NON-NLS-1$
						headers.add("sauId", sauId); //$NON-NLS-1$
						HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
						ResponseEntity<List<SauBranchFiltDto>> response = restTemplate.exchange(Messages.getString("MisController.179"), //$NON-NLS-1$
								HttpMethod.GET,
								httpEntityRequest,
								new ParameterizedTypeReference<List<SauBranchFiltDto>>() {});
						log.info("Response "+response.getBody()); //$NON-NLS-1$
						return response;
					}


					//	DashBoard API
					//	
					@GetMapping("/getMainDataSecretIncomingWAC")
					public ResponseEntity<JSONObject> getMainData8(HttpServletRequest request)
					{
						String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
						String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
						RestTemplate restTemplate = new RestTemplate();
						HttpHeaders headers = new HttpHeaders();
						headers.add("groupName", grpName);	     //$NON-NLS-1$
						headers.add("sauId", sauId); //$NON-NLS-1$
						HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
						ResponseEntity<JSONObject> response = restTemplate.exchange(
								Messages.getString("MisController.185"), //$NON-NLS-1$
								HttpMethod.GET,
								httpEntityRequest,
								JSONObject.class);
						log.info("Response "+response.getBody()); //$NON-NLS-1$
						return response;
					}

					//	DashBoard API	//Card
					@GetMapping("/getDeptDataSecretIncomingWAC")
					public ResponseEntity<Map<String,String>> getDeptData8(HttpServletRequest request)
					{
						String grpName = (String) request.getHeader("groupName"); //$NON-NLS-1$
						String sauId = (String) request.getHeader("sauId"); //$NON-NLS-1$
						RestTemplate restTemplate = new RestTemplate();
						HttpHeaders headers = new HttpHeaders();
						headers.add("groupName", grpName);	     //$NON-NLS-1$
						headers.add("sauId", sauId); //$NON-NLS-1$
						HttpEntity<String> httpEntityRequest = new HttpEntity<String>(headers);
						ResponseEntity<Map<String,String>> response = restTemplate.exchange(
								Messages.getString("MisController.191"), //$NON-NLS-1$
								HttpMethod.GET,
								httpEntityRequest,
								new ParameterizedTypeReference<Map<String,String>>() {});
						return response;
					}

					//  Shabdita
					@PostMapping("/gettotalfilesSecretIncomingWAC")
					private ResponseEntity<JSONObject> gettotalfiles8(@PathParam("sau") String sau, @PathParam("column") String column,@PathParam("num") Integer num){
						log.info("********Detailed Table********"); //$NON-NLS-1$
						Map<String, Object> params = new HashMap<>();
						params.put("sau", sau); //$NON-NLS-1$
						params.put("column", column); //$NON-NLS-1$
						params.put("num", num); //$NON-NLS-1$
						RestTemplate restTemplate = new RestTemplate();
						UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Messages.getString("MisController.196")); //$NON-NLS-1$
						for (Entry<String, Object> entry : params.entrySet()) {
							builder.queryParam(entry.getKey(), entry.getValue());
						}
						HttpHeaders headers = new HttpHeaders();
						headers.set("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
						ResponseEntity<JSONObject> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, new HttpEntity(headers), JSONObject.class);
						log.info("Response "+response.getBody()); //$NON-NLS-1$
						return response;

					}
}
