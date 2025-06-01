package com.foodopia.authentication.unitTests;

import com.foodopia.authentication.integrationTests.AbstractIntegrationTest;
import com.foodopia.authentication.entity.Customer;
import com.foodopia.authentication.repository.CustomerRepository;
import com.foodopia.authentication.testData.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
@Disabled("Requires Docker for MongoDB Testcontainer - enable when Docker is available")
class CustomerRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and find customer by username")
    void testSaveAndFindByUsername() {
        // Given
        Customer customer = TestDataFactory.createTestCustomer();

        // When
        Customer saved = customerRepository.save(customer);
        Optional<Customer> found = customerRepository.findByUsername("testuser");

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getUserId()).isNotNull();
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find customer by email")
    void testFindByEmail() {
        // Given
        Customer customer = TestDataFactory.createTestCustomer();
        customerRepository.save(customer);

        // When
        Optional<Customer> found = customerRepository.findByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should return empty when customer not found by username")
    void testFindByUsername_NotFound() {
        // When
        Optional<Customer> found = customerRepository.findByUsername("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when customer not found by email")
    void testFindByEmail_NotFound() {
        // When
        Optional<Customer> found = customerRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should enforce unique username constraint")
    void testUniqueUsernameConstraint() {
        // Given
        Customer customer1 = TestDataFactory.createTestCustomer();
        Customer customer2 = TestDataFactory.createTestCustomer();
        customer2.setEmail("different@example.com"); // Different email but same username

        // When
        customerRepository.save(customer1);

        // Then
        assertThatThrownBy(() -> customerRepository.save(customer2))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void testUniqueEmailConstraint() {
        // Given
        Customer customer1 = TestDataFactory.createTestCustomer();
        Customer customer2 = TestDataFactory.createTestCustomer();
        customer2.setUsername("differentuser"); // Different username but same email

        // When
        customerRepository.save(customer1);

        // Then
        assertThatThrownBy(() -> customerRepository.save(customer2))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("Should update existing customer")
    void testUpdateCustomer() {
        // Given
        Customer customer = TestDataFactory.createTestCustomer();
        Customer saved = customerRepository.save(customer);

        // When
        saved.setEnabled(false);
        Customer updated = customerRepository.save(saved);

        // Then
        assertThat(updated.isEnabled()).isFalse();
        assertThat(updated.getUserId()).isEqualTo(saved.getUserId());

        // Verify in database
        Optional<Customer> found = customerRepository.findById(saved.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should delete customer")
    void testDeleteCustomer() {
        // Given
        Customer customer = TestDataFactory.createTestCustomer();
        Customer saved = customerRepository.save(customer);

        // When
        customerRepository.delete(saved);

        // Then
        Optional<Customer> found = customerRepository.findById(saved.getUserId());
        assertThat(found).isEmpty();
    }
}