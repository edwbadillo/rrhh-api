package com.edwin.rrhh_api.modules.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad {@link Company}.
 */
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    boolean existsByTaxId(String taxId);

    boolean existsByTaxIdAndIdNot(String taxId, UUID id);

    boolean existsByLegalName(String legalName);

    boolean existsByLegalNameAndIdNot(String legalName, UUID id);

    @Query("""
                SELECT c FROM Company c
                WHERE LOWER(c.legalName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.tradeName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.taxId) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    List<Company> searchByKeyword(@Param("search") String search);

}

