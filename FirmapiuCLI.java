/**
 * 
 */
package firmapiucli;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bouncycastle.cms.CMSException;

import firmapiu.CommandProxyInterface;


/**
 * Questa classe realizza la Command Line Interface dell'applicazione FirmaPiù. 
 * Si interfaccia a CommandProxyInterface per eseguire effettivamente i comandi richiesti
 * 
 * @author dellanna
 *
 */
public class FirmapiuCLI {

	private static final int CMDSIGN=0;
	private static final int CMDVERIFY=1;
	
	//TODO ricorda se si mettono nuove opzioni in quali punto del codice devono essere riutilizzate
	private static final String OPTPIN="-pin";
	private static final String OPTALIAS="-alias";
	private static final String OPTOUTDIR_EXTENDED="--outdir";
	private static final String OPTOUTDIR_SHORT="-o";
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*Set<String> arg= new TreeSet<String>();
		arg.add(new String("/home/andy/libersoftspace/firmapiu/pippoas.txt"));
		arg.add(new String("/home/andy/libersoftspace/firmapiu/puppolo2.txt"));

		
		Map<String,Object> options= new TreeMap<String,Object>();
		options.put(PIN, "87654321".toCharArray());
		options.put(ALIAS, "CNS User Certificate");
		
		Set<?> result= (Set<?>)sign.invokeCommand(arg, options);
		
		System.out.println("Percorsi dei file p7m generati:");
		Iterator<?> it=result.iterator();
		while(it.hasNext()){
			System.out.println("\t"+(String)it.next());
		}*/
		
		/*Set<String> arg= new TreeSet<String>();
		arg.add(new String("/home/andy/libersoftspace/firmapiu/pippo.txt.p7m"));
		arg.add(new String("/home/andy/libersoftspace/firmapiu/pippo2.txt.p7m"));
		
		Map<?,?> mapres= (Map<?,?>)verify.invokeCommand(arg, null);
		Set<?> keyset=mapres.keySet();
		Iterator<?> itr= keyset.iterator();
		System.out.println("Verifica dei file:");
		while(itr.hasNext()){
			String key=(String)itr.next();
			System.out.println("\t"+key+" -> "+mapres.get(key));
		}*/
		
		/*for(int k=0;k<args.length;k++){
			System.out.println(args[k]+" "+args[k].length());
		}*/
		ResourceBundle rb = ResourceBundle.getBundle("firmapiu.lang.locale",Locale.getDefault());
		
		//Inizializza la Console
		Console console=System.console();
		//Inizializza il CommandProxyInterface per interfacciarsi ai comandi
		CommandProxyInterface cmdPInterface =new CommandProxyInterface(rb);
		
		
		//parsa gli argomenti del main per trovare il comando richiesto e inizializza gli argomenti da passare al comando
		Set<String> commandArgs= new TreeSet<String>();
		int command = parseCommand(args,commandArgs);
		//parsa gli argomenti del main per ritrovare le opzioni
		//Map<String,Object> options=new TreeMap<String,Object>();
		Map<String,Object> options=parseOptions(args);
		//options.put(PIN, "87654321".toCharArray());
		//options.put(ALIAS, "CNS User Certificate");
		
