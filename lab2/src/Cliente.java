
/**
 * Laboratorio 1 de Sistemas Distribuidos
 * 
 * Autor: Lucio A. Rocha
 * Ultima atualizacao: 17/12/2022
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    
    private static Socket socket;
    private static DataInputStream entrada;
    private static DataOutputStream saida;
    
    private int porta=1025;
    
    public void iniciar(){
    	System.out.println("Cliente iniciado na porta: "+porta);
    	
    	try {
            
            socket = new Socket("127.0.0.1", porta);
            
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            
            //Recebe do usuario algum valor
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Digite [1] para leitura\nDigite [2] para escrita\n");
            int valor = Integer.parseInt(br.readLine());
            String jsonString = "";

            //O valor eh enviado ao servidor
            if(valor == 1){
                jsonString = "{\"method\": \"read\", \"args\": \"[\"\"]\"}";

            }else if(valor == 2){
                System.out.println("Digite a mensagem a ser gravada:");
                String mensagem = br.readLine();
                jsonString = "{\"method\": \"write\", \"args\":"+ "\"" + mensagem +"\"}";

            }else {
                System.out.println("OPCAO INVALIDA!");
            }

            //Envia a mensagem
            saida.writeUTF(jsonString);

            //Recebe-se o resultado do servidor
            System.out.println("Recebido do servidor:");
            String resultado = entrada.readUTF();
            
            //Mostra o resultado na tela
            System.out.println(resultado);
            socket.close();
            
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
    
}
