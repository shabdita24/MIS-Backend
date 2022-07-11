package com.mis.eoffice.db2Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicUpdate
@Table(name="iaf_file_folder_s")
public class FileFolderNameInventory {
	@Id
	@Column(name = "r_object_id")
	private String rObjectId;

	@Column(name="pkl_directorate")
	private String pklDirectorate;
	
	@Column(name="file_no")
	private String fileName;
}
