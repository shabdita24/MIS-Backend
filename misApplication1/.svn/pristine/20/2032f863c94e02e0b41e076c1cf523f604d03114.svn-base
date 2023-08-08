package com.mis.eoffice.db4Repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db4Models.AppointmentDisplayNameInventoryBMRL;


@Repository
public interface AppointmentDisplayNameRepositoryBMRL extends CrudRepository<AppointmentDisplayNameInventoryBMRL, String> {

	Optional<AppointmentDisplayNameInventoryBMRL> findByappointment(String appointment);
}
