package com.mis.eoffice.keycloakRepo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.modelKeycloak.KeycloakRoles;


@Repository
public interface KeycloakRolsRepository extends JpaRepository<KeycloakRoles, String> {

		public List<KeycloakRoles> findByRealmIdAndClientId(String realmId, String clientId);
}
