package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil userUtil;

    private String existingUsername, nonExistingUsername;

    private UserEntity user;
    private List<UserDetailsProjection> userDetails;

    @BeforeEach
    public void setUp() {
        user = UserFactory.createUserEntity();
        existingUsername = "maria@gmail.com";
        nonExistingUsername = "test@gmail.com";
        userDetails = UserDetailsFactory.createCustomAdminUser(existingUsername);


        Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);

        Mockito.when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(userDetails);
        Mockito.when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenThrow(UsernameNotFoundException.class);

    }

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
        Mockito.when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(user));

        UserEntity result = service.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getUsername(), result.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Mockito.when(repository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

        UserDetails result = service.loadUserByUsername(existingUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDetails.get(0).getUsername(), result.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(nonExistingUsername));
	}
}
