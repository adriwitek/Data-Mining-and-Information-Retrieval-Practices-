package es.uam.eps.bmi.sna.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class GraphUtil {

	
	private HashMap<String,Integer> nodosGrados;
	HashMap<String, HashSet<String>> arcos;
	private String dataset;  
	private String separator;
	HashMap<Integer, Integer> contador;
	
	public GraphUtil(String dataset, String separator ) {
		
		nodosGrados = new HashMap<>();
		arcos = new HashMap<>();
		this.dataset = new String(dataset);
		this.separator= new String(separator);
		
	}
	
	
	
	public void printGradosToFile(String outputFile) throws IOException {
		
		this.loadData();
		
		contador = new HashMap<>(); //Grado,numero de nodos con ese grado
		Integer grado;
		
		for(String n :nodosGrados.keySet()) {
			grado = nodosGrados.get(n);
			if(contador.containsKey(grado)) {
				contador.put(grado, 1 + contador.get(grado)); 
			}else {
				contador.put(grado, 1 ); 

			}
		}
		
		
		//imprimimos
		FileWriter fileWriter = new FileWriter(outputFile);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		
		printWriter.print("Grado, Num Nodos" + "\n");

		for(Integer i : contador.keySet()) {
			printWriter.print(i + "," + contador.get(i) + "\n");

		}
		
		printWriter.close();
		fileWriter.close();

	}
	
	
	
	//funcion de depuracion
	public void printGradosNodosToFile(String outputFile) throws IOException {
		
		//this.loadData();
		
		FileWriter fileWriter = new FileWriter(outputFile);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("NodoId, Grado" + "\n");

		for(String n:nodosGrados.keySet()) {
			printWriter.print(n + "," + nodosGrados.get(n) + "\n");

		}
		
		printWriter.close();
		fileWriter.close();
	}
	

	
	
	private void loadData() throws FileNotFoundException {
		
		String user1, user2;
		HashSet<String> auxHashSet;		
		System.out.println(dataset);
		File file = new File(this.dataset);
		Scanner reader = new Scanner(file);
		Boolean newArcoAdded = false;
		
		while (reader.hasNextLine()) {
			
			String[] line = reader.nextLine().split(separator);

			// Guarda los datos.
			user1 =   line[0] ;
			user2 =   line[1] ;
			
			
			newArcoAdded = false;
			
			
			
			//de u a v
			if(!arcos.containsKey(user1)  ) { 
				auxHashSet = new HashSet();
				auxHashSet.add(user2);
				arcos.put(user1, auxHashSet); //ponemos el arco en 1
				newArcoAdded = true;		
			}else {
				if(!arcos.get(user1).contains(user2)) {
					arcos.get(user1).add(user2);
					newArcoAdded = true;
				} 	
			}
			
			
			//de v a u
			if( !arcos.containsKey(user2) ) { 
				auxHashSet = new HashSet();
				auxHashSet.add(user1);
				arcos.put(user2, auxHashSet); //ponemos el arco en 2
				newArcoAdded = true;		
			}else {
				if(!arcos.get(user2).contains(user1)){
					arcos.get(user2).add(user1);
					newArcoAdded = true;
				} 			
			}
			
			
			if(newArcoAdded) {
				addNodo(user1);
				addNodo(user2);
			}
					
			
		}

		reader.close();

	}
	
	
	
	
	public void addNodo(String n) {
		if(!nodosGrados.containsKey(n)) nodosGrados.put(n, 1);
		else nodosGrados.put(n, 1+nodosGrados.get(n) );	
	}
	
	
	public HashMap<String,Integer> getGrados(){
		return this.nodosGrados;
	}
	
	
	public 	HashMap<String, HashSet<String>> getArcos(){
		return this.arcos;
	}
	
	
	public double getGradoPromedio() {
		//return this.gradoPromedio;
		double sum = 0.0;
		for(String n :nodosGrados.keySet()) {
			sum += (double) nodosGrados.get(n);
		}	
		return (double) sum/nodosGrados.size();
	}
	
	
	
	private double calculaPromediDeAmigosDeUnNodo(String n) {
		double sumatorio = 0.0;
		
		for(String vecino : arcos.get(n)) {
			sumatorio += nodosGrados.get(vecino);
		}
		
		return (double) sumatorio/nodosGrados.get(n);
	}
	
	
	
	
	//funcion a llamar para paradoja amistad: u1
	public double calculaPromedioDePromedioDeAmigosDeCadaNodo(){
		
		double sumatorioFinal = 0.0;
		
		for(String nodo :nodosGrados.keySet()) {
			sumatorioFinal += this.calculaPromediDeAmigosDeUnNodo(nodo);
			
		}
		
		return (double) sumatorioFinal/nodosGrados.size();
	}
	
	
	//metodo a llamar para la paradoja amistad u2
	public double calculaPromedioSobreArcos() {
		double sumatorioFinal = 0.0;
		Integer contador = 0;
		HashSet<String> usersExplorados = new HashSet<>();
		for(String nodo : arcos.keySet()) {
			for(String vecino : arcos.get(nodo)) {
				if(usersExplorados.contains(vecino))continue;
				sumatorioFinal += (double) nodosGrados.get(vecino) + nodosGrados.get(nodo) ;
				contador ++;
			}
			usersExplorados.add(nodo);
		}

		return (double) sumatorioFinal / (2*contador);
	}
	
	
	//OK FUNCIONANDO CORRECTAMENTE
	public double calculaMedianaGrados() {
		
		LinkedHashMap<String,Integer> linkMap  = new LinkedHashMap<>(nodosGrados);
		// 1. get entrySet from LinkedHashMap object
        Set<Map.Entry<String, Integer>> sett = linkMap.entrySet();
        
        // 2. convert LinkedHashMap to List of Map.Entry
        List<Map.Entry<String, Integer>> listaEntradas = new ArrayList<Map.Entry<String, Integer>>(sett);
 
        Collections.sort(listaEntradas, 
                new Comparator<Map.Entry<String, Integer>>() {
 
            @Override
            public int compare(Entry<String, Integer> es1, Entry<String, Integer> es2) {
                return es1.getValue().compareTo(es2.getValue());
            }
        });
	
        linkMap.clear();
        
        // 5. iterating list and storing in LinkedHahsMap
        for(Map.Entry<String, Integer> map : listaEntradas){
            linkMap.put(map.getKey(), map.getValue());
        }
         	
        
        int tam = linkMap.entrySet().size();
        //System.out.println("tammmmm es : " + tam);
        int counter =0;
        double mediana = 0.0;
        if(tam%2 == 0) {
        	for(Map.Entry<String, Integer> lhmap : linkMap.entrySet()){
        		if(counter == tam/2) {
        			mediana = lhmap.getValue();
        			break;
        		} 
        		counter++;
             }
        }else {
        	int suelo = Math.floorDiv(tam, 2);
        	int techo = suelo+1;
        	for(Map.Entry<String, Integer> lhmap : linkMap.entrySet()){
        		if(counter == suelo || counter == techo) {
        			mediana += lhmap.getValue();
        			if(counter == techo)break;
        		}
        		counter++;
             }
        	mediana = (double)mediana/2;
        }
        
      
        
        return mediana;
        
        
        
        
        
        
	}	
	
	
	
	
	
}
