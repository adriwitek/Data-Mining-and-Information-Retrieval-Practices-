package es.uam.eps.bmi.search.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import es.uam.eps.bmi.search.SearchEngine;
import es.uam.eps.bmi.search.index.IndexBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.lucene.LuceneEngine;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import es.uam.eps.bmi.search.vsm.VSMDotProductEngine;
import es.uam.eps.bmi.search.vsm.VSMCosineEngine;

public class UserInterface {

	
	public static void main (String a[]) throws IOException {
		
		// Creando el Marco        
        JFrame frame = new JFrame("Busqueda e Indexacion");       
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       
        frame.setSize(600, 500);
 
        // Creando el panel de busqueda.
        JPanel panel = new JPanel();
        JPanel panelIndex = new JPanel();
        
        // Creando panel donde se muestran los resultados.
        JPanel mainPanel = new JPanel();
        JPanel results = new JPanel();
        results.setLayout(new BoxLayout(results, BoxLayout.PAGE_AXIS));
        
        // Creamos un panel deslizante
        JScrollPane pane = new JScrollPane(results);
        //pane.setPreferredSize(new Dimension(350, 250));
        mainPanel.add(pane);
            
        // Creamos el texto para la busqueda
        JLabel label_tf = new JLabel("Palabras de la búsqueda");
        JTextField tf = new JTextField(10);
        JLabel label_num = new JLabel("Top");
        JTextField num = new JTextField(4);
        JButton send = new JButton("Buscar");
        JComboBox<String> combo = new JComboBox<>();
		combo.addItem("Lucene");
		combo.addItem("Producto escalar");
		combo.addItem("Coseno");
        
        
        /*
         * DEFECTO
         */
        String collectionPath = "src/es/uam/eps/bmi/search/ranking";
        String indexPath = "index/src";
        
        IndexBuilder builder = new LuceneBuilder();
        builder.build(collectionPath, indexPath);
        /*
         * DEFECTO
         */
        
        
        
        
        send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				results.removeAll();
				
				String query = tf.getText();
				
				
				SearchEngine engine;
				try {

					if (combo.getSelectedItem().equals("Lucene"))                   engine = new LuceneEngine(indexPath);
					else if (combo.getSelectedItem().equals("Producto escalar"))    engine = new VSMDotProductEngine(new LuceneIndex(indexPath));
					else if (combo.getSelectedItem().equals("Coseno"))               engine = new VSMCosineEngine(new LuceneIndex(indexPath));
					else throw new Exception("Error");			
					
					int cutoff = Integer.parseInt(num.getText());
					
					
					SearchRanking ranking = engine.search(query, cutoff);
					
					String pathAbs = new File(UserInterface.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
					
					String aux = pathAbs.substring(0, pathAbs.length() - 4 );
					
					for (SearchRankingDoc result : ranking) {
						
						result.getPath();
						result.getDocID();
						result.getScore();
						
						JPanel documento = new JPanel();
						
						JLabel id = new JLabel(String.valueOf(result.getDocID()));
						JLabel name = new JLabel(result.getPath());
						JLabel score = new JLabel(String.valueOf(result.getScore()));
						JButton open = new JButton("Abrir documento");
						
						open.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									
									String fileName = result.getPath();
									
									fileName = fileName.replace("/", "\\");
									
									Runtime.getRuntime().exec("explorer.exe /select," + aux + "\\" + fileName);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						});
						
						documento.add(id);
						documento.add(name);
						documento.add(open);
						documento.add(score);
						
						results.add(documento);
						
					}
					
					
					
					
				} catch (Exception e) {

					JLabel name = new JLabel("ERROR");
					
					results.add(name);
					
				}
				
				mainPanel.revalidate();
				mainPanel.repaint();
			}
        });
        
        JLabel label_idx = new JLabel("Archivos a indexar");
        JTextField pathIndex = new JTextField(10);
        JLabel label_dest = new JLabel("Path del indice");
        JTextField pathDest = new JTextField(10);
        JButton indexar = new JButton("Indexar");
        
        indexar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					builder.build(pathIndex.getText(), pathDest.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
        });

        panel.add(label_tf);
        panel.add(tf);
        panel.add(label_num);
        panel.add(num);
        panel.add(combo);
        panel.add(send);
        
        panelIndex.add(label_idx);
        panelIndex.add(pathIndex);
        panelIndex.add(label_dest);
        panelIndex.add(pathDest);
        panelIndex.add(indexar);
 
        // Agregar componentes al marco.      
        frame.getContentPane().add(BorderLayout.NORTH, panelIndex);
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setVisible(true);
	}
}
