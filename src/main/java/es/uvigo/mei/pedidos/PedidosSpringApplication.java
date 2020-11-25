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
