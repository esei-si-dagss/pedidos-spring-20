# Ejemplo Spring Data for JPA  (SI-2020, semana 3)
Ejemplo de creación de proyectos Spring Boot
Ejemplo de definición de repositorios Spring Data for JPA

## Previo
### Requisitos previos

* Servidor de BD MySQL
* Maven (versión > 3.5.x)
* (opcional) GIT
* (opcional) IDE Java (Eclipse, Netbeans, IntelliJ)

### Crear BD para los ejemplos  (si no se ha hecho antes)

* Crear BD "pruebas_si" en MySQL 

```
mysql -u root -p    [pedirá la contraseña de MySQL]

mysql> create database pruebas_si;
mysql> create user si@localhost identified by "si";
mysql> grant all privileges on pruebas_si.* to si@localhost;

```

Adicionalmente, puede ser necesario establecer un formato de fecha compatible
```
mysql> set @@global.time_zone = '+00:00';
mysql> set @@session.time_zone = '+00:00';
```

## CREAR PROYECTO SPRING BOOT
Existen varias alternativas
* Crear un proyecto Maven vacío e incluir las dependencias de los _starters_ de Spring Boot
* Usar Spring Tool Suite ([https://spring.io/tools](https://spring.io/tools)) y crear un nuevo proyecto _String Starter project_
* Crear el proyecto desde Spring Initializr ([https://start.spring.io/](https://start.spring.io/))

### Características del proyecto
```
Spring Boot version: 2.2.0
Type: Maven
Java version: 8
Packaging: Jar

Proyecto: 
   groupId: es.uvigo.mei
   artefactId: pedidos-spring
   version: 1.0
   package: es.uvigo.mei.pedidos
   
Dependencias a incluir:
	Spring Data JPA
    MySQLDriver
```

Fichero `pom.xml` resultante

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <parent>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-parent</artifactId>
                <version>2.4.0</version>
                <relativePath/> <!-- lookup parent from repository -->
        </parent>
        <groupId>es.uvigo.mei</groupId>
        <artifactId>pedidos-spring</artifactId>
        <version>1.0</version>
        <name>pedidos-spring</name>
        <description>Demo project for Spring Boot</description>

        <properties>
                <java.version>1.8</java.version>
        </properties>

        <dependencies>
                <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-data-jpa</artifactId>
                </dependency>

                <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <scope>runtime</scope>
                </dependency>
                <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-test</artifactId>
                        <scope>test</scope>
                </dependency>
        </dependencies>

        <build>
                <plugins>
                        <plugin>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-maven-plugin</artifactId>
                        </plugin>
                </plugins>
        </build>
</project>
```

Clase de configuración resultante (en `src/main/java/es/uvigo/mei/pedidos/PedidosSpringApplication`)
```java
package es.uvigo.mei.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PedidosSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(PedidosSpringApplication.class, args);
	}
}
```

## CREAR REPOSITORIOS

### Copiar clases de entidades (se usarán mismas las del ejemplo `pedidos-persistencia`)
Crear el directorio para el paquete `entidades` y copiar los ficheros Java con la definición de las entidades
```
mkdir -p src/main/java/es/uvigo/mei/pedidos/entidades
cd src/main/java/es/uvigo/mei/pedidos/entidades

wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/Articulo.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/Almacen.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/ArticuloAlmacen.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/ArticuloAlmacenId.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/Familia.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/Direccion.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/Cliente.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/Pedido.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/LineaPedido.java
wget https://raw.githubusercontent.com/esei-si-dagss/pedidos-persistencia-20/main/src/main/java/es/uvigo/mei/pedidos/entidades/EstadoPedido.java

pushd

```

### Crear los repositorios `FamiliaDAO` y  `ArticuloDAO`

#### Crear paquete `es.uvigo.mei.pedidos.daos`
```
mkdir -p src/main/java/es/uvigo/mei/pedidos/daos
cd src/main/java/es/uvigo/mei/pedidos/daos
```

#### Crear `FamiliaDAO` con el contenido:
```java
package es.uvigo.mei.pedidos.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.uvigo.mei.pedidos.entidades.Familia;

public interface FamiliaDAO extends JpaRepository<Familia, Long> {
	public List<Familia> findFamiliaByNombre(String nombre);
    
	@Query("SELECT f FROM Familia f WHERE f.descripcion LIKE %?1")
	public List<Familia> findFamiliaByPatronDescripcion(String patron);
}

```

#### Crear `ArticuloDAO` con el contenido:
```java
package es.uvigo.mei.pedidos.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.uvigo.mei.pedidos.entidades.Articulo;

public interface ArticuloDAO extends JpaRepository<Articulo, Long>{
	public List<Articulo> findArticuloByFamiliaId(Long id);
	public List<Articulo> findArticuloByNombre(String nombre);
    
	@Query("SELECT a FROM Articulo a WHERE a.descripcion LIKE %?1")
	public List<Articulo> findArticuloByPatronDescripcion(String patron);
}
```

Retornar a la carpeta principal
```
pushd
```
### Configurar _Datasource_

Editar fichero `src/main/resources/application.properties` y establecer los valores de conexión a la BD
```
spring.datasource.url=jdbc:mysql://localhost:3306/pruebas_si
spring.datasource.username=si
spring.datasource.password=si
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
# https://www.baeldung.com/hibernate-field-naming-spring-boot
```


### Crear 'main()' de prueba [Modificar `PedidosSpringApplication` en paquete `es.uvigo.mei.pedidos`] 
```java
package es.uvigo.mei.pedidos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import es.uvigo.mei.pedidos.daos.ArticuloDAO;
import es.uvigo.mei.pedidos.daos.FamiliaDAO;
import es.uvigo.mei.pedidos.entidades.Articulo;
import es.uvigo.mei.pedidos.entidades.Familia;

@SpringBootApplication
public class PedidosSpringApplication implements CommandLineRunner {
	@Autowired
	FamiliaDAO familiaDAO;
	
	@Autowired
	ArticuloDAO articuloDAO;
	
	public static void main(String[] args) {
		SpringApplication.run(PedidosSpringApplication.class, args);
	}
    
	@Override
	public void run(String... args) throws Exception {
		Familia f1 = new Familia("pepes", "familia  de pepes");
		f1 = familiaDAO.save(f1);		  
		  
		Familia f2 = new Familia("anas", "familia  de anas");
		f2 = familiaDAO.save(f1);		  

		articuloDAO.save(new Articulo("pepe1", "pepe1", f1, 10.0));
	    articuloDAO.save(new Articulo("pepe2", "pepe2", f1, 10.0));
	    articuloDAO.save(new Articulo("pepe3", "pepe3", f1, 10.0));

	    articuloDAO.save(new Articulo("ana1", "ana1", f2, 20.0));
	    articuloDAO.save(new Articulo("ana2", "ana2", f2, 20.0));
	    
	    List<Articulo> articulos = articuloDAO.findAll();	    
	    System.out.println("Todos los articulos");
	    for (Articulo a: articulos) {
	    	System.out.println(">> "+a.toString());	    	
	    }
	    
	    System.out.println("Articulos de familia pepes");
	    articulos = articuloDAO.findArticuloByFamiliaId(f1.getId());
	    for (Articulo a: articulos) {
	    	System.out.println(">> "+a.toString());	    	
	    }

	    System.out.println("Articulos segundones");
	    articulos = articuloDAO.findArticuloByPatronDescripcion("2");
	    for (Articulo a: articulos) {
	    	System.out.println(">> "+a.toString());	    	
	    }
	 }
}
```

### Ejecutar el 'main()' creado

En Spring Tool Suite: Proyecto 'pedidos-spring' `[botón derecho] > Run As > Spring Boot App`

Desde línea de comandos:
```
mvn spring-boot:run
```

ó

```
mvn install
java -jar target/pedidos-spring-1.0.jar
```
