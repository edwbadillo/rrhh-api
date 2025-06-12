package com.edwin.rrhh_api.modules.company;

import com.edwin.rrhh_api.common.jpa.AuditableUser;
import com.edwin.rrhh_api.modules.user.AuthUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

/**
 * Entidad de empresa.
 */
@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Company extends AuditableUser<AuthUser> {

    @Id
    private UUID id;

    private String legalName;
    private String tradeName;
    private String taxId;

    private String email;
    private String phoneNumber;
    private String address;

    private String cityName;
    private String stateName;
    private String countryName;

    private boolean isActive;

    // Util para testing
    public static Company of(String legalName, String taxId, String cityName, String stateName, String countryName) {
        return Company.builder()
                .legalName(legalName)
                .taxId(taxId)
                .cityName(cityName)
                .stateName(stateName)
                .countryName(countryName)
                .isActive(true)
                .build();
    }

}
