# Programacion reactive

Es un paradigma de programacion, rompe el esquema de la programacion funcional. Es orientado a objeto y con todas las caracteristicas de la programacion Java. Pero la forma en que se programa de forma encadenada a traves de una estructura funcional, por lo mismo se considera un paradigma. Esta programacion funcional esta orientada a flujos de datos (streams) y a su vez permite la propagacion de los cambios, es decir se pueden modificar y agregar  operadores a nuestros datos con la que podemos realizar alguna logica o proceso de forma asincrona. 

Combina las mejores ideas del patron Observer, Iterator y la programacion funcional. 

Donde contamos con un sujeto (Subject) este sujeto seria nuestro observable o (publicher) como tambien se le conoce. Se encarta de emitir, de manejar y procesar el flujo de datos y la propagacion de los cambios que sucedan y notifica a los observadores.

Obserer reciben las notificaciones enviadas por el subject. Para esto deben estar subscritos.

## Caracteristicas importantes de la programacion reactiva.

* Crea facilmente eventos streams o flujos de datos continuos.
* Transforma y compone flujos con los operadores (map, filter, merge, delay, foreach, etc)
* Suscribir a cualquier flujo observable para realizar alguna tarea.
* Multi-plataforma Java, Javascript, Scala, C#, C++, Python, PHP y otros.

**En webflux el api reacto que se usa spring los observables son los publisher y estos van a tener dos categorias**
**Flux**: Maneja de 0 a (n) elemento
En un Flux vamos a manejar colecciones de objetos.
```Java
Flux<String> just = Flux.just("1","2","3");
```

**Mono**: Maneja de 0 o 1 elemento.
En el mono vamos a manejar un solo objeto.
```Java
Mono<String> just = Flux.just("1");
```

* Trabajan con flujo de datos continuos asíncronos
* Manejo de contrapresión o latencia (non-blocking)
* Los operadores reducen notablemente las tareas de transformaciones
* Los flujos reactivos o los observables pueden ser creados desde otras fuentes como stream, listas, intevalos, rangos, etc.
* Son Cancelables. 
* Pueden ser finitos o infinitos.
* Son inmutables, significa que ninguno de ellos no se puede modificar en absoluto. Pero cuando usamos algun operador como (map, filter, etc) se modifica la forma en la que se representan los datos, mas el objeto como tal. cuando invocamos estos metodos se crea una nueva instancia y la instancia anterior del flujo se deja tal cual como estaba.
* Concurrencia hecho simple, los observables y los scheduler o programadores nos permiten abstraer y simplificar bastante todo lo que es concurrencia, sincronozacion que existe en los hilos todo lo que se esta ejecutando al mismo tiempo.
* Manejo de errores async. Dentro de los observables(flujo reactivo) contamos con mecanismos mucho mejores para el manejo de errores, tiene una seria de eventos, para procesar y manejar cualquier problema que pueda ocurrir. 
* AL fallar se puede volver a re-intentar. Es un operador que nos permite en caso de error reintentar el flujo.


# Iniciativa que regula las api de programacion reactiva 

[Reactive Streams](https://www.reactive-streams.org/)
[Project Reactor](https://projectreactor.io/) Implementación que utiliza spring para la programacion reactiva.
[ReactiveX](https://reactivex.io/) Implementación de programación reactiva multiplataforma (Por debajo funciona con la misma implementacion reactor)



## La Contrapresion

Por defecto cuando nos subscribimos a un observable, solicitamos la cantidad maxima que puede enviar el productor. Es decir que el observable nos envie todos los elementos de una sola vez.
Pero existe el problema de si tenemos recursos limitados, de hardware o de software, esto podria ser pesado o abrumador de procesar por el subscriptor poder procesar todo de una sola vez.
Aqui es donde entra la contrapresion, poder indicar el suscriptor al productor la cantidad de elementos que debe enviar por cada vez. Por ejemplo en lugar de enviar todos los elementos que envie 5 por solicitud.