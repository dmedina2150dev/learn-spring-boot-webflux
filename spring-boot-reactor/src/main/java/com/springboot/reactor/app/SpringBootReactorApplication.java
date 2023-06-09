package com.springboot.reactor.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.springboot.reactor.app.models.Equipo;
import com.springboot.reactor.app.models.Usuario;

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

	}

	public void ejemploFaltMap() throws Exception {
		List<String> usuariosArr = new ArrayList<>();

		usuariosArr.add("Dajan Medina");
		usuariosArr.add("Darwin Medina");
		usuariosArr.add("Dajanevi Medina");
		usuariosArr.add("Janeth de Oliveira");
		usuariosArr.add("Vefy Palacios");

		Flux.fromIterable(usuariosArr)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase())
				)
				.filter(usuario -> {

					return usuario.getApellido().toLowerCase().equals("medina");

				})
				.map(usuario -> {

					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;

				})
				.subscribe( u -> log.info(u.toString()) );
	}

	public void ejemploIterable() throws Exception {
		List<String> usuariosArr = new ArrayList<>();

		usuariosArr.add("Dajan Medina");
		usuariosArr.add("Darwin Medina");
		usuariosArr.add("Dajanevi Medina");
		usuariosArr.add("Janeth de Oliveira");
		usuariosArr.add("Vefy Palacios");

		Flux<String> nombres = Flux.fromIterable(usuariosArr);

		Flux<Usuario> usuarios = nombres
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					
					if( usuario.getApellido().equalsIgnoreCase("medina") ) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
					
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
