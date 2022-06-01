package com.mis.eoffice.service;

import java.util.List;
import java.util.Map;

import com.mis.eoffice.dto.SauBranchFiltDto;


public interface DashboardDataService {

	List<SauBranchFiltDto> parseMainData(String sau, String sauId);

	Map<String, String> parseDeptData(String sau, String sauId);
}