		//esegue il comando richiesto
		switch(command){
			case CMDSIGN:{
				//se le opzioni alias e pin non sono state definite le recupera da console
				//Specifica alias utente
				/*if (!options.containsKey(CommandProxyInterface.ALIAS)) {
					String aliasStr=null;
					while (aliasStr==null || aliasStr.equals("")) {
						System.out.println("Specificare Alias Utente: ");
						System.out.print("\t");
						aliasStr=console.readLine();
					}
					options.put(CommandProxyInterface.ALIAS, aliasStr);
				}*/
				//chiede conferma per eseguire comando
				//System.out.println();
				//System.out.println("Alias Utente: "+(String)options.get(CommandProxyInterface.ALIAS));
				System.out.println();
				System.out.println("Percorso dei file da firmare:");
				Iterator<String> commandItr=commandArgs.iterator();
				while(commandItr.hasNext()){
					File file=fileFromPath(commandItr.next());
					System.out.println("\t"+file.getCanonicalPath());
				}
				//se è contenuta l'opzione OUTDIR stampa a video il percorso nel quale i file saranno salvati
				if (options.containsKey(CommandProxyInterface.OUTDIR)){
					System.out.println();
					System.out.println("Percorso della directory nella quale i file generati saranno salvati:");
					System.out.println("\t"+fileFromPath((String)options.get(CommandProxyInterface.OUTDIR)).getCanonicalPath());
				}
				confirmOperation(console);
				
				//Specificare pin carta
				//if (!options.containsKey(CommandProxyInterface.PIN)) {
					char[] pin=null;
					while (pin==null || pin.length==0){
						System.out.println("Specificare Pin Carta: ");
						System.out.print("\t");
						pin=console.readPassword();
					}
					options.put(CommandProxyInterface.PIN, pin);
				//}
				
				//firma i file e resetta il pin
				Map<String,?> result=cmdPInterface.sign(commandArgs, options);	
				java.util.Arrays.fill(pin, ' ');
				
				//stampa a video il percorso dei file firmati
				System.out.println();
				System.out.println("Esito dell'operazione richiesta:");
				System.out.println();
				Iterator<String> itr=result.keySet().iterator();
				while(itr.hasNext()){
					//System.out.println("\t"+(String)itr.next());
					String key=itr.next();
					System.out.println(key+" :");
					Object value=result.get(key);
					if(value instanceof String)
						System.out.println("\tOK! P7M salvato in -> "+(String)value);
					else if(value instanceof FileNotFoundException){
						System.err.println("\tErrore: File non trovato!");
						System.err.println("\t"+((FileNotFoundException)value).getMessage());
					}else if(value instanceof IOException){
						System.err.println("\tErrore: Errore di I/O!");
						System.err.println("\t"+((IOException)value).getMessage());
					}
				}//fine while
			}break;
			case CMDVERIFY:{
				//chiede conferma dei file da verificare
				System.out.println();
				System.out.println("Percorso dei file da verificare:");
				Iterator<String> commandItr=commandArgs.iterator();
				while(commandItr.hasNext()){
						File file=fileFromPath(commandItr.next());
						System.out.println("\t"+file.getCanonicalPath());
					}
				confirmOperation(console);
				
				//verifica i file
				Map<String,?> result= cmdPInterface.verify(commandArgs, null);
				
				//stampa a video l'esito della verifica dei file passati come paramentro
				System.out.println();
				System.out.println("Esito dell'operazione richiesta:");
				System.out.println();
				Iterator<String> itr= result.keySet().iterator();
				while(itr.hasNext()){
					String key=itr.next();
					System.out.println(key+" :");
					Object value=result.get(key);
					if(value instanceof Boolean){
						if(((Boolean)value).booleanValue())
							System.out.println("\tTRUE! Il file è stato firmato correttamente dal firmatario!");
						else
							System.out.println("\tFALSE! Il file e il valore della firma del firmatario non corrispondono! ");
					}else if(value instanceof FileNotFoundException){
						System.err.println("\tErrore: File non trovato!");
						System.err.println("\t"+((FileNotFoundException)value).getMessage());
					}else if(value instanceof IOException){
						System.err.println("\tErrore: Errore di I/O!");
						System.err.println("\t"+((IOException)value).getMessage());
					}else if(value instanceof CMSException){
						System.err.println("\tErrore: Errore in fase di validazione!");
						System.err.println("\t"+((CMSException)value).getMessage());
					}
				}//fine while
			}break;
			default: {
				System.err.println("Attenzione: Comando non valido!");
				System.exit(-1);
			}
		}
		
		

		
		//mostra a video il risultato
		
