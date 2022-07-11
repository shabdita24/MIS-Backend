package com.mis.eoffice.db5Repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mis.eoffice.db5Models.FileFolderNameInventoryKNH;


@Repository
public interface FileFolderNameRepositoryKNH extends CrudRepository<FileFolderNameInventoryKNH, String> {

	Optional<FileFolderNameInventoryKNH> findByFileName(String fileName);
}
