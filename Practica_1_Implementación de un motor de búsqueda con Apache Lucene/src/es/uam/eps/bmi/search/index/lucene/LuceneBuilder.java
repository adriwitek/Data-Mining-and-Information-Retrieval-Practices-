package es.uam.eps.bmi.search.index.lucene;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

import java.io.*; 

import es.uam.eps.bmi.search.index.IndexBuilder;

public class LuceneBuilder implements IndexBuilder {

	private String indexPath;
	
	@Override
	public void build(String collectionPath, String indexPath) throws IOException {
		
		//Cofigurable
		boolean rebuild = true;
		boolean standarAnalyzer = true;
		
		
		this.indexPath = indexPath;
		Analyzer analyzer;
		
		// Inicio creacion del indice
		Directory directory = FSDirectory.open(Paths.get(indexPath));   // Directorio del indice
		
		if(standarAnalyzer) {
			 analyzer = new StandardAnalyzer();
		}else{

			Set<String> h = new HashSet<>(Arrays.asList(
				    "a", "an", "and", "are", "as", "at", "be", "but", "by",
				    "for", "if", "in", "into", "is", "it",
				    "no", "not", "of", "on", "or", "such",
				    "that", "the", "their", "then", "there", "these",
				    "they", "this", "to", "was", "will", "with"
				)  );
			CharArraySet charArraySetVar = new  CharArraySet( h, true);
			analyzer= new StandardAnalyzer( charArraySetVar);
		}
       
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
      
        // Configuracion de creacion de indice
        if (rebuild == true) {
        	config.setOpenMode(OpenMode.CREATE);
        	
        } else {
        	config.setOpenMode(OpenMode.CREATE_OR_APPEND);
        }


        //IndexWriter
        IndexWriter builder = new IndexWriter(directory, config);
        
        // 1. Anadir documentos al Indice
        for(String path : listFilesForFolder(collectionPath)) {

            Document doc = new Document();
            
            //doc.add(new TextField("path", path, Field.Store.YES));
            
            FieldType type = new FieldType();
            type.setIndexOptions (IndexOptions.DOCS_AND_FREQS);
            type.setStoreTermVectors(true);
            
            // Abrimos y leemos el fichero.
            String text = "";
            
            File myObj = new File(path);
			Scanner myReader = new Scanner(myObj);
            
			
			if(collectionPath.endsWith(".txt")){//URLs en .txt
				while (myReader.hasNextLine()) {
					
					String url = myReader.nextLine();
					try {
						doc = new Document();
						doc.add(new TextField("path", url, Field.Store.YES));
						url = Jsoup.parse(new URL(url), 10000).text();
						doc.add(new Field("content", url, type));
						 builder.addDocument(doc);
					//}catch(IOException | MalformedURLException |UnknownHostException|  UncheckedIOException   |HttpStatusException  e){
					}catch(IOException | UncheckedIOException  e){
						continue;
					}
				
				}

			}else {//LOCAL
				doc.add(new TextField("path", path, Field.Store.YES));
				while (myReader.hasNextLine()) {
					String data = myReader.nextLine();
					text +=  "\n" + data;
				}
				doc.add(new Field("content", Jsoup.parse(text).text(), type));
				 builder.addDocument(doc);
			}
			
			
	
			myReader.close();
           
        }
        
        builder.close();
        this.getDocumentVectorModule();
	}

	

	private List<String> listFilesForFolder(String path) {
		
		List<String> listaDePaths= new ArrayList<String>();
		
		// Comprobar si esta comprimido en ZIP y si lo es descomprimirlo y pasar los path donde se ha descomprimido.
		path = zipFile(path);
		
		File folder = new File(path);
		
		if (folder.isDirectory()) {
			
			for (final File fileEntry : folder.listFiles()) {
				
		        if (fileEntry.isDirectory()) {
		        	
		        	listaDePaths.addAll( listFilesForFolder(path + "/" + fileEntry.getName()) );
		            
		        } else {
		        	
		        	listaDePaths.add( path + "/" + fileEntry.getName() );
		        }
		        	
		    }

        } else  listaDePaths.add( path );
		  
		return listaDePaths;
	}
	
	
	private String zipFile(String path) {
		
		String[] tipo = path.split("\\.");
		
		// Comprueba si es un zip.		
		if (tipo[tipo.length - 1].equals("zip")) {

			String destDir = path.split(".zip")[0];

			// En caso de ser zip lo descomprime
			File dir = new File(destDir);
			
	        if(!dir.exists()) dir.mkdirs();
	        
	        byte[] buffer = new byte[1024];
	        FileInputStream fis;
			
	        try {
	            fis = new FileInputStream(path);
	            ZipInputStream zis = new ZipInputStream(fis);
	            ZipEntry ze = zis.getNextEntry();
	            while(ze != null){
	                String fileName = ze.getName();
	                File newFile = new File(destDir + File.separator + fileName);

	                //create directories for sub directories in zip
	                new File(newFile.getParent()).mkdirs();
	                FileOutputStream fos = new FileOutputStream(newFile);
	                int len;
	                while ((len = zis.read(buffer)) > 0) {
	                fos.write(buffer, 0, len);
	                }
	                fos.close();
	                //close this ZipEntry
	                zis.closeEntry();
	                ze = zis.getNextEntry();
	            }
	            //close last ZipEntry
	            zis.closeEntry();
	            zis.close();
	            fis.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        
			// Y actualiza la variable path
			path = destDir;
		}

		return path;
	}
	
	
	
	
	public HashMap<Integer, Double> getDocumentVectorModule() {

        HashMap<Integer, Double> hmap = new HashMap<Integer, Double>();

		try {
			
			LuceneIndex index = new LuceneIndex(this.indexPath);
	        IndexReader indexReader = index.getLuceneIndexReader();

			ArrayList<String> listaTerminos = index.getAllTerms();
			
			// Para cada documento
			for(int docNumber = 0; docNumber < indexReader.numDocs(); docNumber++) {
				
				double modulo = 0;
				double valor = 0;
				
				// Calculamos el modulo
				for(String termino : listaTerminos) {
	    			 
	    			 Term terminoDeLista= new Term("content", termino);
	    			 
	    			 valor = indexReader.docFreq(terminoDeLista);
	    			 
	    			 modulo += valor * valor;
	    		 }
				
				modulo = Math.sqrt(modulo);
    			
	    		// Guardamos finalmente el modulo del vector
	    		hmap.put(docNumber, modulo);
			}
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}

        //Guardamos el hashmap serializandolo
        try{
               FileOutputStream fos = new FileOutputStream("docsVectorModules.ser");
               ObjectOutputStream oos = new ObjectOutputStream(fos);
               oos.writeObject(hmap);
               oos.close();
               fos.close(); 
               return hmap;
        }catch(IOException ioe){
               ioe.printStackTrace();
         }
        
        return null;
	}
	
	
}
