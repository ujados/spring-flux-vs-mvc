# Spring Flux vs MVC Performance Test

Este proyecto compara el rendimiento de dos enfoques en Spring Boot:

- ✅ **Controlador bloqueante (Spring MVC)** usando `Thread.sleep`
- ⚡ **Controlador reactivo (Spring WebFlux)** usando `Mono.delay`

El objetivo es simular una operación de 100 ms y medir cómo se comportan ambos estilos bajo carga concurrente.

## 🚀 Cómo ejecutar

1. Asegúrate de tener Java 17+ y Maven.
2. Levanta la aplicación Spring Boot:
```
./mvnw spring-boot:run
```

3. Comprueba que los endpoints respondan:
```
http://localhost:8080/blocking
http://localhost:8080/reactive
```
Ejecuta la clase de test PerformanceTest.java desde tu IDE o línea de comandos.

## Recomendaciones para el test
✅ Ejecutar tests uno después del otro (no en paralelo) para comparar el rendimiento de forma aislada.

🚫 No ejecutes ambos tests al mismo tiempo, ya que competirán por recursos del sistema y los resultados serán menos fiables.
De esta forma se vería la aplicación en un uso mixto bloqueante/ no bloqueante.

Cada test realiza múltiples llamadas concurrentes al endpoint correspondiente y mide:
```
Tiempo total de ejecución
Promedio por petición
Throughput (peticiones por segundo)
```


