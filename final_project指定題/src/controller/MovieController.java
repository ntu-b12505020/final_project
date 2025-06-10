package controller;

import model.Movie;
import model.MovieDAO;

import java.util.List;

public class MovieController {
    private MovieDAO movieDAO;

    public MovieController() {
        this.movieDAO = new MovieDAO();
    }

    // 取得所有movies
    public List<Movie> getAllMovies() {
        return movieDAO.getAllMovies();
    }
}
