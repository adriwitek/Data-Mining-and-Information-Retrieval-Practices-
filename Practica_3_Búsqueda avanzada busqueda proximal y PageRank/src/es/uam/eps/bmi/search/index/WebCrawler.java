package es.uam.eps.bmi.search.index;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.apache.lucene.util.packed.PackedLongValues.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.uam.eps.bmi.search.graph.Graph;

public class WebCrawler {

	
	
	
	
	public class Tupla implements Comparator<Tupla>,Comparable<Tupla>{ 
		  public  String path; 
		  public  Integer prioridad; 
		  
		  
		  public Tupla(String path, Integer prioridad) { 
		    this.path = path; 
		    this.prioridad = prioridad; 
		  }
		  
		  public int compare(Tupla x, Tupla y) {
		        if ((x.prioridad) < (y.prioridad)) {
		            return -1;
		        }else if (x.prioridad > y.prioridad) {
		            return 1;
		        }
		        return 0;
		  } 
		  
		  public int compareTo(Tupla t2) {
			  if(this.prioridad< t2.prioridad) {
				  return -1;
			  }else if(this.prioridad > t2.prioridad){
				  return 1;
			  }else {
				  return 0;}
		  }
		} 
	
	
	
	
	
	private static int TIMEOUTMILLIS = 10000;
	private int maxDocsToIndex;
	private int numDocsIndexed;
	private IndexBuilder indexBuilder;
	
	private Graph<String> grafo;
	private PriorityQueue<Tupla> colaP ;
	private String collectionPath;
	private String indexPath;
	private HashSet<String> urlsYaExploradas;
	private int nSemillas;
	
	public WebCrawler(IndexBuilder indexBuilder,int maxDocsToIndex, String collectionPath, String indexPath) throws IOException {
		
		this.indexBuilder = indexBuilder;
		this.maxDocsToIndex = maxDocsToIndex;
		numDocsIndexed = 0;
		this.grafo = new Graph<>();
		this.colaP = new PriorityQueue<Tupla>();
		this.collectionPath =collectionPath;
		this.indexPath = indexPath;
		
		this.indexBuilder.init(indexPath);
		this.urlsYaExploradas = new HashSet<String>();
		this.nSemillas =0;
		
	}
	
	
	
	//guardar enlaceas en la carptea del indice con la clase config TODOOOOO
	
	public void start() throws IOException {
		
		//Empezamos con las semillas
        File f = new File(collectionPath);
        Scanner in = new Scanner(f);
     
        
        //REcogemos las semillas
        while (in.hasNext()) {
        	if(nSemillas >= this.maxDocsToIndex) throw new IOException("El numero de semillas es mayor que el maximo de docuemtnos a indexar");
        	String url = in.nextLine();
        	if(url.isEmpty())continue;
        	nSemillas++;
    		Tupla tup = new Tupla( url , 2);//Prioriad 2 para las semillas 
    		this.colaP.add(tup);
        	        	
        } 
        
		
    
    	
		//TODOS LOS ENLACES EN LA COLA
        while(!colaP.isEmpty()) { //PARA CADA URL
        	
        	if(this.numDocsIndexed >= this.maxDocsToIndex) break;
        	
        	//Indexamos
        	String path = colaP.poll().path;
        	//Control rapido de duplicados
        	if(urlsYaExploradas.contains(path)) {
        		continue;
        	}else{
        		urlsYaExploradas.add(path);
        	}
        	try {
        		Document doc = Jsoup.parse(new URL(path), TIMEOUTMILLIS);
            	this.indexBuilder.indexHTML(doc.text(),path);
            	this.numDocsIndexed++;
            	
                this.grafo.addVertice(path);
              //Capturamos enlaces
            	capturaEnlaces( doc,  path);
        	}catch(Exception e) {
        	}
        	
          
        }
        
        
		//GUARDAMOS EL GRAFO
        String ruta = indexPath + Config.GRAPH_FILE ;
        Files.write(Paths.get(ruta ), this.toString().getBytes());
        System.out.println(ruta);
		this.indexBuilder.close(this.indexPath);
	}
	
	
	
	//Captura enlaces ya anniede conexion al grafo
	public void capturaEnlaces(Document doc, String url) {
		
		Elements links = doc.select("a");
    	Element link; 
    	Tupla tup;
    	int i =0;
    	
    	for(Element e: links) {
    		 link = links.get(i);
    		 i++;
    		 String completeUrl = link.absUrl("href");
    		//Control rapido de duplicados
         	if(urlsYaExploradas.contains(completeUrl)) {
         		continue;
         	}
    		 
    		 //Insertamos en cola
         	
         	if(completeUrl.isEmpty())continue;
    		tup = new Tupla( completeUrl , 1); 
    		this.colaP.add(tup);
    		 
    		 //anniadimos al grafo
    		 this.grafo.addVertice(link.absUrl("href"));
    		 this.grafo.addEdge(url, link.absUrl("href"));
    	}
    	
		
	}
	
	
	
	public int getNumberOfLinksIndexed() {
		return this.numDocsIndexed;
	}
	
	
	@Override
	public String toString() {
		return this.grafo.toString();
	}
	
	public String getUrlsExplored() {
		String res = "";
		java.util.Iterator<String> it =	urlsYaExploradas.iterator();	
		while(it.hasNext()){
			res = res + "\n" + it.next();
			
		}
		return res ;
	}
	
	
	public int getUrlsEnFrontera() {
		return colaP.size() ;
	}


	public int getNumeroSemillas() {
		return nSemillas;
	}

}
