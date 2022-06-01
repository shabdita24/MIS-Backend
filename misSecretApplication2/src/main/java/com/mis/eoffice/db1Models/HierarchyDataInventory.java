package com.mis.eoffice.db1Models;

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
@Table(name="mis_hierarchy_data_inventory")
@DynamicUpdate
public class HierarchyDataInventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer sauId;
	
	@Column(name="command")
	private String command;
	
	@Column(name="r_object_id")
	private String rObjectId;
	
	@Column(name="dep_full_name")
	private String depFullName;

	@Column(name="parent_node_id")
	private Integer parentNodeId;
	
	@Column(name="sequence")
	private Integer sequence;

	@Column(name="sau_name")
	private String sauName;
}
