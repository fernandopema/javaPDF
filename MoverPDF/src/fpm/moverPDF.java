package fpm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

public class moverPDF {
	
	public static void main(String[] args) {
		System.out.println("inicio");
		log("Inicio de la ejecución");
		//String path = "C:\\Users\\fpenaman\\Documents\\$temp";
		String path = "";
		if (args[0] != null) {
			path = args[0];
		}
		ArrayList<File> lista = listaPDF(path);
		System.out.println("número de ficheros: " + lista.size());
		Iterator<File> f = lista.iterator();
		int movidos = 0;
		while (f.hasNext()){
			File file = f.next();
			try{
				System.out.println(getName(file.getName()) + "  -----   " + getExtension(file.getName()));
				//genera la carpeta
				String nameDir = path + System.getProperty("file.separator") + getDir(getExp(file.getName()));
				if (nameDir != null && nameDir.length() != 0) {
					File dir = new File(nameDir);
					System.out.println("Carpeta : " + dir.getAbsolutePath());
					dir.mkdir();
					String nameFile = nameDir + System.getProperty("file.separator") + "ps_" + getExp(file.getName()) + ".pdf";
				    Path moved = Paths.get(nameFile);
				    Path originalPath = Paths.get(file.getAbsolutePath());
				    //System.out.println("nameFile : " + nameFile);
				    //System.out.println("original : " + originalPath.toString());
				    //System.out.println("final : " + moved.toString());
				    Files.move(originalPath, moved, StandardCopyOption.REPLACE_EXISTING);
				    movidos ++;
				}

			}catch(Exception e){
				try{
					log(e.toString());
					log("Error en fichero : " + file.getName());
				}catch (Exception e2){
					System.out.println(e2.toString());
				}
			}
		}
		log("Fin de la ejecución.");
		log("Ficheros tratados : " + lista.size() + " Ficheros movidos : " + movidos);
		System.out.println("fin");
	}


	/**
	 * Devuelve los pdf de una ruta
	 * @param path
	 * @return
	 */
	public static ArrayList<File> listaPDF(String path) {
		ArrayList<File> lista = new ArrayList<File>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	if (getExtension(file.getName()).equals("pdf")){
		    		lista.add(file);
		    	}
		    }
		}
		return lista;
	}
	
	private static String getExtension(String fileName){
		String extension;
		extension = fileName.substring(fileName.lastIndexOf('.')+1);
		return extension;
	}
	
	private static String getName(String fileName){
		String name;
		name = fileName.substring(0,fileName.lastIndexOf('.'));
		return name;
	}
	
	/**
	 * Los expedientes siempre siguen este patron: 2018BABF00006529
	 */
	private static String getExp(String fileName){
		StringBuffer exp = new StringBuffer();
		int indexBabf = fileName.indexOf("BABF");
		exp.append(fileName.substring(indexBabf-4, indexBabf+12));
		return exp.toString();
	}
	
	/**
	 * genera el nombre de una carpeta con el formato 2018_BABF_00006529
	 */
	private static String getDir(String expediente){
		StringBuffer dir = new StringBuffer();
		dir.append(expediente.substring(0, 4));
		dir.append("_");
		dir.append(expediente.substring(4, 8));
		dir.append("_");
		dir.append(expediente.substring(8));
		return dir.toString();
	}
	
	/**
	 * deja un log en la ruta de ejecución
	 * @param logString
	 * @throws IOException 
	 */
	private static void log(String writeLog) {
		File log = new File ("moverPDF.log");
		try{
	        // Si el archivo no existe, se crea!
	        if (!log.exists()) {
	            log.createNewFile();
	        }
	        // flag true, indica adjuntar información al archivo.			
			FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
			StringBuffer logString = new StringBuffer();
			logString.append(LocalDateTime.now().getHour());
			logString.append(":");
			logString.append(LocalDateTime.now().getMinute());
			logString.append(":");
			logString.append(LocalDateTime.now().getSecond());
			logString.append(" * ");
			logString.append(writeLog);
			logString.append(System.lineSeparator());
			fw.write(logString.toString()); 
			fw.close();
		} catch (Exception e){
			System.out.println(e.toString());
		}
	}
		

}