package com.edwin.rrhh_api.modules.company;

import com.edwin.rrhh_api.config.jpa.AuditorAwareImpl;
import com.edwin.rrhh_api.config.jpa.JpaAuditingConfig;
import com.edwin.rrhh_api.config.security.AuthUserDetails;
import com.edwin.rrhh_api.modules.user.AuthUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaAuditingConfig.class, AuditorAwareImpl.class})
@ActiveProfiles("test")
public class CompanyRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    private AuthUser testUser;

    @BeforeEach
    void setupAuthenticatedUser() {
        testUser = AuthUser.builder()
                .email("test@example.com")
                .isActive(true)
                .role("ADMIN")
                .build();

        entityManager.persist(testUser);

        AuthUserDetails userDetails = new AuthUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPersistCompany() {
        Company company = Company.builder()
                .id(UUID.randomUUID())
                .legalName("Test Legal Name")
                .tradeName("Test Trade")
                .taxId("123456789")
                .email("email@test.com")
                .phoneNumber("0000000")
                .address("Some Street")
                .cityName("City")
                .stateName("State")
                .countryName("Country")
                .isActive(true)
                .build();

        Company saved = companyRepository.save(company);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLegalName()).isEqualTo("Test Legal Name");
        assertThat(saved.getTradeName()).isEqualTo("Test Trade");
        assertThat(saved.getTaxId()).isEqualTo("123456789");
        assertThat(saved.getEmail()).isEqualTo("email@test.com");
        assertThat(saved.getPhoneNumber()).isEqualTo("0000000");
        assertThat(saved.getAddress()).isEqualTo("Some Street");
        assertThat(saved.getCityName()).isEqualTo("City");
        assertThat(saved.getStateName()).isEqualTo("State");
        assertThat(saved.getCountryName()).isEqualTo("Country");
        assertThat(saved.isActive()).isTrue();

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        assertThat(saved.getCreatedBy())
                .isNotNull()
                .extracting(AuthUser::getId)
                .isEqualTo(testUser.getId());
    }

    @Test
    void shouldCheckExistenceByTaxId() {
        Company company = persistCompany("ACME S.A.", "ACME", "TAX123", "contact@acme.com");

        assertThat(companyRepository.existsByTaxId("TAX123")).isTrue();
        assertThat(companyRepository.existsByTaxId("INVALID")).isFalse();

        UUID differentId = UUID.randomUUID();
        assertThat(companyRepository.existsByTaxIdAndIdNot("TAX123", differentId)).isTrue();
        assertThat(companyRepository.existsByTaxIdAndIdNot("TAX123", company.getId())).isFalse();
    }

    @Test
    void shouldCheckExistenceByLegalName() {
        Company company = persistCompany("ACME S.A.", "ACME", "TAX123", "contact@acme.com");
        assertThat(companyRepository.existsByLegalName("ACME S.A.")).isTrue();
        assertThat(companyRepository.existsByLegalName("UNKNOWN")).isFalse();

        UUID differentId = UUID.randomUUID();
        assertThat(companyRepository.existsByLegalNameAndIdNot("ACME S.A.", differentId)).isTrue();
        assertThat(companyRepository.existsByLegalNameAndIdNot("ACME S.A.", company.getId())).isFalse();
    }

    @Test
    void shouldSearchByKeywordInMultipleFields() {
        persistCompany("Alpha Corp", "Alpha", "ALPHA001", "alpha@email.com");
        persistCompany("Beta Industries", "Beta Solutions", "BETA002", "beta@solutions.com");
        persistCompany("Gamma LLC", "Gamma", "GAMMA003", "hello@gamma.org");

        List<Company> all = companyRepository.searchByKeyword("a");
        assertThat(all).hasSize(3);

        List<Company> byLegalName = companyRepository.searchByKeyword("alpha");
        assertThat(byLegalName).hasSize(1);
        assertThat(byLegalName.getFirst().getLegalName()).isEqualTo("Alpha Corp");

        List<Company> byTradeName = companyRepository.searchByKeyword("solutions");
        assertThat(byTradeName).hasSize(1);
        assertThat(byTradeName.getFirst().getTradeName()).containsIgnoringCase("solutions");

        List<Company> byTaxId = companyRepository.searchByKeyword("gamma003");
        assertThat(byTaxId).hasSize(1);
        assertThat(byTaxId.getFirst().getTaxId()).isEqualToIgnoringCase("GAMMA003");

        List<Company> byEmail = companyRepository.searchByKeyword("beta@solutions");
        assertThat(byEmail).hasSize(1);
        assertThat(byEmail.getFirst().getEmail()).contains("beta@solutions");

        List<Company> noResults = companyRepository.searchByKeyword("doesnotexist");
        assertThat(noResults).isEmpty();
    }


    private Company persistCompany(String legalName, String tradeName, String taxId, String email) {
        Company company = Company.of(
                legalName,
                taxId,
                "City",
                "State",
                "Country"
        );
        company.setId(UUID.randomUUID());
        company.setTradeName(tradeName);
        company.setEmail(email);
        company.setPhoneNumber("0000000");
        company.setAddress("Some Street");

        return entityManager.persistAndFlush(company);
    }


}
