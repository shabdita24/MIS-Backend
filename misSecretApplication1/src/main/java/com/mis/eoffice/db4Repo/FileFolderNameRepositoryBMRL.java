package com.mis.eoffice.db4Repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db4Models.FileFolderNameInventoryBMRL;


@Repository
public interface FileFolderNameRepositoryBMRL extends CrudRepository<FileFolderNameInventoryBMRL, String> {

	Optional<FileFolderNameInventoryBMRL> findByFileName(String fileName);
}
