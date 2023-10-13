/**
 * Laboratorio 3  
 * Autor: Lucio Agostinho Rocha
 * Ultima atualizacao: 04/04/2023
 */
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class ServidorImpl implements IMensagem{
    


    public ServidorImpl() {
                
    }
    
    //Cliente: invoca o metodo remoto 'enviar'
    //Servidor: invoca o metodo local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
        	System.out.println("Mensagem recebida: " + mensagem.getMensagem());
			resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
		} catch (Exception e) {
			e.printStackTrace();
            
			resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
		}
        return resposta;
    }    
    
    public String parserJSON(String json) {
		String mensagem = json;
		String resultado = "";
        String respostaFinal  = "";
        Principal principal = new Principal();
		if(mensagem.contains("read")){
            resultado = principal.read();
            respostaFinal = "{\n" + "\"result:\"" + "\"" + resultado + "\"" + "}";
            
        }else if(mensagem.contains("write")){
            int argsStartIndex = mensagem.indexOf("\"args\": [") + 9;
            int argsEndIndex = mensagem.indexOf("]", argsStartIndex);
            if (argsStartIndex != -1 && argsEndIndex != -1) {
                String argsString = mensagem.substring(argsStartIndex, argsEndIndex);
                argsString = argsString.replaceAll("\"", "");
                principal.write(argsString);
                respostaFinal = "{\n" + "\"result:\"" + "\"" +argsString+ "\"" + "\n}";
                
        }

        }else{
            respostaFinal = "MÉTODO NÃO ENCONTRADO!";
        }

		return respostaFinal;
	}
    public void iniciar(){

    try {
            Registry servidorRegistro = LocateRegistry.createRegistry(1099);            
            IMensagem skeleton  = (IMensagem) UnicastRemoteObject.exportObject(this, 0); //0: sistema operacional indica a porta (porta anonima)
            servidorRegistro.rebind("servidorFortunes", skeleton);
            System.out.print("Servidor RMI: Aguardando conexoes...");
                        
        } catch(Exception e) {
            e.printStackTrace();
        }        

    }
    
    public static void main(String[] args) {
        ServidorImpl servidor = new ServidorImpl();
        servidor.iniciar();
    }    
}
