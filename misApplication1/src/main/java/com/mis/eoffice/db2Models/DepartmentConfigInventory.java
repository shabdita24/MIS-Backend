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
@Table(name="iaf_department_config_s")
public class DepartmentConfigInventory {
	@Id
	@Column(name = "r_object_id")
	private String rObjectId;

	@Column(name="dep_full_name")
	private String depFullName;

	@Column(name="dep_name")
	private String depBranch;

	@Column(name="sau_parent")
	private String depNameSmall;
	
}
