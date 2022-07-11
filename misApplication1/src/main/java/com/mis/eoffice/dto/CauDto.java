package com.mis.eoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CauDto {
	
	private int total;
	private int fiveToTen;
	private int beforeTen; 

}
