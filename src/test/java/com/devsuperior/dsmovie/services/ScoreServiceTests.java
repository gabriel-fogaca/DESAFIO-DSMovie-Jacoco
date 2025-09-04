package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

    @Mock
    private ScoreRepository repository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserService userService;

    private ScoreEntity score;
    private ScoreDTO scoreDTO;
    private UserEntity user;
    private MovieEntity movie;
    private Long nonExistingId;

    @BeforeEach
    public void setUp() {
        nonExistingId = 2L;
        score = ScoreFactory.createScoreEntity();
        scoreDTO = ScoreFactory.createScoreDTO();
        user = UserFactory.createUserEntity();
        movie = MovieFactory.createMovieEntity();

        Mockito.when(userService.authenticated()).thenReturn(user);

        Mockito.when(repository.saveAndFlush(any())).thenReturn(score);
        Mockito.when(movieRepository.save(any())).thenReturn(movie);

        Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());
    }

	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
        Mockito.when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.of(movie));
        MovieDTO result = service.saveScore(scoreDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(movie.getId(), result.getId());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
        scoreDTO = new ScoreDTO(nonExistingId, 4.5);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.saveScore(scoreDTO));
	}
}
