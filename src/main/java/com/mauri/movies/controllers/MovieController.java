package com.mauri.movies.controllers;

import com.mauri.movies.models.Movie;
import com.mauri.movies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController //Inidca que va a funcionar esta clase como controlador
@RequestMapping("/api/movies")//Indica ruta que gestiona nuestras peticiones
//el controllador siempre escucha las peticiones que se hacen en esta ruta url
public class MovieController {

    //indicamos con que repositorios va a trabajar
    @Autowired // nos evita tener que crear nuevos objetos del controlador cada que ves que ejecutamos la clase controladora
    private MovieRepository movieRepository;

    //acciones crud y ademas votar y rankear
    //para indicar que parte se encarga de cada peticion
    //hay que entender 2 cosas primero el verbo (acción) de la peticion y
    // el segundo es el longTiming (extensión, ej: @GetMapping("/all")) de la peticion.
    //entonces este metodo se va a ejecutar cuando me llegue una peticion en este
    //caso get con una url "/api/movies/all", si tengo varias peticiones get las diferencio con un final diferente de url
    //como yo en este caso quiero que directamente se llame al metodo con "/api/movies" no le agrego nada al @GetMapping
    // y esto me deveria devolver el repositorio de peliculas completo cuando hago una consulta a esta url
    //@CrossOrigin nos permite ejecutar nuestras consultas desde otro puerto que no sea nuestro localhost
    //esto lo que me permite es consumir mi servicio desde otras cosas que no sean postman o thunder, ej desde un front
    //lo debo incluir en todos mis endpoints que quiero que se puedan consumir asi.
    @CrossOrigin
    @GetMapping
    public List<Movie> getAllMovies(){
        return movieRepository.findAll();//este metodo proviene de exteneder el JPA repository
    }// si no existe pelicula cargada, me devolvera una lista de peliculas vacia

    //solicitar una unica movie, el {} va a indicar en este caso que le voy a poder pasar una variable en este caso id
    //entonces si hago una consulta a una url ej "/api/movies/3" esto nos deberia traer la pelicula 3
    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id){
        //para indicar que el id recibido por parametro es el mismo que el que se pasa por la url
        //usamos la anotacion @PathVariable
        //findByAll me devuelve un tipo de dato Optional<Movie> por que puede ser que lo encuentre o no
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }//si no encuentro la peli voy a necesitar devolver un tipo de dato distinto para indicarlo,
    //por lo tanto puedo usar ReponseEntity para indicar el tipo de retorno.
    //ResponseEntity indica que vamos a devolver una respuesta, en el caso de que sea satisfactoria, devolveriamos una movie
    //y en el caso de que no exista

    //en el caso de crear una pelicula usamos una solicitud post por lo tanto usamos la anotacion @PostMapping
    @CrossOrigin
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie){
         //para indicar que el cliente nos manda el parametro movie usamos
         //la anotacion @RequestBody. Esto es por que:
        //En el metodo Post no tenemos la variable en el Path, en este caso viene encriptada en el body de la peticion.

        //el metodo save recibe una entidad y la guarda
        Movie savedMovie = movieRepository.save(movie);
        return  ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
        //devolvemos el estado de la consulta, si es correcta sera un 201, podemos
        // usar la clase HttpStatus y su enum CREATED en este caso para indicarlo
    }//retornamos un ReponseEntity indico si la operacion ocurrio de forma exitosa o no
    //siempre por convencion si creamos un elemento nuevo, lo devolvemos.

    //en el caso de querer eliminar usamos un metodo delete con la anotacion @DeleteMapping y una extension
    //con el parametro por el cual quiero eliminar, el cual seria el id
    //por lo tanto si accedo a la url "api/movies/3" con el metodo delete eliminaria la pelicula 3
    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id){
        //si no existe, devolvemos una respuesta de notFound (un 404)
        if(!movieRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        //si el if no se ejecuta es que lo encontro
        movieRepository.deleteById(id);
        return ResponseEntity.noContent().build();//respuesta de contenido eliminado exitosamente, no hay contenido.
    }//retornamos un ReponseEntity<Void> ya que vamos a devolver un elemento vacio en el caso de exito.

    //Para modificar usamos el @PutMapping("/{id}"), recibimos metodo Put, y una ruta
    //ej si la ruta de la consulta es "api/movies/8" modificara esa pelicula si existe con el contenido que le pase
    //en su respectivo body
    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id,@RequestBody Movie updateMovie){
        //el id me va a venir por la ruta y los datos a actualizar por el body
        //si no existe, retorno un response de no encontrado
        if(!movieRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        //si la encuentro, le establesco el id que le corresponde, ya que me vienen todos los datos actualizar menos el id
        updateMovie.setId(id);
        // y despues guardo la peli, save si ya existe un elemento con ese id lo actualiza, sino crea el nuevo elemento.
        Movie savedMovie = movieRepository.save(updateMovie);
        return  ResponseEntity.ok(savedMovie);
    }

    //crud ya terminado, ahora funcionalidades aparte

    @CrossOrigin
    @GetMapping("/vote/{id}/{rating}")
    public  ResponseEntity<Movie> voteMovie(@PathVariable Long id, @PathVariable double rating){
        //si no la encuentro
        if(!movieRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        //si la encuentro
        Optional<Movie> optional = movieRepository.findById(id);
        Movie movie = optional.get();
        //movie.rating tiene la votacion actual de la pelicula, y movie.votes tiene la cantidad de votos, por lo tanto
        // debo realizar el calculo de la media para obtener el rating.
        //(necesito numero total de votos * rating + rating recibido por parametro)/movie.votes + 1
        // el +1 es por que estoy sumando un voto ahora.
        double newRating = ((movie.getVotes() * movie.getRating())+ rating) / movie.getVotes() + 1;

        movie.setVotes(movie.getVotes()+1);
        movie.setRating(newRating);

        Movie savedMovie =movieRepository.save(movie);
        return ResponseEntity.ok(savedMovie);
    }
}
//para probarlo vamos al movieApplication
