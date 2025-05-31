package com.foodopia.authentication.unitTests;

import com.foodopia.authentication.domain.AbstractFoodopiaUser;
import com.foodopia.authentication.dto.RequestUserDto;
import com.foodopia.authentication.dto.UserInfoResponse;
import com.foodopia.authentication.entity.*;
import com.foodopia.authentication.exception.UserAlreadyExistsException;
import com.foodopia.authentication.exception.UserNotFoundException;
import com.foodopia.authentication.repository.*;
import com.foodopia.authentication.service.impl.IUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AdministratorRepository administratorRepository;

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private KitchenUserRepository kitchenUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private IUserServiceImpl userService;

    private RequestUserDto testUserDto;
    private Customer testCustomer;
    private Administrator testAdmin;
    private Operator testOperator;
    private KitchenUser testKitchenUser;

    @BeforeEach
    void setUp() {
        testUserDto = new RequestUserDto();
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setPassword("Password123");
        testUserDto.setConfirmPassword("Password123");

        testCustomer = new Customer("testuser", "test@example.com");
        testCustomer.setUserId("customer123");
        testCustomer.setPassword("encodedPassword");

        testAdmin = new Administrator("adminuser", "admin@example.com", "SUPER_ADMIN");
        testAdmin.setUserId("admin123");
        testAdmin.setPassword("encodedPassword");

        testOperator = new Operator("operatoruser", "operator@example.com", "OPERATIONS");
        testOperator.setUserId("operator123");
        testOperator.setPassword("encodedPassword");
        testOperator.setPermissions(Arrays.asList("READ", "WRITE"));

        testKitchenUser = new KitchenUser("kitchenuser", "kitchen@example.com", "PREP_STATION_1");
        testKitchenUser.setUserId("kitchen123");
        testKitchenUser.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("Should find customer by username")
    void testFindUserByUsername_Customer() {
        // Given
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));

        // When
        Optional<AbstractFoodopiaUser> result = userService.findUserByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isInstanceOf(Customer.class);
        assertThat(result.get().getUsername()).isEqualTo("testuser");

        verify(customerRepository).findByUsername("testuser");
        verify(administratorRepository, never()).findByUsername(anyString());
        verify(operatorRepository, never()).findByUsername(anyString());
        verify(kitchenUserRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Should find admin by username when customer not found")
    void testFindUserByUsername_Admin() {
        // Given
        when(customerRepository.findByUsername("adminuser")).thenReturn(Optional.empty());
        when(administratorRepository.findByUsername("adminuser")).thenReturn(Optional.of(testAdmin));

        // When
        Optional<AbstractFoodopiaUser> result = userService.findUserByUsername("adminuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isInstanceOf(Administrator.class);
        assertThat(result.get().getUsername()).isEqualTo("adminuser");

        verify(customerRepository).findByUsername("adminuser");
        verify(administratorRepository).findByUsername("adminuser");
        verify(operatorRepository, never()).findByUsername(anyString());
        verify(kitchenUserRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Should return empty when user not found by username")
    void testFindUserByUsername_NotFound() {
        // Given
        when(customerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        when(administratorRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        when(operatorRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<AbstractFoodopiaUser> result = userService.findUserByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();

        verify(customerRepository).findByUsername("nonexistent");
        verify(administratorRepository).findByUsername("nonexistent");
        verify(operatorRepository).findByUsername("nonexistent");
        verify(kitchenUserRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindUserByEmail_Success() {
        // Given
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testCustomer));

        // When
        Optional<AbstractFoodopiaUser> result = userService.findUserByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");

        verify(customerRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindUserById_Success() {
        // Given
        when(customerRepository.findById("customer123")).thenReturn(Optional.of(testCustomer));

        // When
        Optional<AbstractFoodopiaUser> result = userService.findUserById("customer123");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("customer123");

        verify(customerRepository).findById("customer123");
    }

    @Test
    @DisplayName("Should check username availability - available")
    void testIsUsernameAvailable_Available() {
        // Given
        when(customerRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(administratorRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(operatorRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        // When
        boolean available = userService.isUsernameAvailable("newuser");

        // Then
        assertThat(available).isTrue();
    }

    @Test
    @DisplayName("Should check username availability - not available")
    void testIsUsernameAvailable_NotAvailable() {
        // Given
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));

        // When
        boolean available = userService.isUsernameAvailable("testuser");

        // Then
        assertThat(available).isFalse();

        verify(customerRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should check email availability - available")
    void testIsEmailAvailable_Available() {
        // Given
        when(customerRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(administratorRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(operatorRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        // When
        boolean available = userService.isEmailAvailable("new@example.com");

        // Then
        assertThat(available).isTrue();
    }

    @Test
    @DisplayName("Should create customer successfully")
    void testCreateCustomer_Success() {
        // Given
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(administratorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(operatorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(administratorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(operatorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        assertThatCode(() -> userService.createCustomer(testUserDto))
                .doesNotThrowAnyException();

        // Then
        verify(passwordEncoder).encode("Password123");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when creating customer with existing username")
    void testCreateCustomer_UsernameExists() {
        // Given
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));

        // When & Then
        assertThatThrownBy(() -> userService.createCustomer(testUserDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username already exists: testuser");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating customer with existing email")
    void testCreateCustomer_EmailExists() {
        // Given
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(administratorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(operatorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testCustomer));

        // When & Then
        assertThatThrownBy(() -> userService.createCustomer(testUserDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email already exists: test@example.com");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create admin successfully")
    void testCreateAdmin_Success() {
        // Given
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(administratorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(operatorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(administratorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(operatorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123")).thenReturn("encodedPassword");
        when(administratorRepository.save(any(Administrator.class))).thenReturn(testAdmin);

        // When
        assertThatCode(() -> userService.createAdmin(testUserDto, "SUPER_ADMIN"))
                .doesNotThrowAnyException();

        // Then
        verify(passwordEncoder).encode("Password123");
        verify(administratorRepository).save(any(Administrator.class));
    }

    @Test
    @DisplayName("Should create operator successfully")
    void testCreateOperator_Success() {
        // Given
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(administratorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(operatorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(administratorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(operatorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123")).thenReturn("encodedPassword");
        when(operatorRepository.save(any(Operator.class))).thenReturn(testOperator);

        // When
        assertThatCode(() -> userService.createOperator(testUserDto, "OPERATIONS"))
                .doesNotThrowAnyException();

        // Then
        verify(passwordEncoder).encode("Password123");
        verify(operatorRepository).save(any(Operator.class));
    }

    @Test
    @DisplayName("Should create kitchen user successfully")
    void testCreateKitchenUser_Success() {
        // Given
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(administratorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(operatorRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(administratorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(operatorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123")).thenReturn("encodedPassword");
        when(kitchenUserRepository.save(any(KitchenUser.class))).thenReturn(testKitchenUser);

        // When
        assertThatCode(() -> userService.createKitchenUser(testUserDto, "PREP_STATION_1"))
                .doesNotThrowAnyException();

        // Then
        verify(passwordEncoder).encode("Password123");
        verify(kitchenUserRepository).save(any(KitchenUser.class));
    }

    @Test
    @DisplayName("Should update user status successfully")
    void testUpdateUserStatus_Success() {
        // Given
        when(customerRepository.findById("customer123")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // When
        assertThatCode(() -> userService.updateUserStatus("customer123", false))
                .doesNotThrowAnyException();

        // Then
        assertThat(testCustomer.isEnabled()).isFalse();
        verify(customerRepository).findById("customer123");
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user status")
    void testUpdateUserStatus_UserNotFound() {
        // Given
        when(customerRepository.findById("nonexistent")).thenReturn(Optional.empty());
        when(administratorRepository.findById("nonexistent")).thenReturn(Optional.empty());
        when(operatorRepository.findById("nonexistent")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUserStatus("nonexistent", false))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found: nonexistent");
    }

    @Test
    @DisplayName("Should save customer user")
    void testSaveUser_Customer() {
        // Given
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // When
        userService.saveUser(testCustomer);

        // Then
        verify(customerRepository).save(testCustomer);
        verify(administratorRepository, never()).save(any());
        verify(operatorRepository, never()).save(any());
        verify(kitchenUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save admin user")
    void testSaveUser_Admin() {
        // Given
        when(administratorRepository.save(testAdmin)).thenReturn(testAdmin);

        // When
        userService.saveUser(testAdmin);

        // Then
        verify(administratorRepository).save(testAdmin);
        verify(customerRepository, never()).save(any());
        verify(operatorRepository, never()).save(any());
        verify(kitchenUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should build user info response for customer")
    void testBuildUserInfoResponse_Customer() {
        // When
        UserInfoResponse response = userService.buildUserInfoResponse(testCustomer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("customer123");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo(AbstractFoodopiaUser.Role.CUSTOMER);
        assertThat(response.getAdminLevel()).isNull();
        assertThat(response.getStation()).isNull();
        assertThat(response.getDepartment()).isNull();
    }

    @Test
    @DisplayName("Should build user info response for admin")
    void testBuildUserInfoResponse_Admin() {
        // When
        UserInfoResponse response = userService.buildUserInfoResponse(testAdmin);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("admin123");
        assertThat(response.getUsername()).isEqualTo("adminuser");
        assertThat(response.getRole()).isEqualTo(AbstractFoodopiaUser.Role.ADMIN);
        assertThat(response.getAdminLevel()).isEqualTo("SUPER_ADMIN");
        assertThat(response.getStation()).isNull();
        assertThat(response.getDepartment()).isNull();
    }

    @Test
    @DisplayName("Should build user info response for operator")
    void testBuildUserInfoResponse_Operator() {
        // When
        UserInfoResponse response = userService.buildUserInfoResponse(testOperator);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("operator123");
        assertThat(response.getUsername()).isEqualTo("operatoruser");
        assertThat(response.getRole()).isEqualTo(AbstractFoodopiaUser.Role.OPERATOR);
        assertThat(response.getDepartment()).isEqualTo("OPERATIONS");
        assertThat(response.getPermissions()).containsExactly("READ", "WRITE");
        assertThat(response.getAdminLevel()).isNull();
        assertThat(response.getStation()).isNull();
    }

    @Test
    @DisplayName("Should build user info response for kitchen user")
    void testBuildUserInfoResponse_KitchenUser() {
        // When
        UserInfoResponse response = userService.buildUserInfoResponse(testKitchenUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("kitchen123");
        assertThat(response.getUsername()).isEqualTo("kitchenuser");
        assertThat(response.getRole()).isEqualTo(AbstractFoodopiaUser.Role.KITCHEN);
        assertThat(response.getStation()).isEqualTo("PREP_STATION_1");
        assertThat(response.getAdminLevel()).isNull();
        assertThat(response.getDepartment()).isNull();
    }

    @Test
    @DisplayName("Should update password successfully")
    void testUpdatePassword_Success() {
        // Given
        when(customerRepository.findById("customer123")).thenReturn(Optional.of(testCustomer));
        when(passwordEncoder.encode("NewPassword123")).thenReturn("newEncodedPassword");
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // When
        assertThatCode(() -> userService.updatePassword("customer123", "NewPassword123"))
                .doesNotThrowAnyException();

        // Then
        assertThat(testCustomer.getPassword()).isEqualTo("newEncodedPassword");
        verify(customerRepository).findById("customer123");
        verify(passwordEncoder).encode("NewPassword123");
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @DisplayName("Should throw exception when updating password for non-existent user")
    void testUpdatePassword_UserNotFound() {
        // Given
        when(customerRepository.findById("nonexistent")).thenReturn(Optional.empty());
        when(administratorRepository.findById("nonexistent")).thenReturn(Optional.empty());
        when(operatorRepository.findById("nonexistent")).thenReturn(Optional.empty());
        when(kitchenUserRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updatePassword("nonexistent", "NewPassword123"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found: nonexistent");

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should validate password successfully")
    void testValidatePassword_Success() {
        // Given
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);

        // When
        boolean isValid = userService.validatePassword("Password123", "encodedPassword");

        // Then
        assertThat(isValid).isTrue();
        verify(passwordEncoder).matches("Password123", "encodedPassword");
    }

    @Test
    @DisplayName("Should validate password fail for wrong password")
    void testValidatePassword_Fail() {
        // Given
        when(passwordEncoder.matches("WrongPassword", "encodedPassword")).thenReturn(false);

        // When
        boolean isValid = userService.validatePassword("WrongPassword", "encodedPassword");

        // Then
        assertThat(isValid).isFalse();
        verify(passwordEncoder).matches("WrongPassword", "encodedPassword");
    }
}