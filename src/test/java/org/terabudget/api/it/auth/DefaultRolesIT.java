package org.terabudget.api.it.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.terabudget.api.domain.BudgetRole;
import org.terabudget.api.model.auth.BuiltInRoleUrn;

import jakarta.transaction.Transactional;

import org.terabudget.api.repository.BudgetRoleRepository;

/**
 * Integration tests to check that all the roles referred to in BuiltInRole
 * exist in the database and have the built in flag set.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Transactional
public class DefaultRolesIT {

    @Autowired
    private BudgetRoleRepository roleRepository;

    @ParameterizedTest
    @MethodSource("getRoles")
    public void role_exists(BuiltInRoleUrn builtInRole) throws Exception {
        BudgetRole role = roleRepository.findByUrn(builtInRole.getUrn()).get();
        assertTrue(role.isBuiltIn());
    }

    public static Stream<Arguments> getRoles() {
        return Stream.of(BuiltInRoleUrn.values()).map(Arguments::of);
    }
}
