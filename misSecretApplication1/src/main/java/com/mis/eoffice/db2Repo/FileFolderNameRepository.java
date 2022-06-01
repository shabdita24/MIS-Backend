package com.mis.eoffice.db2Repo;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.mis.eoffice.db2Models.FileFolderNameInventory;


@Repository
public interface FileFolderNameRepository extends CrudRepository<FileFolderNameInventory, String> {

	Optional<FileFolderNameInventory> findByFileName(String fileName);
}
