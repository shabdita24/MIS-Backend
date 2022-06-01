package com.mis.eoffice.dto;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SauDTO {

	private Integer totalFileInbox;

	private Integer totalCorrespondenceInbox;

	private Integer totalFileProcessedThirtyDays;

	private Integer totalCorrespondenceProcessedThirtyDays;

	private Integer totalFilesPendingThreeSeven;

	private Integer totalFilesPendingSeven;

	private Integer totalCorrespondencePendingThreeSeven;

	private Integer totalCorrespondencePendingSeven;

	private String sau;
	
	private String depFullName;
	
	private int indexId;
	
	private Integer parentId;
	
	private int fileId;
	
	private String lastUpdated;
}
