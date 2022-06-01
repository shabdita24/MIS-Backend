package com.mis.eoffice.dto;

import java.time.LocalDate;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailedTable {
	private String departmentName;
	private String fileNumber;
	private String subject;
	private String initiatedBy;
	private String pendingWith;
	private LocalDate pendingSince;
	private int pendingdays;
}