		//termina esecuzione
		
		
	}//fine main
	
	//Procedure private
	//procedura interattiva di conferma operazione
	private static void confirmOperation(Console console){
		String confirm=null;
		while(confirm==null || !(confirm.equals("s") || confirm.equals("n"))){
			System.out.println("Confermare operazione? [s/n]");
			confirm=console.readLine();
		}
		if(confirm.equals("n"))
		{
			System.out.println();
			System.out.println("Attenzione: Operazione Abortita!");
			System.exit(0);
		}
	}

	//parsa gli argomenti del main per trovare il comando desiderato 
	private static int parseCommand(String [] args,Set<String> commandArgs){
		if(args.length==0)
		{
			System.err.println("Attenzione: Comando non trovato!");
			System.exit(-1);
		}
		if(args[0].equals("--sign") || args[0].equals("-s"))
			{
				parseCommandArgs(args,commandArgs);
				return CMDSIGN;
			}
		if(args[0].equals("--verify") || args[0].equals("-v"))
			{
				parseCommandArgs(args,commandArgs);
				return CMDVERIFY;
			}
		return -1;
	}
	
	//Inizializza gli argomenti effettivi da passare al comando
	private static void parseCommandArgs(String [] args,Set<String> commandArgs){
		if(args.length<2)
		{
			System.err.println("Attenzione: file non trovato!");
			System.exit(-1);
		}
		for(int i=1;i<args.length;i++){
			if(optMatch(args[i])) break;
			commandArgs.add(args[i]);
		}
		if(commandArgs.isEmpty()){
			System.err.println("Attenzione: file non trovato!");
			System.exit(-1);
		}
	}
	
	//option matcher
	private static boolean optMatch(String opt){
		//TODO le opzioni vengono riutilizzate qui
		if(opt.equals(OPTOUTDIR_EXTENDED) || opt.equals(OPTOUTDIR_SHORT))
			return true;
		/*if(opt.equals(OPTPIN))
			return true;
		if(opt.equals(OPTALIAS))
			return true;*/
		return false;
	}
	
	//parsa gli argomenti del main per trovare le opzioni desiderate
	private static Map<String,Object> parseOptions(String[] args){
		//TODO le opzioni vengono riutilizzate qui.
		Map<String,Object> options= new TreeMap<String,Object>();
		for(int i=0;i<args.length;i++){
			/*if(args[i].equals(OPTPIN)){
				if(i+1>=args.length || !(args[i+1].matches("[a-zA-Z0-9]+")))
					{
						System.err.println("Attenzione: "+OPTPIN+" non ha un'opzione valida!");
						System.exit(-1);
					}
				options.put(CommandProxyInterface.PIN, args[i+1].toCharArray());
				i++;
			}
			if(args[i].equals(OPTALIAS)){
				if(i+1>=args.length)
				{
					System.err.println("Attenzione: "+OPTALIAS+" non ha un'opzione valida!");
					System.exit(-1);
				}
				options.put(CommandProxyInterface.ALIAS, args[i+1]);
				i++;
			}*/
			if(args[i].equals(OPTOUTDIR_EXTENDED) || args[i].equals(OPTOUTDIR_SHORT)){
				if(i+1>=args.length)
				{
					System.err.println("Attenzione: "+args[i]+" non ha un'opzione valida!");
					System.exit(-1);
				}
				options.put(CommandProxyInterface.OUTDIR, args[i+1]);
				i++;
			}
		}//fine for
		
		return options;
	}//fine parseOptions
	
	//procedura privata per trovare il percorso canonico di un file da un path generico
		private static File fileFromPath(String filepath) throws IOException{
			if(filepath.startsWith("~"))
			{
				String[] user=filepath.split("/",2);
			    try {
			        String command = "ls -d " + user[0];
			        Process shellExec = Runtime.getRuntime().exec(
			            new String[]{"bash", "-c", command});

			        BufferedReader reader = new BufferedReader(
			            new InputStreamReader(shellExec.getInputStream()));
			        String expandedPath = reader.readLine();

			        // Only return a new value if expansion worked.
			        // We're reading from stdin. If there was a problem, it was written
			        // to stderr and our result will be null.
			        if (expandedPath != null) {
			            filepath = expandedPath+"/"+user[1];
			        }
			    } catch (java.io.IOException ex) {
			        // Just consider it unexpandable and return original path.
			    }
			}
			return new File(filepath).getCanonicalFile();
		}//fine file from path
}