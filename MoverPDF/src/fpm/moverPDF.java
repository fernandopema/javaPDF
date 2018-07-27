package fpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class moverPDF {

	private static String pattern = "";
	private static String path = "";
	private static String operador = "";

	public static String getPattern() {
		return pattern;
	}

	public static void setPattern(String pattern) {
		moverPDF.pattern = pattern;
	}

	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		moverPDF.path = path;
	}

	public static void main(String[] args) {
		System.out.println("inicio");

		init(args);

		log("Inicio de la ejecuci�n");

		ArrayList<File> lista = listaPDF(getPath());
		ArrayList<String> listaExp = new ArrayList<String>();
		System.out.println("n�mero de ficheros: " + lista.size());
		Iterator<File> f = lista.iterator();
		int movidos = 0;
		String nameDir = "";
		while (f.hasNext()) {
			File file = f.next();
			try {
				System.out.println(getName(file.getName()) + "  -----   " + getExtension(file.getName()));
				// genera la carpeta cada 100 expedientes
				if ((movidos % 100) == 0){
					nameDir = getPath() + System.getProperty("file.separator") + getOperador() + (int)(movidos % 100);
				}
				//String nameDir = getPath() + System.getProperty("file.separator") + getDir(getExp(file.getName()));
				String nameExp = getExp(file.getName()).replace("_", "/");
				if (nameDir != null && nameDir.length() != 0) {
					File dir = new File(nameDir);
					//System.out.println("Carpeta : " + dir.getAbsolutePath());
					if (!dir.exists())
						dir.mkdir();
					String nameFile = nameDir + System.getProperty("file.separator") + "ps_" + getExp(file.getName())
							+ ".pdf";
					Path moved = Paths.get(nameFile);
					Path originalPath = Paths.get(file.getAbsolutePath());
					Files.move(originalPath, moved, StandardCopyOption.REPLACE_EXISTING);
					movidos++;
					listaExp.add(nameExp);
				}
				volcadoExpedientes(listaExp);

			} catch (Exception e) {
				try {
					log(e.toString());
					log("Error en fichero : " + file.getName());
				} catch (Exception e2) {
					System.out.println(e2.toString());
				}
			}
		}
		log("Ficheros tratados : " + lista.size() + " Ficheros movidos : " + movidos);
		log("Fin de la ejecuci�n.");
		System.out.println("fin");
	}

	private static void init(String[] args) {
		log("inicializa");
		Properties properties = new Properties();
		try {
			File file = new File(args[0]);
			FileInputStream fileInput = new FileInputStream(file);
			properties.load(fileInput);
			fileInput.close();
		} catch (Exception e) {
			log(e.toString());
		}

		if (properties.getProperty("path") != null) {
			setPath(properties.getProperty("path"));
			log("path: " + getPath());
			//System.out.println("path: " + getPath());
		}
		if (properties.getProperty("pattern") != null) {
			setPattern(properties.getProperty("pattern"));
			log("pattern: " + getPattern());
			//System.out.println("pattern: " + getPattern());
		}
		if (properties.getProperty("operador") != null) {
			setOperador(properties.getProperty("operador"));
			log("operador: " + getPattern());
			//System.out.println("pattern: " + getPattern());
		}		
	}


	/**
	 * deja un fichero con los n�meros de expediente tratados
	 * 
	 * @param listaExp
	 */
	private static void volcadoExpedientes(ArrayList<String> listaExp) {
		File exp = new File("listaExp.txt");
		try {
			// Si el archivo no existe, se crea!
			if (!exp.exists()) {
				exp.createNewFile();
			}
			// flag true, indica adjuntar informaci�n al archivo.
			FileWriter fw = new FileWriter(exp.getAbsoluteFile(), true);
			StringBuffer expString = new StringBuffer();
			Iterator<String> e = listaExp.iterator();
			while (e.hasNext()) {
				String expediente = e.next();
				expString.append(expediente);
				expString.append(System.lineSeparator());
			}
			fw.write(expString.toString());
			fw.close();
		} catch (Exception e) {
			//System.out.println(e.toString());
			log(e.toString());
		}
	}

	/**
	 * Devuelve los pdf de una ruta
	 * 
	 * @param path
	 * @return
	 */
	public static ArrayList<File> listaPDF(String path) {
		ArrayList<File> lista = new ArrayList<File>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				if (getExtension(file.getName()).equals("pdf")) {
					lista.add(file);
				}
			}
		}
		return lista;
	}

	private static String getExtension(String fileName) {
		String extension;
		extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		return extension;
	}

	private static String getName(String fileName) {
		String name;
		name = fileName.substring(0, fileName.lastIndexOf('.'));
		return name;
	}

	/**
	 * Los expedientes siempre siguen este patron: 2018pattern00006529
	 */
	private static String getExp(String fileName) {
		StringBuffer exp = new StringBuffer();
		int indexBabf = fileName.indexOf(getPattern().trim());
		int lenPat = getPattern().trim().length();
		exp.append(fileName.substring(indexBabf - lenPat, indexBabf + (lenPat + 8)));
		return exp.toString();
	}

	/**
	 * genera el nombre de una carpeta con el formato 2018_BABF_00006529
	 */
	private static String getDir(String expediente) {
		StringBuffer dir = new StringBuffer();
		dir.append(expediente.substring(0, 4));
		dir.append("_");
		dir.append(expediente.substring(4, 8));
		dir.append("_");
		dir.append(expediente.substring(8));
		return dir.toString();
	}

	/**
	 * deja un log en la ruta de ejecuci�n
	 * 
	 * @param logString
	 * @throws IOException
	 */
	private static void log(String writeLog) {
		File log = new File("moverPDF.log");
		try {
			// Si el archivo no existe, se crea!
			if (!log.exists()) {
				log.createNewFile();
			}
			// flag true, indica adjuntar informaci�n al archivo.
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
		} catch (Exception e) {
			System.out.println(writeLog);
			System.out.println(e.toString());
		}
	}

	public static String getOperador() {
		return operador;
	}

	public static void setOperador(String operador) {
		moverPDF.operador = operador;
	}

}