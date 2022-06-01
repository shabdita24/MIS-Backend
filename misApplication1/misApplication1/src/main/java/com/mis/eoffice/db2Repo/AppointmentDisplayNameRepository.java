package com.mis.eoffice.db2Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db2Models.AppointmentDisplayNameInventory;
import com.mis.eoffice.db2Models.DepartmentConfigInventory;


@Repository
public interface AppointmentDisplayNameRepository extends CrudRepository<AppointmentDisplayNameInventory, String> {

	Optional<AppointmentDisplayNameInventory> findByappointment(String appointment);
}
