package com.mauri.movies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoviesApplication {

	public static void main(String[] args) {

		SpringApplication.run(MoviesApplication.class, args);
	}
	//al correr la aplicacion se ejecuta nuestro servidor en local
	//para probar las peticiones podemos usar postman , o cualquier aplicacion
	//que nos permita probar la api
}
