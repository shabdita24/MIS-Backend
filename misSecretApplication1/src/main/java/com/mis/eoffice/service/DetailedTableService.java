package com.mis.eoffice.service;

import java.util.List;

import com.mis.eoffice.dto.DetailedTable;


public interface DetailedTableService {

	List<DetailedTable> getdetailedtableInboxFile(String sau, Integer num, String command);

	List<DetailedTable> getdetailedtablepend37(String sau, Integer num, String command);

	//List<DetailedTable> getdetailedtablepend7(String sau, Integer num);

	List<DetailedTable> getdetailedtablepro30days(String sau, Integer num, String command);

	List<DetailedTable> getdetailedtablependingatfiveten(String upperCase, Integer num, String command);

	List<DetailedTable> getdetailedtablependingatten(String upperCase, Integer num, String command);

	List<DetailedTable> getdetailedtablependingatfive(String upperCase, Integer num, String command);

	List<DetailedTable> getdetailedtableInboxFileCau(String upperCase, Integer num, String command);

	List<DetailedTable> getdetailedtablependingatzerofive(String upperCase, Integer num, String command);

}
