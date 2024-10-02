package com.mauri.movies.repositories;

import com.mauri.movies.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Float> {
}
