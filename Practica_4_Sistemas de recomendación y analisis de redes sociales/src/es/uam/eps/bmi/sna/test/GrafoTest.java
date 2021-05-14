package es.uam.eps.bmi.sna.test;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import es.uam.eps.bmi.sna.graph.GraphUtil;
import es.uam.eps.bmi.sna.structure.IntParser;
import es.uam.eps.bmi.sna.structure.StringParser;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.ErdosRenyiGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph; 




public class GrafoTest {

	 
	 //Clases para los generadores de los grafos aletorios
	 
	static class GraphFactory implements Factory<Graph<Integer,Integer>> {
		    public Graph<Integer,Integer> create() {
		        return new UndirectedSparseGraph<Integer,Integer>();
		    }
		}

	static	class UndirectedGraphFactory implements Factory<UndirectedGraph<Integer,Integer>> {
		    public UndirectedGraph<Integer,Integer> create() {
		        return new UndirectedSparseGraph<Integer,Integer>();
		    }
		}

	static	class VertexFactory implements Factory<Integer> {
		    private Integer nVertices = 0;
		    public Integer create() {
		        return nVertices++;
		    }
		}

	static	class EdgeFactory implements Factory<Integer> {
		    private Integer nEdges = 0;
		    public Integer create() {
		        return nEdges++;
		    }
		}
	 
	
	 public static void main (String a[]) throws IOException {
		 
		 
		 
		 
		 //GENERACION DE LOS GRAFOS PEDIDOS
		 
		 
		 //Erdos 
		 /*
	     GraphFactory graphFactory1 = new GraphFactory();
	     VertexFactory vertexFactory1 = new VertexFactory();
	     EdgeFactory edgeFactory1 = new EdgeFactory();

		 
		 ErdosRenyiGenerator genErdos = new  ErdosRenyiGenerator( graphFactory1, vertexFactory1,  edgeFactory1, 1000, 0.5);
		 Graph grafo1 = genErdos.create();
		 HashMap<String,Integer> conteoGrados1 = new HashMap<>();
	     for(Object o: grafo1.getVertices()) {
	    	 conteoGrados1.put(o.toString(),  grafo1.getNeighbors(o).size()  );
	     }
	     
		 imprime( conteoGrados1, "data/grafo_Erdos.csv");
	     System.out.println(" \n Generacion Erdos correcta");

		 */
		 
		 
		  //BARABASI
		 /*
	     GraphFactory graphFactory = new GraphFactory();
	     VertexFactory vertexFactory = new VertexFactory();
	     EdgeFactory edgeFactory = new EdgeFactory();
	     int init_vertices=2;
	     int numEdgesToAttach = 2;
	     HashSet semillas = new HashSet<>(vertexFactory.create());
	     semillas.add(vertexFactory.create());
	     BarabasiAlbertGenerator genBArabasi = new BarabasiAlbertGenerator( graphFactory, vertexFactory, edgeFactory,  init_vertices,  numEdgesToAttach,  semillas);
	     Graph grafo2 =  genBArabasi.create();
	     int iteraciones = 1000;
	     genBArabasi.evolveGraph(iteraciones);	     
	     HashMap<String,Integer> conteoGrados = new HashMap<>();
	     for(Object o: grafo2.getVertices()) {
	    	 conteoGrados.put(o.toString(),  grafo2.getNeighbors(o).size()  );
	     }
	     
		 imprime( conteoGrados, "data/grafo_barabasi.csv");
	     System.out.println(" \n Generacion grafos creados correcta");

		 */
		 
		 
		 
		 //generacion del archivo de distribucion
		 
		
		String redSocial1 = "data/small1.csv";
		String redSocial2 = "data/small2.csv";
		String redSocial3 = "data/small3.csv";
		String redSocial4 = "data/twitter.csv";
		String redSocial5 = "data/facebook_combined.txt";
		String redSocial6 = "data/grafo_barabasi.csv";
		String redSocial7 = "data/grafo_Erdos.csv";

		String separator1 = ",";
		String separator2 = ",";
		String separator3 = ",";
		String separator4 = ","; 
		String separator5 = " ";//FAcebook
		String separator6 = ",";
		String separator7 = ",";

		
		
		generaArchivoGrados(redSocial1,separator1);
		generaArchivoGrados(redSocial2,separator2);
		generaArchivoGrados(redSocial3,separator3);
		generaArchivoGrados(redSocial4,separator4);
		generaArchivoGrados(redSocial5,separator5);
		generaArchivoGrados(redSocial6,separator6);
		generaArchivoGrados(redSocial7,separator7);

	    System.out.println(" \n GENERACION DE LA DISTRIBUCION FINALIZADA");
	     
	     
	     
	     
	     //PARADOJA DE LA AMISTAD
	    System.out.println(" \n PARADOJA AMISTAD");

	    
	    
	     
	 }
	
	 
	 
	 
	 static public void generaArchivoGrados(String redSocial,String separator) throws IOException {

		 GraphUtil gu;
		 String[] aux;
		 String aux2;
		 
		 System.out.println("===============================================");
	     System.out.println("Testing: " + redSocial);
	     gu = new GraphUtil(redSocial,  separator );
	     aux = redSocial.split("\\.");
	     aux2 = aux[0]; 
	     gu.printGradosToFile(aux2 + "_grade_counter.csv");
	     gu.printGradosNodosToFile(aux2 + "_grade_node_correspondency.csv"); //para comprobar 

	     
	     System.out.println("Grado promedio: " + gu.getGradoPromedio() );
	     System.out.println("u1: " + gu.calculaPromedioDePromedioDeAmigosDeCadaNodo());
	     System.out.println("u2: " + gu.calculaPromedioSobreArcos() );
	    System.out.println("mediana: " + gu.calculaMedianaGrados() );	     
	 }
	 
	 
	 
	 
	 
	 public static void imprime( HashMap<String,Integer> conteoGrados,String outputFile) throws IOException {
		 	FileWriter fileWriter = new FileWriter(outputFile);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print("NodoId, Grado" + "\n");

			for(String  n:conteoGrados.keySet()) {
				printWriter.print( n + "," + conteoGrados.get(n) + "\n");
			}
			
			printWriter.close();
			fileWriter.close();
	 }
	
	 
	
	 
	
	
}
