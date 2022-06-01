package com.mis.eoffice.db2Models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name="iaf_mis_s")
public class FileInventory {
	@Id
	@Column(name = "r_object_id")
	private String rObjectId;

	@Column(name="subject_")
	private String subject;
	
	@Column(name="task_state")
	private String taskState;
	
	@Column(name="current_dept")
	private String currDep;
	
	
	@Column(name="file_number")
	private String fileNumber;
	
	@Column(name="to_person")
	private String toPerson;
	
	@Column(name="date_time_received")
	private Date dateTimeRecieved;
	
	@Column(name="mis_type")
	private String misType;
	
	@Column(name="date_time_forwarded")
	private Date dateTimeForwarded;
	
	@Column(name="to_sau")
	private String toSau;
	
	@Column(name="from_sau")
	private String fromSau;
	
	@Column(name="initiated_by_sau")
	private String initiatedBySau;
	
	@Column(name="from_person")
	private String fromPerson;
}
