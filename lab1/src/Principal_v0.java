/**
 * Lab0: Leitura de Base de Dados N�o-Distribuida
 * 
 * Autor: Lucio A. Rocha
 * Ultima atualizacao: 20/02/2023
 * 
 * Referencias: 
 * https://docs.oracle.com/javase/tutorial/essential/io
 * 
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Principal_v0 {

	public final static Path path = Paths.get("Sistemas_Distribuidos\\lab1\\src\\fortune-br.txt");

	public class FileReader {

		public int countFortunes() throws FileNotFoundException {

			int lineCount = 0;

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();

				}// fim while

				System.out.println(lineCount);
			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
			return lineCount;
		}

		public void parser(HashMap<Integer, String> hm)
				throws FileNotFoundException {

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				int lineCount = 0;

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();
					StringBuffer fortune = new StringBuffer();
					while (!(line == null) && !line.equals("%")) {
						fortune.append(line + "\n");
						line = br.readLine();
						// System.out.print(lineCount + ".");
					}

					hm.put(lineCount, fortune.toString());
					System.out.println(fortune.toString());

				}// fim while

			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
		}

        public void read(HashMap<Integer, String> hm) throws FileNotFoundException {
            int totalFortunes = hm.size();

			//Sorteando a fortuna que será lida
            SecureRandom random = new SecureRandom();
            int randomIndex = random.nextInt(totalFortunes);
			
			InputStream is = new BufferedInputStream(new FileInputStream(path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
				
				int lineCount = 0;
				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;
					
					line = br.readLine();
					if(lineCount == randomIndex){
						StringBuffer fortune = new StringBuffer();
						while (!(line == null) && !line.equals("%")) {
							fortune.append(line + "\n");
							line = br.readLine();
						}

						System.out.println("\nFortuna Sorteada: "+randomIndex);
						System.out.println(fortune.toString());
						break;
					}
				}// fim while			
			
			}catch (IOException e) {
                System.err.println("ERRO NA LEITURA.");
            }

		}
    

		public void write(HashMap<Integer, String> hm) throws FileNotFoundException {

			String minhaFortuna = "Quem é rico faz o PIX!\n \t--Edinaldo Pereira";
			String marcador = "\n%\n";
			OutputStream is = new BufferedOutputStream(new FileOutputStream(path.toString(),true));
			try(BufferedWriter br = new BufferedWriter(new OutputStreamWriter(is))){

				br.write(marcador,0,marcador.length());
				br.write(minhaFortuna,0,minhaFortuna.length());

			} catch(IOException e){
				System.err.println("ERRO NA ESCRITA.");
			}
		}
	}

	public void iniciar() {

		FileReader fr = new FileReader();
		try {
			fr.countFortunes();
			HashMap<Integer, String> hm = new HashMap<Integer, String>();
			fr.parser(hm);
			fr.read(hm);
			fr.write(hm);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new Principal_v0().iniciar();
	}

}
