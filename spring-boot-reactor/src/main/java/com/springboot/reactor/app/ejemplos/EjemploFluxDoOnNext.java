package com.springboot.reactor.app.ejemplos;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springboot.reactor.app.models.Equipo;
import com.springboot.reactor.app.models.Usuario;

import reactor.core.publisher.Flux;

public class EjemploFluxDoOnNext {
	
	private static final Logger log = LoggerFactory.getLogger(EjemploFluxDoOnNext.class);

	public void ejemplo() {
		/*********************************************************************************/
		/* Empezamos con el flujo async conocemos primer metodo */
		/*********************************************************************************/
		log.info("NIVEL 1 - NOMBRES");
		Flux<String> nombres = Flux.just("Dajan", "Vefy", "Diego", "Pedro")
				.doOnNext(elemento -> System.out.println(elemento));
		/*
		 * doOnNext -> Es un Metodo evento que es parte del siclo de vida del
		 * observable. Se ejecuta cada vez que llega un elemento
		 */

		// DEBEMOS SUSCRIBIRNOS AL FLUJO PARA PODER OBTENER LOS RESULTADOS
		nombres.subscribe();

		/*********************************************************************************/
		/*********************************************************************************/

		/*********************************************************************************/
		/* Otra forma de hacer la llamada a un metodo INLINE */
		/*********************************************************************************/
		log.info("NIVEL 2 - NOMBRES2");
		Flux<String> nombres2 = Flux.just("JANETH", "Nevi", "Darwin", "Juena").doOnNext(System.out::println);
		// FORMA MAS SIMPLE DE HACER UNA LLAMADA A UN METODO EN ESTE CASO EL DE IMPRIMIR
		// PERO INLINE
		// TODO: SE LE DENOMINA (CALABLE)
		nombres2.subscribe(log::info);

		/*********************************************************************************/
		/*********************************************************************************/

		/*********************************************************************************/
		/* Emulamos error y lo manejamos en el observer */
		/*********************************************************************************/
		log.info("NIVEL 3 - NOMBRES3");
		Flux<String> nombres3 = Flux.just("JANETH", "Nevi", "", "Darwin", "Juena").doOnNext(e -> {
			if (e.isEmpty()) {
				throw new RuntimeException("Nombre no puede ser vacio");
			}

			System.out.println(e);

		});

		// Manejamos el error
		nombres3.subscribe(e -> log.info(e), error -> log.error(error.getMessage()));

		/*********************************************************************************/
		/*********************************************************************************/

		/*********************************************************************************/
		/* Completamos el observable con un metodo runnable */
		/*********************************************************************************/
		log.info("NIVEL 4 - NOMBRES4");
		Flux<String> nombres4 = Flux.just("Pepito", "Juanito", "Maria", "Alimaña", "Mala").doOnNext(e -> {
			if (e.isEmpty()) {
				throw new RuntimeException("Nombre no puede ser vacio");
			}

			System.out.println(e);

		});

		nombres4.subscribe(e -> log.info(e), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Ha finalizado la ejecucion del observable");
			}
		});

		/*********************************************************************************/
		/*********************************************************************************/

		/*********************************************************************************/
		/* Modificamos los datos con operadores como Map */
		/*********************************************************************************/
		log.info("NIVEL 5 - EQUIPOS");
		Flux<Equipo> teams = Flux.just("Real Madrid", "Barcelona", "Inter", "Milan")
				.map(equipo -> new Equipo(equipo, null)).doOnNext(e -> {
					if (e == null) {
						log.error("El equipo no sirve");
					}

					System.out.println(e);
				}).map(equipo -> {
					String nombre = equipo.getNombre().toLowerCase();
					equipo.setNombre(nombre);
					return equipo;
				});

		teams.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()));

		/*********************************************************************************/
		/*********************************************************************************/

		/*********************************************************************************/
		/* Modificamos los datos con operadores como Filter */
		/*********************************************************************************/
		log.info("NIVEL 6 - NOMBRES 5");
		Flux<Usuario> nombres5 = Flux
				.just("Pepito Perez", "Juanito Alimaña", "Maria Teresa", "Juanito Guarnizo", "Mala Rodriguez")
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> {
					return usuario.getNombre().toLowerCase().equals("juanito");
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

		nombres5.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Ha finalizado la ejecucion del observable");
			}
		});

		/*********************************************************************************/
		/*********************************************************************************/

		/*********************************************************************************/
		/* PRACTICA QUE YO REALICE POR MI CUENTA */
		/*********************************************************************************/
		log.info("NIVEL 7 - EQUIPOS2");
		Equipo equipo1 = new Equipo("Real Madrid", "España");
		Equipo equipo2 = new Equipo("Barcelona", "España");
		Equipo equipo3 = new Equipo("Inter", "Italia");
		Equipo equipo4 = new Equipo("Milan", "Italia");

		List<Equipo> grupoA = new ArrayList<Equipo>();

		grupoA.add(equipo1);
		grupoA.add(equipo2);
		grupoA.add(equipo3);
		grupoA.add(equipo4);

		Flux<Equipo> teams2 = Flux.just("Real Madrid", "Barcelona", "Inter", "Milan")
				.map(equipo -> new Equipo(equipo, null)).doOnNext(e -> {
					if (e == null) {
						log.error("El equipo no sirve");
					}

					System.out.println(e);
				}).map(equipo -> {
					String nombre = equipo.getNombre().toLowerCase();
					equipo.setNombre(nombre);
					return equipo;
				});

		teams2.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()));

		/*********************************************************************************/
		/*********************************************************************************/

		/*********************************************************************************/
		/* Los observables son inmutables */
		/*********************************************************************************/
		log.info("NIVEL 8 - NOMBRES6");
		Flux<String> nombres6 = Flux.just("Pepito Perez", "Juanito Alimaña", "Maria Teresa", "Juanito Guarnizo",
				"Mala Rodriguez");

		// TODO: ESTE ES OTRO FLUJO
		// Le creamos una variable para guardar este flujo que se crea a partir del
		// flujo nombres - y nos suscribimos mas abajo. PEro
		Flux<Usuario> usuarios = nombres6
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> {
					return usuario.getNombre().toLowerCase().equals("juanito");
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

		// TODO: AQUI SE MOSTRARA EL INICIO DEL FLUJO NO LA MODIFICACION
		nombres6.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Ha finalizado la ejecucion del observable");
			}
		});

		// TODO: AQUI SI MOSTRAR EL FLUJO USUARIOS CON LAS MODIFICACIONES DEL MAP Y DEL
		// FILTER
		usuarios.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Ha finalizado la ejecucion del observable");
			}
		});
		/*********************************************************************************/
		/*********************************************************************************/

		/*********************************************************************************/
		/* CREAMOS UN FLUX OSERVABLE A PARTIR DE UN LIST O ITERABLE */
		/*********************************************************************************/
		log.info("NIVEL 9 - NOMBRES7");
		List<String> usuariosArr = new ArrayList<>();

		usuariosArr.add("Dajan Medina");
		usuariosArr.add("Darwin Medina");
		usuariosArr.add("Dajanevi Medina");
		usuariosArr.add("Janeth de Oliveira");
		usuariosArr.add("Vefy Palacios");

		Flux<String> nombres7 = Flux.fromIterable(usuariosArr);
//				Flux.just("Pepito Perez", "Juanito Alimaña", "Maria Teresa", "Juanito Guarnizo", "Mala Rodriguez");

		Flux<Usuario> usuarios2 = nombres7
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

		usuarios2.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {

			@Override
			public void run() {
				log.info("Ha finalizado la ejecucion del observable");
			}
		});

		/*********************************************************************************/
		/*********************************************************************************/
	}

}
