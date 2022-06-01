package com.mis.eoffice.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SauBranchFiltDto {

	private Integer fileId;


	private Integer totalFilePendingTenDaysSau;

	private Integer totalFilesPendingFiveTenSau;
	
	private Integer totalFilesPendingFiveSau;
	
	private Integer totalSau;

	private Integer totalFilePendingTenDaysCau;

	private Integer totalFilesPendingFiveTenCau;

	private Integer totalFilesPendingFiveCau;
	
	private Integer totalCau;
	
	private String rObjectId;

	private String sauDisplayName;

	private String sauBranch;

	private int sauId;

	private int parentId;

	private String sauParent;

	private LocalDateTime lastUpdated;

	private boolean hasItems;
}
