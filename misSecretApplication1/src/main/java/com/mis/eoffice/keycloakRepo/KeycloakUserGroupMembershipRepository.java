package com.mis.eoffice.keycloakRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.modelKeycloak.KeycloakUsersGroup;


@Repository
public interface KeycloakUserGroupMembershipRepository extends JpaRepository<KeycloakUsersGroup, String> {


}
