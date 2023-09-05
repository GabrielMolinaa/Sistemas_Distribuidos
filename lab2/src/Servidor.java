import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class Servidor {

	private static Socket socket;
	private static ServerSocket server;

	private static DataInputStream entrada;
	private static DataOutputStream saida;
	public final static Path path = Paths.get("Sistemas_Distribuidos\\lab2\\src\\fortune-br.txt");

	private int porta = 1025;

	public void iniciar() {
		System.out.println("Servidor iniciado na porta: " + porta);
		try {

			// Criar porta de recepcao
			server = new ServerSocket(porta);
			socket = server.accept();  //Processo fica bloqueado, ah espera de conexoes

			// Criar os fluxos de entrada e saida
			entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());
			String resultado = parser(entrada.readUTF());	
		
			// Envio dos dados (resultado)
			saida.writeUTF(resultado);
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Processando a mensagem recebida
	public String parser(String jsonString) {
        String method = "";
        String args = "";
        String resultado = "";

        // Expressão regular para encontrar o valor da chave "method"
        String methodPattern = "\"method\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern methodRegex = java.util.regex.Pattern.compile(methodPattern);
        java.util.regex.Matcher methodMatcher = methodRegex.matcher(jsonString);
        if (methodMatcher.find()) {
            method = methodMatcher.group(1);
        } else {
            resultado = "{\"result\":\"false\"}\n";
        }

        // Expressão regular para encontrar o valor da chave "args"
        String argsPattern = "\"args\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern argsRegex = java.util.regex.Pattern.compile(argsPattern);
        java.util.regex.Matcher argsMatcher = argsRegex.matcher(jsonString);
        if (argsMatcher.find()) {
            args = argsMatcher.group(1);
        } else {
            resultado = "{\"result\":\"false\"}\n";
        }

        if ("read".equals(method)) {
            resultado = read();
        } else if ("write".equals(method)) {
            resultado = write(args);
        } else {
            resultado = "{\"result\":\"false\"}\n";
        }

        return resultado;
	}//fim parser

	//Lê uma fortuna aleatória
	 public String read() {
        SecureRandom random = new SecureRandom();
        int randomIndex = random.nextInt(2000);
        String resultado = "";

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            int lineCount = 0;
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.equals("%")) {
                    lineCount++;
                }
                if (lineCount == randomIndex) {
                    StringBuilder fortune = new StringBuilder();
                    while ((line = br.readLine()) != null && !line.equals("%")) {
                        fortune.append(line).append("\n");
                    }

                    System.out.println("\nFortuna Sorteada: " + randomIndex);
                    resultado = "{\"result\":\"" + fortune.toString() + "\"}\n";
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("ERRO NA LEITURA.");
        }

        return resultado;
    }//fim read

	//Escreve fortuna no arquivo
    public String write(String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            String marcador = "\n%\n";
            bw.write(marcador);
            bw.write(message);
            System.out.println("\nFortuna Gravada: " + message);
            return "{\"result\":\"" + message + "\"}\n";
        } catch (IOException e) {
            System.err.println("ERRO NA ESCRITA.");
            return "{\"result\":\"false\"}\n";
        }
    }//fim write

	public static void main(String[] args) {
		new Servidor().iniciar();

	}

}
