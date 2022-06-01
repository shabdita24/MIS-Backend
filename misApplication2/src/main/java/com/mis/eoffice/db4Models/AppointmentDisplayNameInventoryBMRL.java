package com.mis.eoffice.db4Models;

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
@Table(name="appt")
public class AppointmentDisplayNameInventoryBMRL {
	@Id
	@Column(name = "id")
	private String rObjectId;

	@Column(name="appointment")
	private String appointment;

	@Column(name="appointmentdisplayname")
	private String appointmentdisplayname;
}
