package com.mis.eoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchDTO {

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
	
	private int parentId;
	
	private int indexId;
	
	private int fileId;
}
