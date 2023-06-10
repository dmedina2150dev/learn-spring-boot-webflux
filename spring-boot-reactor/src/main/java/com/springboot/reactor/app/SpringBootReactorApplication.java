package com.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.springboot.reactor.app.models.Comentarios;
import com.springboot.reactor.app.models.Usuario;
import com.springboot.reactor.app.models.UsuarioConComentario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		ejemploIterable();

		ejemploFaltMap();

		ejemploToString();

		ejemploFromCollectListToMono();

		ejemploUsuariComentariosFlatMap();

		ejemploUsuariComentariosZipWith();
		
		ejemploUsuariComentariosZipWithII();
		
		ejemploZipWithRangos();
		
		//ejemploInterval();
		
		//ejemploDelayElements();
		
		ejemploIntervaloInfinito();

	}
	
	public void ejemploIntervaloInfinito () throws InterruptedException {
		
		// Otro mecanismo de bloque
		CountDownLatch latch = new CountDownLatch(1);
		
		Flux.interval(Duration.ofSeconds(1))
			.doOnTerminate(() -> latch.countDown())// Va decrementando el latch
			.flatMap( i -> {
				if( i >= 5) {
					return Flux.error(new InterruptedException("Solo hasta 5!"));
				}
				
				return Flux.just(i);
			})
			.map( i -> "Holla " + i )
			.retry(2)// TODO este operador reintenta la operacion(n) cantidad de veces despues que el flujo falla
			.subscribe(
					s -> log.info(s),
					e -> log.error(e.getMessage())
					);
		
		latch.await(); // Espera a que se decremente por completo y lo suelta
	}
	
	public void ejemploDelayElements () throws InterruptedException {
		log.info("<---------------  ejemploDelayElements  --------------------->");
		Flux<Integer> rango = Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext( i -> log.info(i.toString()));
				
		// rango.subscribe(); // TODO: No se veria en el log porque se ejecuta en segundo plano
		//rango.blockLast(); // TODO: IMPORTANTE NO ES RECOMENDABLE PORQUE ESTE METODO SE SUSCRIBE PERO BLOQUEA LOS HILOS Y PODRIA CREAR CUELLOS DE BOTELLA
		// Se usa (blockLast) solo en modo de prueba para ver los resultados en el log
		
		// Otra forma
		rango.subscribe();
		//Thread.sleep(13000);
	}
	
	public void ejemploInterval () {
		log.info("<---------------  ejemploIntervalos  --------------------->");
		Flux<Integer> rango = Flux.range(1, 12);
		
		
		// TODO: Le aplicamos in intervalo
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		
		rango.zipWith(retraso, (ra, re) -> ra)
			.doOnNext( i -> log.info(i.toString()) )
			.subscribe(); // TODO: No se veria en el log porque se ejecuta en segundo plano
			//.blockLast();// TODO: IMPORTANTE NO ES RECOMENDABLE PORQUE ESTE METODO SE SUSCRIBE PERO BLOQUEA LOS HILOS Y PODRIA CREAR CUELLOS DE BOTELLA
			// Se usa (blockLast) solo en modo de prueba para ver los resultados en el log
		
	}
	
	public void ejemploZipWithRangos() {
		log.info("<---------------  ejemploZipWithRangos  --------------------->");
		// TODO: EL flujo podria ser desde afuera igual
		Flux<Integer> rangos = Flux.range(0, 4); 
		
		Flux.just(1, 2, 3, 4)
			.map( i -> (i * 2) )
			.zipWith( 
					Flux.range(0, 4), // TODO: EL flujo lo pasamos por parametro 
					(uno, dos) -> String.format("Primer Flux: %d, Segundo Flux: %d ", uno, dos))
			.subscribe( texto -> log.info(texto) );
	}
	
	public void ejemploUsuariComentariosZipWithII() {
		log.info("ejemploUsuariComentariosZipWithII");
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> {
			return new Usuario("Dajan", "Medina");
		});

		Mono<Comentarios> comentarioUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola Vefy, qué tal!");
			comentarios.addComentarios("Te amo con todo mi corazon");
			comentarios.addComentarios("Eres el amor de mi vida");
			comentarios.addComentarios("Me chupa la polla");

			return comentarios;
		});

		Mono<UsuarioConComentario> usuarioConComentario = usuarioMono
				.zipWith(comentarioUsuarioMono)
				.map( tuple -> {
					Usuario u = tuple.getT1();
					Comentarios c = tuple.getT2();
					
					return new UsuarioConComentario(u, c);
				});
				
		usuarioConComentario.subscribe(uc -> log.info(uc.toString()));

	}

	public void ejemploUsuariComentariosZipWith() {
		log.info("ejemploUsuariComentariosZipWith");
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> {
			return new Usuario("Pepe", "Agular");
		});

		Mono<Comentarios> comentarioUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe, qué tal!");
			comentarios.addComentarios("Hola Juan, qué tal!");
			comentarios.addComentarios("Hola Maria, qué tal!");
			comentarios.addComentarios("Hola Sol, qué tal!");

			return comentarios;
		});

		Mono<UsuarioConComentario> usuarioConComentario = usuarioMono
				.zipWith(comentarioUsuarioMono, (user, comentarioUsuario) -> new UsuarioConComentario(user, comentarioUsuario));
				
		usuarioConComentario.subscribe(uc -> log.info(uc.toString())); 
	
	}

	public void ejemploUsuariComentariosFlatMap() {
		log.info("ejemploUsuariComentariosFlatMap");
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> {
			return new Usuario("Dajan", "Medina");
		});

		Mono<Comentarios> comentarioUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola pepe, qué tal!");
			comentarios.addComentarios("Hola Juan, qué tal!");
			comentarios.addComentarios("Hola Maria, qué tal!");
			comentarios.addComentarios("Hola Sol, qué tal!");

			return comentarios;
		});

		usuarioMono.flatMap(u -> comentarioUsuarioMono.map(c -> new UsuarioConComentario(u, c)))
			.subscribe(uc -> log.info(uc.toString()));


	}

	public void ejemploFromCollectListToMono() throws Exception {
		log.info("ejemploFromCollectListToMono");
		List<Usuario> usuariosArr = new ArrayList<>();
		usuariosArr.add(new Usuario("Dajan", "Medina"));
		usuariosArr.add(new Usuario("Darwin", "Medina"));
		usuariosArr.add(new Usuario("Dajanevi", "Medina"));
		usuariosArr.add(new Usuario("Janeth", "De Oliveira"));
		usuariosArr.add(new Usuario("Vefy", "Palacios"));

		log.info("Comienzo Primera Lista");
		Flux.fromIterable(usuariosArr).collectList().subscribe(lista -> log.info(lista.toString()));

		log.info("Fin Primera Lista");

		log.info("Comienzo segunda Lista");
		Flux.fromIterable(usuariosArr).collectList().subscribe(lista -> {
			lista.forEach(item -> log.info(item.toString()));
		});
		log.info("Fin segunda Lista");
	}

	public void ejemploToString() throws Exception {
		log.info("ejemploToString");
		List<Usuario> usuariosArr = new ArrayList<>();
		usuariosArr.add(new Usuario("Dajan", "Medina"));
		usuariosArr.add(new Usuario("Darwin", "Medina"));
		usuariosArr.add(new Usuario("Dajanevi", "Medina"));
		usuariosArr.add(new Usuario("Janeth", "De Oliveira"));
		usuariosArr.add(new Usuario("Vefy", "Palacios"));

		Flux.fromIterable(usuariosArr).map(
				usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {

					if (nombre.contains("medina".toUpperCase())) {
						return Mono.just(nombre);
					} else {
						return Mono.empty();
					}

				}).map(nombre -> {

					return nombre.toLowerCase();

				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploFaltMap() throws Exception {
		log.info("ejemploFaltMap");
		List<String> usuariosArr = new ArrayList<>();

		usuariosArr.add("Dajan Medina");
		usuariosArr.add("Darwin Medina");
		usuariosArr.add("Dajanevi Medina");
		usuariosArr.add("Janeth de Oliveira");
		usuariosArr.add("Vefy Palacios");

		Flux.fromIterable(usuariosArr)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {

					if (usuario.getApellido().equalsIgnoreCase("medina")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}

				}).map(usuario -> {

					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;

				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploIterable() throws Exception {
		log.info("ejemploIterable");
		List<String> usuariosArr = new ArrayList<>();

		usuariosArr.add("Dajan Medina");
		usuariosArr.add("Darwin Medina");
		usuariosArr.add("Dajanevi Medina");
		usuariosArr.add("Janeth de Oliveira");
		usuariosArr.add("Vefy Palacios");

		Flux<String> nombres = Flux.fromIterable(usuariosArr);

		Flux<Usuario> usuarios = nombres
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> {

					return usuario.getApellido().toLowerCase().equals("medina");

				}).doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("Nombres no puede ser vacio");
					}

					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));

				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Ha finalizado la ejecucion del observable");
			}
		});
	}
}
