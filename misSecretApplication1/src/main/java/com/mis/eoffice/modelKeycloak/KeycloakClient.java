package com.mis.eoffice.modelKeycloak;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="client")
public class KeycloakClient {

	@Id
	@Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	@Column(name="name")
	private String roleName;
	
	@Column(name="realm_id")
	private String realmId;
	
	@Column(name="client_id")
	private String clientId;
	
}
