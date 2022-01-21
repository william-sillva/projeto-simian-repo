package com.developer.simianproject.api.repositories;

import com.developer.simianproject.api.entities.DnaBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface DnaBaseRepository extends JpaRepository<DnaBase, Long> {

}
