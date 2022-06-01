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
@Table(name="mis_my_file_sau_branch_inventory")
@DynamicUpdate
public class FileSauBranchInventory {

	@Id
	@Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer fileId;


	@Column(name="command")
	private String command;
	
	@Column(name="total_file_inbox")
	private Integer totalFileInbox;

	@Column(name="total_correspondence_inbox")
	private Integer totalCorrespondenceInbox;
	
	@Column(name="total_file_pending_seven")
	private Integer totalFilesPendingFive;

	@Column(name="total_file_processed_thirty_days")
	private Integer totalFileProcessedTenDays;

	@Column(name="total_file_pending_three_seven")
	private Integer totalFilesPendingFiveTen;
	
	@Column(name="r_object_id")
	private String rObjectId;
	
	@Column(name="sau_display_name")
	private String sauDisplayName;
	
	@Column(name="sau_branch")
	private String sauBranch;
	
	@Column(name="sau_id")
	private Integer sauId;
	
	@Column(name="parent_id")
	private Integer parentId;
	
	@Column(name="sau_parent")
	private String sauParent;
	
	@Column(name="has_children")
	private Integer hasChildren;
}
