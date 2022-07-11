package com.mis.eoffice.service;

import java.util.List;

import com.mis.eoffice.dto.DetailedTable;
import com.mis.eoffice.dto.ResponseDetailTable;


public interface DetailedTableService {

	ResponseDetailTable getdetailedtableInboxFile(String sau, Integer num, String command, Integer currentPage, Integer pageSize);

	ResponseDetailTable getdetailedtablepend37(String sau, Integer num, String command, Integer currentPage, Integer pageSize);

	//List<DetailedTable> getdetailedtablepend7(String sau, Integer num);

	ResponseDetailTable getdetailedtablepro30days(String sau, Integer num, String command, Integer currentPage, Integer pageSize);

	ResponseDetailTable getdetailedtablependingatfiveten(String upperCase, Integer num, String command, Integer currentPage, Integer pageSize);

	ResponseDetailTable getdetailedtablependingatten(String upperCase, Integer num, String command, Integer currentPage, Integer pageSize);

	ResponseDetailTable getdetailedtablependingatfive(String upperCase, Integer num, String command, Integer currentPage, Integer pageSize);

	ResponseDetailTable getdetailedtableInboxFileCau(String upperCase, Integer num, String command, Integer currentPage, Integer pageSize);

	ResponseDetailTable getdetailedtablependingatzerofive(String upperCase, Integer num, String command, Integer currentPage, Integer pageSize);

}
