package bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import sun.net.www.URLConnection;
import xmlData.Fertiliser;
import xmlData.Ingredient;
import xmlData.ObjectFactory;
import xmlData.PresentFertiliser;
import xmlData.PresentIngredient;
import xmlData.Production;
import xmlData.RequiredFertiliser;
import xmlData.RequiredIngredient;
import xmlData.Solution;
import xmlData.Units;
import xmlData.UsedFertiliser;
import xmlData.UsedIngredient;
import data.Model;
import jCMPL.Cmpl;
import jCMPL.CmplException;
import jCMPL.CmplParameter;
import jCMPL.CmplSet;
import jCMPL.CmplSolArray;
import jCMPL.CmplSolElement;
import jCMPL.CmplSolution;

/**
 * Diese EDbean Klasse organisiert die Sachlogik der Webanwendung
 * Bei allen Datenzugriffen wird auf die Klasse data.Model zugegriffen
 * Fuer die Problemloesung wird auf die Klasse bean.Solver zugegriffen. 
 * 
 * @autor: Eddi M.
 */
public class EDbean implements Serializable{
	
	/**
	 * Absoluter Verzeichnisname der Configurationsdatei.
	 * 
	 */
	public static final String CONFIG = "/Users/Max/Documents/workspace/Fertiliser_0.2/Config/Config.txt";

	/**
	 * Url des verwendeten WebService
	 * 
	 * 
	 */
	public static String WebService;
	
	/**
	 * Absoluter Verzeichnisname des Verzeichnisses in dem die ModellDateien gespeichert werden
	 */
	public static String ModelDir;
	
	public static String CmplModel;

	/**
	 * Als Produktionssystem wird der Nutzername von
	 * der WebApplicationEngine abgefragt, �berpr�ft und der 
	 * Anwendung �bergeben.
	 */
	public static final boolean ProduktionSyst	= false;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * absoluter Pfad zum ParameterFile, wird in init() gesetzt 
	 * und vom Solver gebraucht.
	 */
	public static String parameterFile = "";
	
	static Logger logger = Logger.getLogger(EDbean.class);
	
	/**
	 * Name des aktuellen Nutzers
	 * 
	 */
	private String 	nutzer;

	/**
	 * Das aktuelle Modell
	 * 
	 */
	private Model	model;

	/**
	 * Ueber dieses Objekt koennen die Inhalte in den XML Dateien gespeichert werden.
	 * 
	 */
	private Fertiliser	modelData;

	/**
	 * Dieses Attribut regelt die Sichtbarkeit der ID Spalte
	 * 
	 * @autor: Eddi M.
	 */
	private boolean showID = true;
	
	/**
	 * Ist der Verzeichnispfad der Datei
	 * @autor: Jonas G.
	 */
	private String filepath	= ModelDir+"user.usr";
	
	/**
	 * Beschreibt das Objekt zum Auslesen der Nutzerverwaltung.
	 * 
	 * @autor: Jonas G.
	 */
	private File f 			= new File(filepath);
	
	/**
	 * Das Verweis zum aktuellen Model.
	 * 
	 * @autor: Jonas G.
	 */
	private int linefile	= 0;
	
	/**
	 * Speichert die eingelesenen Lines.
	 * @autor: Jonas G.
	 */
	private String[] LineArray;
	
	/**
	 * 
	 * @autor: Jonas G.
	 */
	private boolean rightcheck = false;

	/**
	 * Ueber AddIngredient wird die Sichtbarkeit der Zeile zum hinzufuegen von neuen Zutaten geregelt.
	 * 
	 * @autor: Eddi M.
	 */
	private boolean AddIngredient;
	
	/**
	 * Ueber AddPresentFertiliser wird die Sichtbarkeit der Zeile zum hinzufuegen von neuen PresentFertiliser geregelt.
	 * 
	 * @autor: Eddi M.
	 */
	private boolean AddPresentFertiliser;
	
	/**
	 * Über AddPresentIngredients wird die Sichtbarkeit der Zeile zum hinzufügen von neuen Zutaten geregelt.
	 * Dabei wird in der ersten Zeile die Id des PresentFertiliser gespeichert und in der zweiten Zeile 1 für sichtbar
	 * und 0 für nicht sichtbar.
	 * 
	 * @autor: Eddi M.
	 */
	private int[] AddPresentIngredients;
	
	/**
	 * Ueber AddRequiredFertiliser wird die Sichtbarkeit der Zeile zum hinzufuegen von neuen RequiredFertiliser geregelt.
	 * 
	 * @autor: Eddi M.
	 */
	private boolean AddRequiredFertiliser;
	
	/**
	 * Über AddRequiredIngredients wird die Sichtbarkeit der Zeile zum hinzufügen von neuen Zutaten geregelt.
	 * Dabei wird in der ersten Zeile die Id des RequiredFertiliser gespeichert und in der zweiten Zeile 1 für sichtbar
	 * und 0 für nicht sichtbar.
	 * 
	 * @autor: Eddi M.
	 */
	private int[] AddRequiredIngredients;
	
	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen Ingredients da.
	 * 
	 * @autor: Eddi M.
	 */
	private int ChangeIngredientId = -1;

	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen PresentIngredients da.
	 * 
	 * @autor: Eddi M.
	 */
	private int[] ChangePresentIngredientId;

	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen RequiredIngredients da.
	 * 
	 * @autor: Eddi M.
	 */
	private int[] ChangeRequiredIngredientId;

	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen Fertilisers da.
	 * 
	 * @autor: Eddi M.
	 */
	private int ChangePresentFertiliserId = -1;

	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen Fertilisers da.
	 * 
	 * @autor: Eddi M.
	 */
	private int ChangeRequiredFertiliserId = -1;

	/**
	 * Das Attribut regelt die Sichtbarkeit der geloeschten Ingredients. Ist der Wert von ShowDeletedIngredients auf true gesetzt,
	 * sind die geloeschten Ingredients in der Liste sichtbar. Bei false sind sie verborgen.
	 * 
	 * @autor: Eddi M.
	 */
	private boolean ShowDeletedIngredients;
	
	/**
	 * Das Attribut regelt die Sichtbarkeit der geloeschten PresentFertilisers. Ist der Wert von ShowDeletedPresentFertilisers auf true gesetzt,
	 * sind die geloeschten PresentFertilisers in der Liste sichtbar. Bei false sind sie verborgen.
	 * 
	 * @autor: Eddi M.
	 */
	private boolean ShowDeletedPresentFertilisers;
	
	/**
	 * Das Attribut regelt die Sichtbarkeit der geloeschten PresentIngredients. Ist der Wert von ShowDeletedPresentIngredients[1] auf 1 gesetzt,
	 * sind die geloeschten PresentIngredients in der Liste sichtbar. Bei 0 sind sie verborgen.
	 * Der Wert von ShowDeletedPresentIngredient[0] gelegt dabei die Zuweisung umwelchen Fertiliser es sich handelt.
	 * 
	 * @autor: Eddi M.
	 */
	private int[] ShowDeletedPresentIngredient;
	
	/**
	 * Das Attribut regelt die Sichtbarkeit der geloeschten RequiredFertilisers. Ist der Wert von ShowDeletedRequiredFertilisers auf true gesetzt,
	 * sind die geloeschten RequiredFertilisers in der Liste sichtbar. Bei false sind sie verborgen.
	 * 
	 * @autor: Eddi M.
	 */
	private boolean ShowDeletedRequiredFertilisers;

	/**
	 * Das Attribut regelt die Sichtbarkeit der geloeschten RequiredIngredients. Ist der Wert von ShowDeletedRequiredIngredients[1] auf 1 gesetzt,
	 * sind die geloeschten RequiredIngredients in der Liste sichtbar. Bei 0 sind sie verborgen.
	 * Der Wert von ShowDeletedRequiredIngredient[0] gelegt dabei die Zuweisung umwelchen Fertiliser es sich handelt.
	 * 
	 * @autor: Eddi M.
	 */
	private int[] ShowDeletedRequiredIngredient;

	/**
	 * Initialisierung der Webanwendung,
	 * wird vom Controller aufgerufen
	 * setzt EDbean.parameterFile, Proxy Daten, ...
	 * 
	 * @throws Exception 
	 */
	public static void init(String servletPath) throws Exception {
		
		try {
			
			Properties properties = new Properties();
			BufferedReader configReader = new BufferedReader(new FileReader(EDbean.CONFIG));
			properties.load(configReader);
			
			//Sucht in der Config-Datei nach den Attributen
			EDbean.WebService = properties.getProperty("WebService");
			EDbean.ModelDir = properties.getProperty("ModelDir");
			EDbean.CmplModel = properties.getProperty("CmplModel");
			
			configReader.close();		
		}
		catch (IOException e) {
			System.out.println("aus den folgenden Gründen konnte der Webserver nicht inizialisiert werden");
			e.printStackTrace();
		}
		
		Model.setBasisPfad(EDbean.ModelDir);
		Model.init();
		if(! EDbean.checkWebService()) throw new Exception("Webservice nicht gefunden, siehe log.");

	}
	
	/**
	 * Konstrukter der EDbean
	 * 
	 */
	public EDbean(){
		
		this.nutzer	= "Gast";
		
	}
	
	/**
	 * Setfunktion des Attributs Nutzer.
	 * 
	 * @param nutzer - String
	 */
	public void setNutzer(String nutzer){
		this.nutzer	= nutzer;
	}
	
	/**
	 * Getfunktion des Attributs Nutzer
	 * 
	 * @return - String
	 */
	public String getNutzer(){
		return this.nutzer;
	}
	
	/**
	 * Diese Funktion ermittelt die Id des Fertilisers und gibt sie zurueck.
	 * 
	 * @return - String
	 * @autor: Jonas G.
	 */
	public String getId() {
		return this.modelData.getId();
	}
	
	/**
	 * Diese Funktion wird beim Erstellen des Modells ausgefuehrt.
	 * 
	 * @param modelId - String
	 */
	public void setModel(String modelId){
		this.model		= Model.get(modelId);
		this.modelData	= this.model.getModelData();
		
		this.setAddIngredient(false);
		this.setAddPresentFertiliser(false);
		this.setAddPresentIngredients("0", false);
		this.setAddRequiredFertiliser(false);
		this.setAddRequiredIngredients("0", false);
		this.setChangePresentIngredientId("-1", "-1");
		this.setChangeRequiredIngredientId("-1", "-1");
		this.setShowDeletedPresentIngredient("0", false);
		this.setShowDeletedRequiredIngredient("0", false);
	}
	
	/**
	 * Diese Funktion gibt das Model zurueck.
	 * 
	 * @return - Model
	 */
	public Model getModel(){
		return this.model;
	}
	
	/**
	 * Diese Funktion gibt das aktuelle Datum als String zurück.
	 * 
	 * @return - String
	 */
	public String getDatum(){
		Calendar today = Calendar.getInstance();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(today.getTime());
	}
	
	/**
	 * Die Funktion gibt den Namen des Models als String zurück.
	 * 
	 * @return - String
	 */
	public String getModelName(){
		return this.modelData.getName();
	}
	
	/**
	 * Diese Funktion erlaubt den Nutzer den Modelnamen zu setzen. Ueber geben wird der Name als String.
	 * 
	 * @param name - String
	 */
	public void setModelName(String name){
		this.modelData.setName(name);
	}
	
	/**
	 * Diese Funktion gibt im Fall, dass das Model geloest wurd true und im Fall das nicht false, zurück.
	 * 
	 * @return - Boolean
	 */
	public boolean isSolved(){
		return this.modelData.getSolution().size()>0 && this.modelData.isSolved();
	}
	
	/**
	 * Diese Funktion gibt den SolutionStatus des Models zurueck. Hierfuer wird die SolutionId als Integer uebergeben.
	 * Rueckgabewert ist ein String.
	 * 
	 * @param solId - Integer
	 * @return - String
	 */
	public String getSolutionStatus(int solId){
		return this.modelData.getSolution().get(solId).getStatus();
	}
	
	/**
	 * Diese Funktion gibt die Anzahl aller Ingredients als Integer wieder.
	 * 
	 * @return - Integer
	 */
	private int getTotalIngredients(){
		return this.modelData.getIngredients().getIngredient().size();
	}
	
	/**
	 * Gibt die Anzahl der PresentFertiliser wieder.
	 * 
	 * @return - Integer als Anzahl der PresentFertiliser.
	 */
	private int getTotalPresentFertilisers(){
		return this.modelData.getPresentFertiliser().size();
	}
	
	
	/**
	 * Gibt die Anzahl der RequiredFertiliser wieder.
	 * 
	 * @return - Integer als Anzahl der RequiredFertiliser.
	 */
	private int getTotalRequiredFertilisers(){
		return this.modelData.getRequiredFertiliser().size();
	}
	
	/**
	 * Gibt die Anzahl der Solution wieder.
	 * 
	 * @return - Integer
	 */
	private int getTotalSolutions(){
		return this.modelData.getSolution().size();
	}
	
	/**
	 * Setzt das Attribut AddIngredient auf den boolischen Wert b
	 * 
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setAddIngredient(boolean b){
		if (b == true){
			this.setAddPresentIngredients("0", false);
			this.setAddRequiredIngredients("0", false);
		}
		this.AddIngredient = b;
	}

	/**
	 * Gibt den Wert des Attribut AddIngredients zurueck.
	 * 
	 * @return - Boolean
	 * @autor: Eddi M.
	 */
	public boolean getAddIngredient(){
		return AddIngredient;
	}
	
	/**
	 * Setzt das Attribut AddPresentFertiliser auf den boolischen Wert b
	 * 
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setAddPresentFertiliser(boolean b){
		if (b == true){
			this.setAddPresentIngredients("0", false);
			this.setAddRequiredIngredients("0", false);
		}
		this.AddPresentFertiliser = b;
	}

	/**
	 * Gibt den Wert des Attribut AddPresentFertilisers zurueck.
	 * 
	 * @return - Boolean
	 * @autor: Eddi M.
	 */
	public boolean getAddPresentFertiliser(){
		return AddPresentFertiliser;
	}

	/**
	 * Diese Funktion setzt das Attribut AddPresentIngredient mit dem Wert des PresentFertiliserIds
	 * als Integer in AddPresentIngredient[0] und den Wert 0 fuer dich Sichtbarkeit false und 1 fuer die Sichtbarkeit true.
	 * Des Weitern werden die anderen Rows zum Hinzufuegen eingeklappt.
	 * 
	 * @param strPresentFertiliserId - String
	 * @param sichtbarkeit - Boolean
	 * @autor: Eddi M.
	 */
	public void setAddPresentIngredients(String strPresentFertiliserId, boolean sichtbarkeit){
		int sichtbarkeitAsInt, presentFertiliserId;
		presentFertiliserId = Integer.parseInt(strPresentFertiliserId);
		
		this.AddPresentIngredients = new int[2];
		
		if (sichtbarkeit == true){
			sichtbarkeitAsInt = 1;
			this.setAddIngredient(false);
			this.setAddRequiredIngredients("0", false);
		}else {
			sichtbarkeitAsInt = 0;
		}
		 
		this.AddPresentIngredients[0] = presentFertiliserId;
		this.AddPresentIngredients[1] = sichtbarkeitAsInt;
	}

	/**
	 * Gibt den Wert des Attributs AddPresentIngredient als Array zurueck.
	 * 
	 * @return - int[]
	 * @autor: Eddi M.
	 */
	public int[] getAddPresentIngredients(){
		return this.AddPresentIngredients;
	}
	
	/**
	 * Setzt das Attribut AddRequiredFertiliser auf den boolischen Wert b
	 * 
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setAddRequiredFertiliser(boolean b){
		if (b == true){
			this.setAddPresentIngredients("0", false);
			this.setAddRequiredIngredients("0", false);
		}
		this.AddRequiredFertiliser = b;
	}

	/**
	 * Gibt den Wert des Attribut AddRequiredFertilisers zurueck.
	 * 
	 * @return - Boolean
	 * @autor: Eddi M.
	 */
	public boolean getAddRequiredFertiliser(){
		return AddRequiredFertiliser;
	}

	/**
	 * Diese Funktion setzt das Attribut AddRequiredIngredient mit dem Wert des RequiredFertiliserIds
	 * als Integer in AddRequiredIngredient[0] und den Wert 0 fuer dich Sichtbarkeit false und 1 fuer die Sichtbarkeit true.
	 * Des Weitern werden die anderen Rows zum Hinzufuegen eingeklappt.
	 * 
	 * @param strRequiredFertiliserId - String
	 * @param sichtbarkeit - Boolean
	 * @autor: Eddi M.
	 */
	public void setAddRequiredIngredients(String strRequiredFertiliserId, boolean sichtbarkeit){
		int sichtbarkeitAsInt, requiredFertiliserId;
		requiredFertiliserId = Integer.parseInt(strRequiredFertiliserId);
		
		this.AddRequiredIngredients = new int[2];
		
		if (sichtbarkeit == true){
			sichtbarkeitAsInt = 1;
			this.setAddIngredient(false);
		}else {
			sichtbarkeitAsInt = 0;
		}
		 
		this.AddRequiredIngredients[0] = requiredFertiliserId;
		this.AddRequiredIngredients[1] = sichtbarkeitAsInt;
	}

	/**
	 * Gibt den Wert des Attributs AddRequiredIngredient als Array zurueck.
	 * 
	 * @return - int[]
	 * @autor: Eddi M.
	 */
	public int[] getAddRequiredIngredients(){
		return this.AddRequiredIngredients;
	}

	/**
	 * Setzt das Attribut ChangeIngredientId.
	 * 
	 * @param id - Integer
	 * @autor: Eddi M.
	 */
	public void setChangeIngredientId(int id){
		this.ChangeIngredientId = id;
	}

	/**
	 * Gibt den Integerwert des Attributs ChangeIngredientId zurueck.
	 * 
	 * @return - Interger
	 * @autor: Eddi M.
	 */
	public int getChangeIngredientId(){
		return this.ChangeIngredientId;
	}

	/**
	 * Setzt das Attribut ChangePresentIngredient auf den Wert der Id des Fertilisers und des Ingredients, welche eingegeben wird.
	 * 
	 * @param fertiliserId
	 * @param ingredientId
	 * @autor: Eddi M.
	 */
	public void setChangePresentIngredientId(String fertiliserId, String ingredientId){
		this.ChangePresentIngredientId = new int[2];
		
		int fId = Integer.parseInt(fertiliserId);
		int iId = Integer.parseInt(ingredientId);
		this.ChangePresentIngredientId[0] = fId;
		this.ChangePresentIngredientId[1] = iId;
	}
	
	/**
	 * Gibt den Wert des Attributs ChangePresentIngredientId zurueck.
	 * 
	 * @return - Integer
	 * @autor: Eddi M.
	 */
	public int[] getChangePresentIngredientId(){
		return this.ChangePresentIngredientId;
	}

	/**
	 * Setzt das Attribut ChangePresentFertiliser auf den Wert der Id, welche eingegeben wird.
	 * 
	 * @param sId - String
	 * @autor: Eddi M.
	 */
	public void setChangePresentFertiliser(String sId){
		int id = Integer.parseInt(sId);
		this.ChangePresentFertiliserId = id;
	}
	
	/**
	 * Gibt den Wert des Attributs ChangePresentFertiliser zurueck.
	 * 
	 * @return - Integer
	 * @autor: Eddi M.
	 */
	public int getChangePresentFeritliser(){
		return this.ChangePresentFertiliserId;
	}
	
	/**
	 * Setzt das Attribut ChangeRequiredFertiliser auf den Wert der Id, welche eingegeben wird.
	 * 
	 * @param sId - String
	 * @autor: Eddi M.
	 */
	public void setChangeRequiredFertiliser(String sId){
		int id = Integer.parseInt(sId);
		this.ChangeRequiredFertiliserId = id;
	}
	
	/**
	 * Gibt den Wert des Attributs ChangeRequiredFertiliser zurueck.
	 * 
	 * @return - Integer
	 * @autor: Eddi M.
	 */
	public int getChangeRequiredFeritliser(){
		return this.ChangeRequiredFertiliserId;
	}
	
	/**
	 * Setzt das Attribut ChangeRequiredIngredientId auf den Wert der Id, welche eingegeben wird.
	 * 
	 * @param sId - String
	 * @autor: Eddi M.
	 */
	public void setChangeRequiredIngredientId(String fertiliserId, String ingredientId){
		this.ChangeRequiredIngredientId = new int[2];
		int fId = Integer.parseInt(fertiliserId);
		int iId = Integer.parseInt(ingredientId);
		
		this.ChangeRequiredIngredientId[0] = fId;
		this.ChangeRequiredIngredientId[1] = iId;
	}
	
	/**
	 * Gibt den Wert des Attributs ChangeRequiredIngredientId zurueck.
	 * 
	 * @return - Integer
	 * @autor: Eddi M.
	 */
	public int[] getChangeRequiredIngredientId(){
		return ChangeRequiredIngredientId;
	}
	
	/**
	 * Setzt das Attribut ShowDeletedIngredient auf den boolischen Wert b. 
	 * Dabei werden die Attribute AddPresentIngredient und AddRequiredIngredient auf false gesetzt.
	 * Somit werden alle zum Hinzufuegen notwendige Zeilen eingeklappt, bis auf die der Ingredients.
	 * 
	 * @param b - Boolean
	 * 
	 * @autor: Eddi M.
	 */
	public void setShowDeletedIngredient(boolean b){
		this.setAddPresentIngredients("0", false);
		this.setAddRequiredIngredients("0", false);
		this.ShowDeletedIngredients = b;
	}

	/**
	 * Gibt den Wert des Attributs ShowDeletedIngredients zurück.
	 * 
	 * @return - boolean
	 * 
	 * @autor: Eddi M.
	 */
	public boolean getShowDeletedIngredient(){
		return this.ShowDeletedIngredients;
	}
	
	/**
	 * Setzt das Attribut ShowDeletedPresentFertiliser auf den boolischen Wert b. 
	 * Dabei werden die Attribute AddPresentPresentFertiliser und AddRequiredPresentFertiliser auf false gesetzt.
	 * Somit werden alle zum Hinzufuegen notwendige Zeilen eingeklappt, bis auf die der PresentFertilisers.
	 * 
	 * @param b - Boolean
	 * 
	 * @autor: Eddi M.
	 */
	public void setShowDeletedPresentFertiliser(boolean b){
		this.setAddPresentIngredients("0", false);
		this.setAddRequiredIngredients("0", false);
		this.ShowDeletedPresentFertilisers = b;
	}

	/**
	 * Gibt den Wert des Attributs ShowDeletedPresentFertilisers zurueck.
	 * 
	 * @return - boolean
	 * 
	 * @autor: Eddi M.
	 */
	public boolean getShowDeletedPresentFertiliser(){
		return this.ShowDeletedPresentFertilisers;
	}
	
	/**
	 * Setzt das Attribut ShowDeletedRequiredFertiliser auf den boolischen Wert b. 
	 * Dabei werden die Attribute AddPresentRequiredFertiliser und AddRequiredRequiredFertiliser auf false gesetzt.
	 * Somit werden alle zum Hinzufuegen notwendige Zeilen eingeklappt, bis auf die der RequiredFertilisers.
	 * 
	 * @param b - Boolean
	 * 
	 * @autor: Eddi M.
	 */
	public void setShowDeletedRequiredFertiliser(boolean b){
		this.setAddPresentIngredients("0", false);
		this.setAddRequiredIngredients("0", false);
		this.ShowDeletedRequiredFertilisers = b;
	}

	/**
	 * Gibt den Wert des Attributs ShowDeletedRequiredFertilisers zurück.
	 * 
	 * @return - boolean
	 * 
	 * @autor: Eddi M.
	 */
	public boolean getShowDeletedRequiredFertiliser(){
		return this.ShowDeletedRequiredFertilisers;
	}

	/**
	 * Diese Funktion regelt die Sichtbarkeit der geloeschten Prequiredngredients.
	 * 
	 * @param strPresentFertiliserId - String
	 * @param sichtbarkeit - Boolean
	 * @autor: Eddi M.
	 */
	public void setShowDeletedPresentIngredient(String strPresentFertiliserId, boolean sichtbarkeit){
		int sichtbarkeitAsInt, presentFertiliserId;
		presentFertiliserId = Integer.parseInt(strPresentFertiliserId);
		this.ShowDeletedPresentIngredient = new int[2];
		if (sichtbarkeit == true){
			sichtbarkeitAsInt = 1;
			this.setAddIngredient(false);
		}else{
			sichtbarkeitAsInt = 0;
		}
		
		this.ShowDeletedPresentIngredient[0] = presentFertiliserId;
		this.ShowDeletedPresentIngredient[1] = sichtbarkeitAsInt;
	}
	
	/**
	 * Gibt den Wert des Attributs ShowDeletedPresentIngredient als Array zurueck.
	 * 
	 * @return - int[]
	 * @autor: Eddi M.
	 */
	public int[] getShowDeletedPresentIngredient(){
		return this.ShowDeletedPresentIngredient;
	}
	
	/**
	 * Diese Funktion regelt die Sichtbarkeit der geloeschten RequiredIngredients.
	 * 
	 * @param strRequiredFertiliserId - String
	 * @param sichtbarkeit - Boolean
	 * @autor: Eddi M.
	 */
	public void setShowDeletedRequiredIngredient(String strRequiredFertiliserId, boolean sichtbarkeit){
		int sichtbarkeitAsInt, requiredFertiliserId;
		requiredFertiliserId = Integer.parseInt(strRequiredFertiliserId);
		this.ShowDeletedRequiredIngredient = new int[2];
		if (sichtbarkeit == true){
			sichtbarkeitAsInt = 1;
			this.setAddIngredient(false);
		}else{
			sichtbarkeitAsInt = 0;
		}
		
		this.ShowDeletedRequiredIngredient[0] = requiredFertiliserId;
		this.ShowDeletedRequiredIngredient[1] = sichtbarkeitAsInt;
	}
	
	/**
	 * Gibt den Wert des Attributs ShowDeletedRequiredIngredient als Array zurueck.
	 * 
	 * @return - int[]
	 * @auter: Eddi M.
	 */
	public int[] getShowDeletedRequiredIngredient(){
		return this.ShowDeletedRequiredIngredient;
	}
	
	/**
	 * Setzt das Attribut ShowId auf den boolischen Wert b
	 * 
	 * @param b - Boolean
	 */
	public void setShowID(boolean b){
		this.showID = b;
	}
	
	/**
	 * Gibt den boolischen Wert showId zurueck.
	 * 
	 * @return - boolean
	 * @autor: Eddi M.
	 */
	public boolean getShowID(){
		return this.showID;
	}
	
	/**
	 * Diese Funktion schaut ob die Zutat vorhanden ist und fuegt eine neue hinzu oder speichert die Veraenderung.
	 * 
	 * @param name - String
	 * @param price - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	public void addOrChangeIngredient(String name, String strPrice, String strUnit){
		double price = 0.0;
		Units unit = Units.valueOf(strUnit.toUpperCase());
		price = Double.parseDouble(strPrice);
		
		this.addOrChangeIngredient(name, price, unit);
	}
	
	/**
	 * Diese Funktion schaut ob die Zutat vorhanden ist und fuegt eine neue hinzu oder speichert die Veraenderung.
	 * 
	 * @param name - String
	 * @param price - double
	 * @param unit - Unit
	 * @autor: Eddi M.
	 */
	private void addOrChangeIngredient(String name, double price, Units unit){
		int ingredientId = model.getIngredientIdFromIngredientName(name);
		if (model.getIdsOfIngredients().contains(ingredientId) == true){
			this.changeIngredient(ingredientId, price, unit);
		}else {
			this.addIngredient(name, price, unit);
		}
	}
	
	/**
	 * Diese Funktion speichert ein neuen Ingredient mit den Inhalten als String.
	 * 
	 * @param name - String
	 * @param price - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	private void addIngredient(String name, double price, Units unit){
		int newId = this.getTotalIngredients();
		this.model.addIngredient(newId , name, price, unit);
	}
	
	/**
	 * Diese Funktion aendert die Eigenschaften des Ingredients und speichert die Aenderungen.
	 * 
	 * @param ingId - String
	 * @param price - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	private void changeIngredient(int ingId, double price, Units unit){
		Ingredient ingredient = model.getIngredient(ingId);
		this.model.changeIngredient(ingredient, price, unit);
	}
	
	/**
	 * Diese Funktion schaut ob die Zutat vorhanden ist und fuegt eine neue hinzu oder speichert die Veraenderung.
	 * 
	 * @param name - String
	 * @param amount - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	public void addOrChangePresentFertiliser(String name, String strAmount, String strUnit){
		double amount = 0.0;
		Units unit = Units.valueOf(strUnit.toUpperCase());
		amount = Double.parseDouble(strAmount);
		
		this.addOrChangePresentFertiliser(name, amount, unit);
	}
	
	/**
	 * Diese Funktion schaut ob die Duenger vorhanden ist und fuegt eine neue hinzu oder speichert die Veraenderung.
	 * 
	 * @param name - String
	 * @param amount - double
	 * @param unit - Unit
	 * @autor: Eddi M.
	 */
	private void addOrChangePresentFertiliser(String name, double amount, Units unit){
		int presentFertiliserId = model.getIdOfPresentFertiliserFromPresentFertiliserName(name);
		if (model.getIdsOfPresentFertilisers().contains(presentFertiliserId) == true){
			System.out.println("addOrChangePresentFertiliser2:then");
			this.changePresentFertiliser(presentFertiliserId, amount, unit);
		}else {
			System.out.println("addOrChangePresentFertiliser2:else");
			this.addPresentFertiliser(name, amount, unit);
		}
	}
	
	/**
	 * Diese Funktion speichert ein neuen PresentFertiliser mit den Inhalten als String.
	 * 
	 * @param name - String
	 * @param amount - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	private void addPresentFertiliser(String name, double amount, Units unit){
		System.out.println("addPresentFertiliser");
		int newId = this.getTotalPresentFertilisers();
		this.model.addPresentFertiliser(newId , name, amount, unit);
	}
	
	/**
	 * Diese Funktion aendert die Eigenschaften des PresentFertilisers und speichert die Aenderungen.
	 * 
	 * @param presentFertiliserId - String
	 * @param amount - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	private void changePresentFertiliser(int presentFertiliserId, double amount, Units unit){
		PresentFertiliser presentFertiliser = model.getPresentFertiliser(presentFertiliserId);
		this.model.changePresentFertiliser(presentFertiliser, amount, unit);
	}
	
	/**
	 * Diese Funktion entscheidet anhand der FertiliserId, des Namens des PresentIngredient, ob die Angabe als neuer PresentIngredient gespeichert oder
	 * ein vorhander Ingredient aktualisiert werden soll.
	 * Alle Parameter sind dabei als String zu übergeben.
	 * 
	 * Die Funktion fürt die zum Speichern notwendigen Funktionen aus.
	 * 
	 * @param fId - Die Id des Fertilisers als String
	 * @param name - Name des PresentIngredient als String
	 * @param percent - Prozentwert des PresentIngredients als String
	 * @autor: Eddi M.
	 */
	public void addOrChangePresentIngredientViaName(String fId, String name, String percent){
		int presentIngredientId, presentFertiliserId;
		double p;
		presentIngredientId = this.model.getIngredientIdFromIngredientName(name);
		
		try {
			presentFertiliserId = Integer.parseInt(fId);
			p = Double.parseDouble(percent);
		} catch(NumberFormatException e){
			presentFertiliserId = 0;
			p = 0.0;
		}
		
		this.addOrChangePresentIngredient(presentFertiliserId, presentIngredientId, p);
	}
	
	/**
	 * Diese Funktion entscheidet anhand der FertiliserId und der Id des PresentIngredient, ob die Angabe als neuer PresentIngredient gespeichert oder
	 * ein vorhander Ingredient aktualisiert werden soll.
	 * Alle Parameter sind dabei als String zu übergeben.
	 * 
	 * Die Funktion fürt die zum Speichern notwendigen Funktionen aus.
	 * 
	 * @param fId - Die Id des Fertilisers als String
	 * @param strPresentIngredientId - Id des PresentIngredients als String
	 * @param percent - Prozentwert des PresentIngredients als String
	 * @autor: Eddi M.
	 */
	public void addOrChangePresentIngredientViaId(String fId, String strPresentIngredientId, String percent){
		int presentIngredientId, presentFertiliserId;
		double p = 0.0;
		presentIngredientId = Integer.parseInt(strPresentIngredientId);
		
		try {
			presentFertiliserId = Integer.parseInt(fId);
			p = Double.parseDouble(percent);
		} catch(NumberFormatException e){
			presentFertiliserId = 0;
			System.out.println("Die eingelesenen Daten konnten nicht überführt werden.");
		}
		this.addOrChangePresentIngredient(presentFertiliserId, presentIngredientId, p);
		
	}
	
	/**
	 * Diese Funktion entscheidet anhand der FertiliserId und der Id des PresentIngredient, ob die Angabe als neuer PresentIngredient gespeichert oder
	 * ein vorhander Ingredient aktualisiert werden soll.
	 * 
	 * Die Funktion fürt die zum Speichern notwendigen Funktionen aus.
	 * 
	 * @param fId - Die Id des Fertilisers als Integer
	 * @param strPresentIngredientId - Id des PresentIngredients als Integer
	 * @param percent - Prozentwert des PresentIngredients als double
	 * @autor: Eddi M.
	 */
	private void addOrChangePresentIngredient(int presentFertiliserId, int presentIngredientId, double p){
		if (this.model.getIdsOfPresentIngredientsFromPresentFertiliser(model.getPresentFertiliser(presentFertiliserId)).contains(presentIngredientId) == true){
			int index = this.model.getIndexOfPresentIngredientFromIngredientId(this.model.getPresentFertiliser(presentFertiliserId), presentIngredientId);
			this.changePresentIngredient(presentFertiliserId, index, p);
		}else {
			this.addPresentIngredient(presentFertiliserId, presentIngredientId, p);
		}
	}
	
	/**
	 * Diese Funktion erstellt ein neuen PresentIngredient und speichert ihn ab.
	 * 
	 * @param fertiliserId
	 * @param presentIngredientId
	 * @param percent
	 * @autor: Eddi M.
	 */
	private void addPresentIngredient(int fertiliserId, int presentIngredientId, double percent){
		this.model.addPresentIngredient(fertiliserId, presentIngredientId, percent);
	}
	
	
	/**
	 * Diese Funktion speichert die Aenderungen des PresentIngredient.
	 * 
	 * @param fertiliserId - Integer
	 * @param presentIngredientIndex - Integer
	 * @param percent - double
	 * @autor: Eddi M.
	 */
	private void changePresentIngredient(int fertiliserId, int presentIngredientIndex, double percent){
		this.model.changePresentIngredient(fertiliserId, presentIngredientIndex, percent);
	}
	
	/**
	 * Diese Funktion schaut ob die Zutat vorhanden ist und fuegt eine neue hinzu oder speichert die Veraenderung.
	 * 
	 * @param name - String
	 * @param amount - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	public void addOrChangeRequiredFertiliser(String name, String strAmount, String strUnit){
		double amount = 0.0;
		Units unit = Units.valueOf(strUnit.toUpperCase());
		amount = Double.parseDouble(strAmount);
		
		this.addOrChangeRequiredFertiliser(name, amount, unit);
	}
	
	/**
	 * Diese Funktion schaut ob die Duenger vorhanden ist und fuegt eine neue hinzu oder speichert die Veraenderung.
	 * 
	 * @param name - String
	 * @param amount - double
	 * @param unit - Unit
	 * @autor: Eddi M.
	 */
	private void addOrChangeRequiredFertiliser(String name, double amount, Units unit){
		int requiredFertiliserId = model.getIdOfRequiredFertiliserFromRequiredFertiliserName(name);
		if (model.getIdsOfRequiredFertilisers().contains(requiredFertiliserId) == true){
			System.out.println("addOrChangeRequiredFertiliser2:then");
			this.changeRequiredFertiliser(requiredFertiliserId, amount, unit);
		}else {
			System.out.println("addOrChangeRequiredFertiliser2:else");
			this.addRequiredFertiliser(name, amount, unit);
		}
	}
	
	/**
	 * Diese Funktion speichert ein neuen RequiredFertiliser mit den Inhalten als String.
	 * 
	 * @param name - String
	 * @param amount - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	private void addRequiredFertiliser(String name, double amount, Units unit){
		System.out.println("addRequiredFertiliser");
		int newId = this.getTotalRequiredFertilisers();
		this.model.addRequiredFertiliser(newId , name, amount, unit);
	}
	
	/**
	 * Diese Funktion aendert die Eigenschaften des RequiredFertilisers und speichert die Aenderungen.
	 * 
	 * @param requiredFertiliserId - String
	 * @param amount - String
	 * @param unit - String
	 * @autor: Eddi M.
	 */
	private void changeRequiredFertiliser(int requiredFertiliserId, double amount, Units unit){
		RequiredFertiliser requiredFertiliser = model.getRequiredFertiliser(requiredFertiliserId);
		this.model.changeRequiredFertiliser(requiredFertiliser, amount, unit);
	}
	
	/**
	 * Diese Funktion entscheidet anhand der FertiliserId, des Namens des RequiredIngredient, ob die Angabe als neuer RequiredIngredient gespeichert oder
	 * ein vorhander Ingredient aktualisiert werden soll.
	 * Alle Parameter sind dabei als String zu uebergeben.
	 * 
	 * Die Funktion fuert die zum Speichern notwendigen Funktionen aus.
	 * 
	 * @param fId - Die Id des Fertilisers als String
	 * @param name - Name des RequiredIngredient als String
	 * @param percent - Prozentwert des RequiredIngredients als String
	 * @autor: Eddi M.
	 */
	public void addOrChangeRequiredIngredientViaName(String fId, String name, String percentMin, String percentMax){
		int requiredIngredientId, requiredFertiliserId;
		double pMin = 0.0, pMax = 0.0;
		requiredIngredientId = this.model.getIngredientIdFromIngredientName(name);
		
		try {
			requiredFertiliserId = Integer.parseInt(fId);
			pMin = Double.parseDouble(percentMin);
			pMax = Double.parseDouble(percentMax);
		} catch(NumberFormatException e){
			requiredFertiliserId = 0;
		}
		
		this.addOrChangeRequiredIngredient(requiredFertiliserId, requiredIngredientId, pMin, pMax);
	}
	
	/**
	 * Diese Funktion entscheidet anhand der FertiliserId und der Id des RequiredIngredient, ob die Angabe als neuer RequiredIngredient gespeichert oder
	 * ein vorhander Ingredient aktualisiert werden soll.
	 * Alle Parameter sind dabei als String zu uebergeben.
	 * 
	 * Die Funktion fuert die zum Speichern notwendigen Funktionen aus.
	 * 
	 * @param fId - Die Id des Fertilisers als String
	 * @param strRequiredIngredientId - Id des RequiredIngredients als String
	 * @param percent - Prozentwert des RequiredIngredients als String
	 * @autor: Eddi M.
	 */
	public void addOrChangeRequiredIngredientViaId(String fId, String strRequiredIngredientId, String percentMin, String percentMax){
		int requiredIngredientId, requiredFertiliserId;
		double pMin = 0.0, pMax = 0.0;
		requiredIngredientId = Integer.parseInt(strRequiredIngredientId);
		
		try {
			requiredFertiliserId = Integer.parseInt(fId);
			pMin = Double.parseDouble(percentMin);
			pMax = Double.parseDouble(percentMax);
		} catch(NumberFormatException e){
			requiredFertiliserId = 0;
		}
		this.addOrChangeRequiredIngredient(requiredFertiliserId, requiredIngredientId, pMin, pMax);
		
	}
	
	/**
	 * Diese Funktion entscheidet anhand der FertiliserId und der Id des RequiredIngredient, ob die Angabe als neuer RequiredIngredient gespeichert oder
	 * ein vorhander Ingredient aktualisiert werden soll.
	 * 
	 * Die Funktion fürt die zum Speichern notwendigen Funktionen aus.
	 * 
	 * @param fId - Die Id des Fertilisers als Integer
	 * @param strRequiredIngredientId - Id des RequiredIngredients als Integer
	 * @param percent - Prozentwert des RequiredIngredients als double
	 * @autor: Eddi M.
	 */
	private void addOrChangeRequiredIngredient(int requiredFertiliserId, int requiredIngredientId, double percentMin, double percentMax){
		if (this.model.getIdsOfRequiredIngredientsFromRequiredFertiliser(model.getRequiredFertiliser(requiredFertiliserId)).contains(requiredIngredientId) == true){
			int index = this.model.getIndexOfRequiredIngredientFromIngredientId(this.model.getRequiredFertiliser(requiredFertiliserId), requiredIngredientId);
			this.changeRequiredIngredient(requiredFertiliserId, index, percentMin, percentMax);
		}else {
			this.addRequiredIngredient(requiredFertiliserId, requiredIngredientId, percentMin, percentMax);
		}
	}
	
	/**
	 * Diese Funktion erstellt ein neuen RequiredIngredient und speichert ihn ab.
	 * 
	 * @param fertiliserId - Integer
	 * @param requiredIngredientId - Integer
	 * @param percentMin - double
	 * @param percentMax - double
	 * @autor: Eddi M.
	 */
	private void addRequiredIngredient(int fertiliserId, int requiredIngredientId, double percentMin, double percentMax){
		this.model.addRequiredIngredient(fertiliserId, requiredIngredientId, percentMin, percentMax);
	}
	
	/**
	 * Diese Funktion speichert die Aenderungen des RequiredIngredient.
	 * 
	 * @param fertiliserId - Integer
	 * @param presentIngredientIndex - Integer
	 * @param percentMin - double
	 * @param percentMax - double
	 * @autor: Eddi M.
	 */
	private void changeRequiredIngredient(int fertiliserId, int presentIngredientIndex, double percentMin, double percentMax){
		this.model.changeRequiredIngredient(fertiliserId, presentIngredientIndex, percentMin, percentMax);
	}
	
	/**
	 * Diese Funktion Rundet eine double Zahl Kaufmännisch auf die mitgegebene Nachkommastelle.
	 * 
	 * @param zahl
	 * @param stellenNachKomma
	 * @return
	 * @autor: Eddi M.
	 */
	public double runden(double zahl, int stellenNachKomma){
		int temp = (int) (zahl * Math.pow(10.0, (double) stellenNachKomma));
		zahl = (double)(temp);
		zahl = zahl / Math.pow(10.0, (double) stellenNachKomma);
		return zahl;
	}
	
	/**
	 * Diese Funktion aktuallisiert den Prozentwert der PresentIngredients eines Fertilisers.
	 * Um welchen Fertilisers sich handel wird durch den eingegebenen String definiert.
	 * 
	 * @param fId - String
	 * @autor: Eddi M.
	 */
	public void aktualisierePercent(String fId){
		int presentFertiliserId = Integer.parseInt(fId);
		
		this.aktualisierePercent(presentFertiliserId);
	}
	
	/**
	 * Diese Funktion aktualisiert die Prozentwerte der vorhandenen PresentIngrediens, wenn der Gesamtwert über 100 % sein sollte.
	 * Die Prozente werden automatisch angepasst, sodass das gleiche Verhaeltnis bestehen bleibt.
	 * 
	 * @param fId - Integer
	 * @autor: Eddi M.
	 */
	private void aktualisierePercent(int fId){
		
		PresentFertiliser fertiliser = this.modelData.getPresentFertiliser().get(fId);
		
		double sumPercent = 0.0;
		sumPercent += this.getTotalPercentOfPresentIngredients(fId);
		for (int i=0; i< this.getTotalIngredients(); i++){
			if (this.modelData.getIngredients().getIngredient().get(i).isActive() == true && this.modelData.getIngredients().getIngredient().get(i).isActive() == true && model.getIdsOfPresentIngredientsFromPresentFertiliser(model.getPresentFertiliser(fId)).contains(i) == true ){
				
				int index = model.getIndexOfPresentIngredientFromIngredientId(fertiliser, i);
				PresentIngredient pi = model.getPresentIngredient(fertiliser, index);
				
				if (pi.isActive() == false){
					this.setPresentIngredientActive(fertiliser, Integer.toString(i), true);
				}
				if (pi.isActive() == true){
					double neuPercent = runden((100) * (pi.getPercent()/(sumPercent)), 2);
					changePresentIngredient(fId, index, neuPercent);
				}
			}
		}
	}
	
	/**
	 * liefert HTML String fuer ZutatenTableau
	 * @return - String
	 */
	public String getZutatenTableau(){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		out += "<div class=\"container\">";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		if (this.showID == true){
			out += "<tr><th colspan=\"6\">Zutaten</th></tr>\n";
		}else if (this.showID == false){
			out += "<tr><th colspan=\"5\">Zutaten</th></tr>\n";
		}
		out += "<tr>";
		if (this.showID == true){
			out += "<th>Id</th>";
		}
		out += "<th>";
		if (this.ShowDeletedIngredients == true){
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "deleted ";
			out += this.getImageAndToolTip(Images.Invisible);
			out += "<input type=\"hidden\" name=\"action\" value=\"24_dontshowDeletedIngredients\"/>";
			out += "</form>";
		}else {
			if(right.equals("x") || right.equals("w")) {
				out += "<form action=\"Controller\" method=\"post\" />";
				out += "deleted ";
				out += this.getImageAndToolTip(Images.Visible);
				out += "<input type=\"hidden\" name=\"action\" value=\"23_showDeletedIngredients\"/>";
				out += "</form>";
			}
		}
		out += "</th><th>Name</th><th>Preis [&euro;/Einheit]</th><th>Einheit</th>";
		out += "<form action=\"Controller\" method=\"post\" />";
		if (this.AddIngredient == false){
			out += "<td>";
			if(right.equals("x") || right.equals("w")) {
				out += this.getImageAndToolTip(Images.Add);
			}
			out += "</td></tr>\n";
			out += "<input type=\"hidden\" name=\"action\" value=\"20_showRowAdding\"/>";
		}else if (this.AddIngredient == true){
			out += "<td>";
			out += this.getImageAndToolTip(Images.Back);
			out += "</td></tr>\n";
			out += "<input type=\"hidden\" name=\"action\" value=\"21_NoShowRowAdding\"/>";
		}
		out += "</form>";
		
		for (int i=0; i< this.getTotalIngredients(); i++ ){
			out += "<tr>";
			
			if (this.modelData.getIngredients().getIngredient().get(i).isActive() == true || this.ShowDeletedIngredients == true){
				
				if (i == this.getChangeIngredientId()){
					if (this.showID == true){
						out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getId()+"</td>";
					}
					out += this.getDeleteIngredientButton(i);
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getName()+"</td>";
					out += "<td><input type=\"text\" class=\"form-control\" name=\"addIngredientPrice\" value=\""+this.modelData.getIngredients().getIngredient().get(i).getPrice()+ "\"/></td>";
					out += "<td><select class=\"form-control\" name=\"addIngredientUnit\" >";
					for( Units u : Units.values()){
						if (u.value() == this.modelData.getIngredients().getIngredient().get(i).getUnit().value()){
							out += "<option selected>"+u.value()+"</option>";
						}else{
							out += "<option>"+u.value()+"</option>";
						}
					}
					out += "</select>";
					out += "<td>";
					out += this.getImageAndToolTip(Images.Done);
					out += "</td>";
					out += "<input type=\"hidden\" name=\"ingredientName\" value=\""+this.modelData.getIngredients().getIngredient().get(i).getName()+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"07_saveEditIngredient\"/>";
					out += "</form>";
				}else{
					if (this.showID == true){
						out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getId()+"</td>";
					}
					out += this.getDeleteIngredientButton(i);
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getName()+"</td>";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getPrice()+"</td>";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getUnit()+"</td>";
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "</select>";
					out += "<td>";
					if(right.equals("x") || right.equals("w")) {
						out += this.getImageAndToolTip(Images.Edit);	
					}
					out += "</td>";
					out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+i+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"06_editIngredient\"/>";
					out += "</form>";
				}
				
			}
			
			out += "</tr>\n";
		}
		if (this.AddIngredient == true){
			out += this.getRowAddIngredient();
		}
		out += "</table> </div>\n";
		return out;
	}
	
	/**
	 * Diese Funktion gibt ein String zurück in dem der Image mit AltTag und Tooltip dargestellt wird.
	 * 
	 * @param image des Typs Images
	 * @return String
	 * 
	 * @autor: Eddi M.
	 */
	private String getImageAndToolTip(Images image){
		String out="";
		
		out += "<input id=\"image\" type=\"image\" src=\"" + image.getImagePfad() + "\" alt=\""+ image.getAltTag() +"\" data-toggle=\"tooltip\" data-placement=\"top\" title=\""+ image.getToolTip() +"\" />";
		return out;
	}
	
	/**
	 * Diese Funktion fuegt den button zum loeschen in die Tabelle ein.
	 * 
	 * @parm Integerwert der die ID des Ingredients wiedergibt.
	 * @return Es wird der Button als html String zurück gegeben
	 * @autor: Eddi M.
	 */
	private String getDeleteIngredientButton(int ingredientId){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		if (this.ShowDeletedIngredients == true && this.modelData.getIngredients().getIngredient().get(ingredientId).isActive() == false){
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			out += this.getImageAndToolTip(Images.Add);
			out += "</td>";
			out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+ingredientId+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"25_undeleteIngredients\" />";
			out += "</form>";
		}else{
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			if(right.equals("x") || right.equals("w")) {
				out += this.getImageAndToolTip(Images.Delete);	
			}
			out += "</td>";
			out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+ingredientId+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"22_deleteIngredients\" />";
			out += "</form>";
		}
		return out;
	}
	
	/**
	 * Dies Funktion gibt die Zeile zurueck mit der eine neue Zutat hinzugefuegt werden kann.
	 * 
	 * @param String, welcher erweitert wird
	 * @return Gibt die Zeile zum hinzufügen von Zutaten als String zurueck.
	 * 
	 * @autor: Eddi M.
	 */
	private String getRowAddIngredient(){
		String out = "";
		
		out += "<form type=\"hidden\" action=\"Controller\" method=\"post\" />";
		out += "<tr>";
		if (this.showID == true){
			out += "<td bgcolor=\"black\"></td>";
			out += "<td bgcolor=\"black\"></td>";
		}else if (this.showID == false){
			out += "<td bgcolor=\"black\"></td>";
		}
		out += "<td><input type=\"text\" class=\"form-control\" name=\"addIngredientName\" autofocus /></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"addIngredientPrice\" /></td>";
		out += "<td><select class=\"form-control\" name=\"addIngredientUnit\" >";
		for( Units u : Units.values()){
			out += "<option>"+u.value()+"</option>";
		}
		out += "</select>";
		out += "<td>";
		out += this.getImageAndToolTip(Images.Done);
		out += "</td>";
		out += "<input type=\"hidden\" name=\"action\" value=\"04_addIngredient\"/>";
		out += "</tr>\n";
		out += "</form>";
		
		return out;
	}
	
	/**
	 * liefert HTML String für Vorhandenen Dünger
	 * @return - String
	 */
	public String getVorhandenDuengerTableau(){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		out += "<div class=\"container\">";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		if (this.showID == true){
			out += "<tr><th colspan=\"6\">";
		}else{
			out += "<tr><th colspan=\"5\">";
		}
		out += "<u>Vorhandene Duenger</u></th></tr>\n";
		out += "<tr>";
		if (this.showID == true){
			out += "<th>Id</th>";
		}
		out += "<th><form action=\"Controller\" method=\"post\" />";
		if (this.getShowDeletedPresentFertiliser() == false && (right.equals("x") || right.equals("w"))){
			out += "deleted ";
			out += this.getImageAndToolTip(Images.Visible);
			out += "<input type=\"hidden\" name=\"action\" value=\"47_showDeletedPresentFertilisers\"/>";
		}else if (this.getShowDeletedPresentFertiliser() == true){
			out += "deleted ";
			out += this.getImageAndToolTip(Images.Invisible);
			out += "<input type=\"hidden\" name=\"action\" value=\"48_dontshowDeletedPresentFertilisers\"/>";
		}
		
		out += "</form></th>";
		out += "<th>Name</th>";
		out += "<th>Bestand</th>";
		out += "<th>Einheit</th>";
		out += "<form action=\"Controller\" method=\"post\" />";
		out += "<td>";
		if (this.getAddPresentFertiliser() == false){
			if(right.equals("x") || right.equals("w")) {
				out += this.getImageAndToolTip(Images.Add);	
			}
			out += "<input type=\"hidden\" name=\"action\" value=\"43_showRowAddingPresentFertiliser\"/>";
		}else if (this.getAddPresentFertiliser() == true){
			out += this.getImageAndToolTip(Images.Back);
			out += "<input type=\"hidden\" name=\"action\" value=\"44_NoShowRowAddingPresentFertiliser\"/>";
		}
		out += "</td></tr>\n";
		out += "</form>";
		
		for (int i=0; i< this.getTotalPresentFertilisers(); i++ ){
			PresentFertiliser fertiliser = this.modelData.getPresentFertiliser().get(i);
			System.out.println("PresentFertiliser: " + i + " wird geladen Id: " + fertiliser.getId());
			
			if (fertiliser.isActive() == true || this.getShowDeletedPresentFertiliser() == true){
			
				out += "<tr>";
				if (this.showID == true){
					out += "<td>"+fertiliser.getId()+"</td>";
				}
				out += this.getDeletePresentFertiliserButton(fertiliser);
				if (i == this.getChangePresentFeritliser() && (right.equals("x") || right.equals("w"))){
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "<td>"+ fertiliser.getName() +"</td>";
					out += "<td><input type=\"text\" class=\"form-control\" name=\"fertiliserAmount\" value=\""+fertiliser.getAmount()+ "\"/></td>";
					out += "<td><select class=\"form-control\" name=\"fertiliserUnit\" >";
					for( Units u : Units.values()){
						if (u.value() == fertiliser.getUnit().value()){
							out += "<option selected>"+u.value()+"</option>";
						}else{
							out += "<option>"+u.value()+"</option>";
						}
					}
					out += "</select>";
					out += "<td>";
					out += this.getImageAndToolTip(Images.Done);
					out += "</td>";
					out += "<input type=\"hidden\" name=\"fertiliserName\" value=\""+fertiliser.getName()+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"46_saveEditPresentFertiliser\"/>";
				}else{
					out += "<td>"+ fertiliser.getName() +"</td>";
					out += "<td>"+ fertiliser.getAmount() +"</td>";
					out += "<td>"+ fertiliser.getUnit() +"</td>";
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "<td>";
					if (right.equals("x") || right.equals("w")){
						out += this.getImageAndToolTip(Images.Edit);
					}
					out += "</td>";
					out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+fertiliser.getId()+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"45_editPresentFertiliser\"/>";
						
				}
				out += "</form>";
				out += "</tr>";
				
				out += "<tr>";
				if (this.showID == true){
					out += "<td colspan=\"6\">";
				}else{
					out += "<td colspan=\"5\">";
				}
				
				//Hier beginnt die Tabelle mit den PresentIngredients
				out += "<div class=\"container\">";
				out += "<table border=\"1\" class='table table-bordered' style=' text-align: center' >";
				if (this.showID == true){
					out += "<th>Id</th>";
				}
				out += "<th>";
				if (this.ShowDeletedPresentIngredient[0] == i && this.ShowDeletedPresentIngredient[1] == 1){
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "deleted ";
					out += this.getImageAndToolTip(Images.Invisible);
					out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"29_dontshowDeletedPresentIngredients\"/>";
					out += "</form>";
				}else{
					out += "<form action=\"Controller\" method=\"post\" />";
					if(right.equals("x") || right.equals("w")) {
						out += "deleted ";
						out += this.getImageAndToolTip(Images.Visible);
					}
					out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"30_showDeletedPresentIngredients\"/>";
					out += "</form>";
				}
				out += "</th>";
				out += "<th>Name</th><th>";
				if (fertiliser.isHasPresentIngredients() == true && (right.equals("x") || right.equals("w"))){
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "" + this.getTotalPercentOfPresentIngredients(fertiliser.getId()) + " % ";
					out += this.getImageAndToolTip(Images.FillUp);
					out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"32_aktualisierePercentOfPresentIngredients\"/>";
					out += "</form>";
				}else if (fertiliser.isHasPresentIngredients() == true){
					out += "" + this.getTotalPercentOfPresentIngredients(fertiliser.getId()) + " % ";
				}else{
					out += "Prozentwert %";
				}
				
				out += "</th>";
				out += "<th>";
				
				out += "<form action=\"Controller\" method=\"post\" />";
				if (this.AddPresentIngredients[1] == 1 && this.AddPresentIngredients[0] == i){
					out += this.getImageAndToolTip(Images.Back);
					out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"27_NoShowRowPresentIngredientAdding\"/>";
				}else{
					if(right.equals("x") || right.equals("w")) {
						out += this.getImageAndToolTip(Images.Add);	
					}
					out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"26_showRowPresentIngredientAdding\"/>";
				}
				out += "</form>";
				
				out += "</th>";
				out += "</tr>\n";
				for(int j=0; j< this.getTotalIngredients(); j++){
					
					System.out.println("RequiredFertiliser: " + i + " RequiredIngredient: " + j);
					System.out.println("RequiredIngredients of RequiredFertiliser: " + model.getIdsOfPresentIngredientsFromPresentFertiliser(fertiliser));
					if (this.modelData.getIngredients().getIngredient().get(j).isActive() == true &&
						model.getIdsOfPresentIngredientsFromPresentFertiliser(fertiliser).contains(j) == true){
						
						int index = model.getIndexOfPresentIngredientFromIngredientId(fertiliser, j);
						PresentIngredient pi = fertiliser.getPresentIngredients().getPresentIngredient().get(index);
						System.out.println("Status von requiredIngredient: " + pi.isActive());
						if (pi.isActive() == true || 
								(this.ShowDeletedPresentIngredient[0] == i && 
								this.ShowDeletedPresentIngredient[1] == 1
								)
							){
						
							out += "<tr>";
							if (this.showID == true){
								out += "<td>"+pi.getIngredientId()+"</td>";
							}
							out += this.getDeletePresentIngredientButton(i, index);
							out += "<td>"+this.modelData.getIngredients().getIngredient().get(pi.getIngredientId()).getName()+"</td>";
							
							if (i == this.getChangePresentIngredientId()[0] && index == model.getIndexOfPresentIngredientFromIngredientId(fertiliser, this.getChangePresentIngredientId()[1])){
								out += "<form action=\"Controller\" method=\"post\" />";
								out += "<td><input type=\"text\" class=\"form-control\" name=\"changeIngredientPercent\" value=\""+pi.getPercent()+"\"/></td>";
								out += "<td>";
								out += this.getImageAndToolTip(Images.Done);
								out += "</td>";
								out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+fertiliser.getId()+"\"/>";
								out += "<input type=\"hidden\" name=\"presentIngredientId\" value=\""+pi.getIngredientId()+"\"/>";
								out += "<input type=\"hidden\" name=\"action\" value=\"09_saveEditPresentIngredient\"/>";
								out += "</form>";
							}else{
								out += "<td>"+pi.getPercent()+"</td>";
								out += "<form action=\"Controller\" method=\"post\" />";
								out += "</select>";
								out += "<td>";
								if(right.equals("x") || right.equals("w")) {
									out += this.getImageAndToolTip(Images.Edit);
								}
								out += "</td>";
								out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+i+"\"/>";
								out += "<input type=\"hidden\" name=\"presentIngredientId\" value=\""+j+"\"/>";
								out += "<input type=\"hidden\" name=\"action\" value=\"08_editPresentIngredient\"/>";
								out += "</form>";
							}
							out += "</tr>\n";
						}
					}
				}
				
				if (this.AddPresentIngredients[1] == 1 && this.AddPresentIngredients[0] == i){
					out += this.getRowAddPresentIngredient(i);
				}
				out += "<br>";
				out += "</table></div>";
				out += "</td>";
				out += "</tr>";
			}
		}
		if (this.AddPresentFertiliser == true){
			out += this.getRowAddPresentFertiliser();
		}
		
		out += "</table></div>\n";
		return out;
	}
	
	/**
	 * Dies Funktion gibt die Zeile zurueck mit der eine neue Zutat hinzugefuegt werden kann.
	 * 
	 * @return Gibt die Zeile zum hinzufügen von Zutaten als String zurueck.
	 * 
	 * @autor: Eddi M.
	 */
	private String getRowAddPresentFertiliser(){
		String out = "";
		
		out += "<form type=\"hidden\" action=\"Controller\" method=\"post\" />";
		out += "<tr>";
		if (this.showID == true){
			
			out += "<td bgcolor=\"black\"></td>";
		}
		out += "<td bgcolor=\"black\"></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"fertiliserName\" placeholder=\"name\" autofocus/></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"fertiliserAmount\" placeholder=\"Amount\"/></td>";
		out += "<td><select class=\"form-control\" name=\"fertiliserUnit\" >";
		for( Units u : Units.values()){
			out += "<option>"+u.value()+"</option>";
		}
		out += "</select>";
		out += "<td>";
		out += this.getImageAndToolTip(Images.Done);
		out += "</td>";
		out += "<input type=\"hidden\" name=\"action\" value=\"42_addPresentFertiliser\"/>";
		out += "</tr>\n";
		out += "</form>";
		
		return out;
	}
	
	/**
	 * Dies Funktion gibt die Zeile zurueck mit der ein neuer PresentIngredient hinzugefuegt werden kann.
	 * 
	 * @param presentFertiliserId - Integer der Id des PresentFertiliser
	 * @return - String der Html Zeile
	 * @autor: Eddi M.
	 */
	private String getRowAddPresentIngredient(int presentFertiliserId){
		String out = "";
		
		out += "<form type=\"hidden\" action=\"Controller\" method=\"post\" />";
		out += "<tr>";
		if (this.showID == true){
			out += "<td bgcolor=\"black\"></td>";
			out += "<td bgcolor=\"black\"></td>";
		}else if (this.showID == false){
			out += "<td bgcolor=\"black\"></td>";
		}
		out += "<td><select class=\"form-control\" name=\"addPresentIngredientName\" autofocus >";
		for( int g=0; g< this.getTotalIngredients(); g++ ){
			if (this.modelData.getIngredients().getIngredient().get(g).isActive() == true){
				out += "<option>"+this.modelData.getIngredients().getIngredient().get(g).getName()+"</option>";
			}
			
		}
		out += "</select></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"addIngredientPercent\" /></td>";
		out += "<td>";
		out += this.getImageAndToolTip(Images.Done);
		out += "</td>";
		out += "<input type=\"hidden\" name=\"presetFertiliserId\" value=\""+presentFertiliserId+"\">";
		out += "<input type=\"hidden\" name=\"action\" value=\"05_addPresentIngredient\"/>";
		out += "</tr>\n";
		out += "</form>";
		
		return out;
	}
	
	/**
	 * Diese Funktion fuegt den button zum loeschen in die Tabelle ein.
	 * 
	 * @parm Integerwert der die ID des Ingredients wiedergibt.
	 * @return Es wird der Button als html String zurück gegeben
	 * @autor: Eddi M.
	 */
	private String getDeletePresentFertiliserButton(PresentFertiliser fertiliser){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		if (this.getShowDeletedPresentFertiliser() == true && fertiliser.isActive() == false){
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			out += this.getImageAndToolTip(Images.Add);
			out += "</td>";
			out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+fertiliser.getId()+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"50_undeletePresentFertilisers\" />";
			out += "</form>";
		}else{
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			if(right.equals("x") || right.equals("w")) {
				out += this.getImageAndToolTip(Images.Delete);
			}
			out += "</td>";
			out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+fertiliser.getId()+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"49_deletePresentFertilisers\" />";
			out += "</form>";
		}
		return out;
	}
	
	/**
	 * Diese Funktion zeigt je nach Status des PresentIngredients ein Button zum loeschen oder ein Button zum hinzufuegen.
	 * Des Weiteren haegt die Darstellung davon, ob die geloeschten PresentIngredients eingezeigt werden sollen.
	 * 
	 * @param presentFertiliserId - Integer
	 * @param ingredientIndex - Integer
	 * @return - String
	 * @autor: Eddi M.
	 */
	private String getDeletePresentIngredientButton(int presentFertiliserId, int ingredientIndex){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		PresentFertiliser presentFertiliser= this.modelData.getPresentFertiliser().get(presentFertiliserId);
		PresentIngredient presentIngredient = presentFertiliser.getPresentIngredients().getPresentIngredient().get(ingredientIndex);
		if (
			(this.ShowDeletedPresentIngredient[0] == presentFertiliserId && this.ShowDeletedPresentIngredient[1] == 1) && 
			presentIngredient.isActive() == false
			){
			
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			out += this.getImageAndToolTip(Images.Add);
			out += "</td>";
			out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+presentFertiliserId+"\"/>";
			out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+presentIngredient.getIngredientId()+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"31_undeletePresentIngredients\" />";
			out += "</form>";
		}else{
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			if(right.equals("x") || right.equals("w")) {
				out += this.getImageAndToolTip(Images.Delete);	
			}
			out += "</td>";
			out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+presentFertiliserId+"\"/>";
			out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+presentIngredient.getIngredientId()+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"28_deletePresentIngredient\" />";
			out += "</form>";
			
		}
		
		return out;
	}
	
	/**
	 * Berechnet die Summe der Prozente eines PresentFertilisers
	 * 
	 * @param presentFertiliserId
	 * @return - double
	 * 
	 * @autor: Eddi M.
	 */
	private double getTotalPercentOfPresentIngredients(int presentFertiliserId){
		double sum = 0.0;
		PresentFertiliser presentFertiliser = model.getPresentFertiliser(presentFertiliserId);
		
		for (int i=0; i< this.getTotalIngredients(); i++){
			if (this.modelData.getIngredients().getIngredient().get(i).isActive() == true && this.modelData.getIngredients().getIngredient().get(i).isActive() == true && model.getIdsOfPresentIngredientsFromPresentFertiliser(presentFertiliser).contains(i) == true){
				
				int index = model.getIndexOfPresentIngredientFromIngredientId(model.getPresentFertiliser(presentFertiliserId), i);
				PresentIngredient presentIngredient= model.getPresentIngredient(presentFertiliser, index);
				System.out.println("PresentFertiliserId " + presentFertiliserId + " PresentIngredientId: " + i + " index: " + index);
				if (presentIngredient.isActive() == false){
					this.setPresentIngredientActive(presentFertiliser, Integer.toString(i), true);
				}
				if (presentIngredient.isActive() == true && presentIngredient.isActive() == true){
					sum += presentIngredient.getPercent();
				}
			}
		}
		return this.runden(sum, 2);
	}
	
	/**
	 * liefert HTML String für benoetigte Duenger
	 * @return
	 */
	public String getBenoetigteDuengerTableau(){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		out += "<div class=\"container\">";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		if (this.showID == true){
			out += "<tr><th colspan=\"6\">";
		}else{
			out += "<tr><th colspan=\"5\">";
		}
		out += "<u>Benoetigter Duenger</u></th></tr>\n";
		out += "<tr>";
		if (this.showID == true){
			out += "<th>Id</th>";
		}
		out += "<th><form action=\"Controller\" method=\"post\" />";
		if (this.getShowDeletedRequiredFertiliser() == false && (right.equals("x") || right.equals("w"))){
			out += "deleted ";
			out += this.getImageAndToolTip(Images.Visible);
			out += "<input type=\"hidden\" name=\"action\" value=\"56_showDeletedRequiredFertilisers\"/>";
		}else if (this.getShowDeletedRequiredFertiliser() == true){
			out += "deleted ";
			out += this.getImageAndToolTip(Images.Invisible);
			out += "<input type=\"hidden\" name=\"action\" value=\"57_dontshowDeletedRequiredFertilisers\"/>";
		}
		
		out += "</form></th>";
		out += "<th>Name</th>";
		out += "<th>Bestand</th>";
		out += "<th>Einheit</th>";
		out += "<form action=\"Controller\" method=\"post\" />";
		out += "<td>";
		if (this.getAddRequiredFertiliser() == false){
			if(right.equals("x") || right.equals("w")) {
				out += this.getImageAndToolTip(Images.Add);	
			}
			out += "<input type=\"hidden\" name=\"action\" value=\"52_showRowAddingRequiredFertiliser\"/>";
		}else if (this.getAddRequiredFertiliser() == true){
			out += this.getImageAndToolTip(Images.Back);
			out += "<input type=\"hidden\" name=\"action\" value=\"53_NoShowRowAddingRequiredFertiliser\"/>";
		}
		out += "</td></tr>\n";
		out += "</form>";
		
		for (int i=0; i< this.getTotalRequiredFertilisers(); i++ ){
			RequiredFertiliser fertiliser = this.modelData.getRequiredFertiliser().get(i);
			System.out.println("RequiredFertiliser: " + i + " wird geladen Id: " + fertiliser.getId());
			
			if (fertiliser.isActive() == true || this.getShowDeletedRequiredFertiliser() == true){
			
				out += "<tr>";
				if (this.showID == true){
					out += "<td>"+fertiliser.getId()+"</td>";
				}
				out += this.getDeleteRequiredFertiliserButton(fertiliser);
				if (fertiliser.getId() == this.getChangeRequiredFeritliser() && (right.equals("x") || right.equals("w"))){
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "<td>"+ fertiliser.getName() +"</td>";
					out += "<td><input type=\"text\" class=\"form-control\" name=\"fertiliserAmount\" value=\""+fertiliser.getAmount()+ "\"/></td>";
					out += "<td><select class=\"form-control\" name=\"fertiliserUnit\" >";
					for( Units u : Units.values()){
						if (u.value() == fertiliser.getUnit().value()){
							out += "<option selected>"+u.value()+"</option>";
						}else{
							out += "<option>"+u.value()+"</option>";
						}
					}
					out += "</select>";
					out += "<td>";
					out += this.getImageAndToolTip(Images.Done);
					out += "</td>";
					out += "<input type=\"hidden\" name=\"fertiliserName\" value=\""+fertiliser.getName()+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"55_saveEditRequiredFertiliser\"/>";
				}else{
					out += "<td>"+ fertiliser.getName() +"</td>";
					out += "<td>"+ fertiliser.getAmount() +"</td>";
					out += "<td>"+ fertiliser.getUnit() +"</td>";
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "<td>";
					if (right.equals("x") || right.equals("w")){
						out += this.getImageAndToolTip(Images.Edit);
					}
					out += "</td>";
					out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+fertiliser.getId()+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"54_editRequiredFertiliser\"/>";
						
				}
				out += "</form>";
				out += "</tr>";
				
				out += "<tr>";
				if (this.showID == true){
					out += "<td colspan=\"6\">";
				}else{
					out += "<td colspan=\"5\">";
				}
				
				//Hier beginnt die Tabelle mit den PresentIngredients
				out += "<div class=\"container\">";
				out += "<table border=\"1\" class='table table-bordered' style=' text-align: center' >";
				if (this.showID == true){
					out += "<th>Id</th>";
				}
				out += "<th>";
				if (this.ShowDeletedRequiredIngredient[0] == fertiliser.getId() && this.ShowDeletedRequiredIngredient[1] == 1){
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "deleted ";
					out += this.getImageAndToolTip(Images.Invisible);
					out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ fertiliser.getId() +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"33_dontshowDeletedRequiredIngredients\"/>";
					out += "</form>";
				}else{
					out += "<form action=\"Controller\" method=\"post\" />";
					if(right.equals("x") || right.equals("w")) {
						out += "deleted ";
						out += this.getImageAndToolTip(Images.Visible);
					}
					out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ fertiliser.getId() +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"34_showDeletedRequiredIngredients\"/>";
					out += "</form>";
				}
				out += "</th>";
				out += "<th>Name</th>";
				out += "<th>min %</th>";
				out += "<th>max %</th>";
				out += "<th>";
				
				out += "<form action=\"Controller\" method=\"post\" />";
				if (this.AddRequiredIngredients[1] == 1 && this.AddRequiredIngredients[0] == fertiliser.getId()){
					out += this.getImageAndToolTip(Images.Back);
					out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ fertiliser.getId() +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"35_NoShowRowRequiredIngredientAdding\"/>";
				}else{
					if(right.equals("x") || right.equals("w")) {
						out += this.getImageAndToolTip(Images.Add);	
					}
					out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ fertiliser.getId() +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"36_showRowRequiredIngredientAdding\"/>";
				}
				out += "</form>";
				
				out += "</th>";
				out += "</tr>\n";
				for(int j=0; j< this.getTotalIngredients(); j++){
					if (this.modelData.getIngredients().getIngredient().get(j).isActive() == true &&
						model.getIdsOfRequiredIngredientsFromRequiredFertiliser(fertiliser).contains(j) == true){
						
						int index = model.getIndexOfRequiredIngredientFromIngredientId(fertiliser, j);
						RequiredIngredient requiredIngredient = fertiliser.getRequiredIngredients().getRequiredIngredient().get(index);
						System.out.println("Status von requiredIngredient: " + requiredIngredient.isActive());
						if (requiredIngredient.isActive() == true || 
								(this.ShowDeletedRequiredIngredient[0] == fertiliser.getId() && 
								this.ShowDeletedRequiredIngredient[1] == 1
								)
							){
						
							out += "<tr>";
							if (this.showID == true){
								out += "<td>"+requiredIngredient.getIngredientId()+"</td>";
							}
							out += this.getDeleteRequiredIngredientButton(fertiliser.getId(), index);
							out += "<td>"+this.modelData.getIngredients().getIngredient().get(requiredIngredient.getIngredientId()).getName()+"</td>";
							
							if (fertiliser.getId() == this.getChangeRequiredIngredientId()[0] && index == model.getIndexOfRequiredIngredientFromIngredientId(fertiliser, this.getChangeRequiredIngredientId()[1])){
								out += "<form action=\"Controller\" method=\"post\" />";
								out += "<td><input type=\"text\" class=\"form-control\" name=\"changeIngredientPercentMin\" value=\""+requiredIngredient.getPercentMin()+"\"/></td>";
								out += "<td><input type=\"text\" class=\"form-control\" name=\"changeIngredientPercentMax\" value=\""+requiredIngredient.getPercentMax()+"\"/></td>";
								out += "<td>";
								out += this.getImageAndToolTip(Images.Done);
								out += "</td>";
								out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+fertiliser.getId()+"\"/>";
								out += "<input type=\"hidden\" name=\"requiredIngredientId\" value=\""+requiredIngredient.getIngredientId()+"\"/>";
								out += "<input type=\"hidden\" name=\"action\" value=\"38_saveEditRequiredIngredient\"/>";
								out += "</form>";
							}else{
								out += "<td>"+requiredIngredient.getPercentMin()+"</td>";
								out += "<td>"+requiredIngredient.getPercentMax()+"</td>";
								out += "<form action=\"Controller\" method=\"post\" />";
								out += "</select>";
								out += "<td>";
								if(right.equals("x") || right.equals("w")) {
									out += this.getImageAndToolTip(Images.Edit);
								}
								out += "</td>";
								out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+fertiliser.getId()+"\"/>";
								out += "<input type=\"hidden\" name=\"requiredIngredientId\" value=\""+requiredIngredient.getIngredientId()+"\"/>";
								out += "<input type=\"hidden\" name=\"action\" value=\"39_editRequiredIngredient\"/>";
								out += "</form>";
							}
							out += "</tr>\n";
						}
					}
				}
				
				if (this.AddRequiredIngredients[1] == 1 && this.AddRequiredIngredients[0] == fertiliser.getId()){
					out += this.getRowAddRequiredIngredient(fertiliser.getId());
				}
				out += "<br>";
				out += "</table></div>";
				out += "</td>";
				out += "</tr>";
			}
		}
		if (this.AddRequiredFertiliser == true){
			out += this.getRowAddRequiredFertiliser();
		}
		
		out += "</table></div>\n";
		
		
		//____________________________________________
//		String out = "";
//		out += "<table border=\"1\">\n";
//		if (this.showID == true){
//			out += "<tr><td colspan=\"6\">";
//		}else{
//			out += "<tr><td colspan=\"5\">";
//		}
//		out += "Benoetigter Duenger</th></tr>\n";
//		
//		for (int i=0; i< this.getTotalRequiredFertilisers(); i++ ){
//			RequiredFertiliser fertiliser = this.modelData.getRequiredFertiliser().get(i);
//			if (this.showID == true){
//				out += "<tr><td colspan=\"6\">";
//				out += "<b>ID: </b>"+fertiliser.getId()+"<br/>";
//			}else{
//				out += "<tr><td colspan=\"5\">";
//			}
//			out += "<b>Name: </b>"+fertiliser.getName()+"<br/>";
//			out += "<b>Bestand: </b>"+fertiliser.getAmount()+"<br/>";
//			out += "<b>Einheit: </b>"+fertiliser.getUnit()+"<br/>";
//			out += "<b>Bestandteile:</b>";
//			out += "</td></tr>\n";
//			
//			out += "<tr>";
//			if (this.showID == true){
//				out += "<th>Id</th>";
//			}
//			out += "<th>";
//			if (this.ShowDeletedRequiredIngredient[0] == i && this.ShowDeletedRequiredIngredient[1] == 1){
//				out += "<form action=\"Controller\" method=\"post\" />";
//				out += "deleted ";
//				out += this.getImageAndToolTip(Images.Invisible);
//				out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ i +"\"/>";
//				out += "<input type=\"hidden\" name=\"action\" value=\"33_dontshowDeletedRequiredIngredients\"/>";
//				out += "</form>";
//			}else{
//				out += "<form action=\"Controller\" method=\"post\" />";
//				out += "deleted ";
//				out += this.getImageAndToolTip(Images.Visible);
//				out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ i +"\"/>";
//				out += "<input type=\"hidden\" name=\"action\" value=\"34_showDeletedRequiredIngredients\"/>";
//				out += "</form>";
//			}
//			out += "</th>";
//			out += "<th>Name</th><th>min %</th><th>max %</th>";
//			
//			out += "<th>";
//			out += "<form action=\"Controller\" method=\"post\" />";
//			if (this.AddRequiredIngredients[1] == 1 && this.AddRequiredIngredients[0] == i){
//				out += this.getImageAndToolTip(Images.Back);
//				out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ i +"\"/>";
//				out += "<input type=\"hidden\" name=\"action\" value=\"35_NoShowRowRequiredIngredientAdding\"/>";
//			}else{
//				out += this.getImageAndToolTip(Images.Add);
//				out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ i +"\"/>";
//				out += "<input type=\"hidden\" name=\"action\" value=\"36_showRowRequiredIngredientAdding\"/>";
//			}
//			out += "</form>";
//			out += "</th>";
//			out += "</tr>\n";
//			for(int j=0; j< this.getTotalIngredients(); j++){
//				
//				System.out.println("RequiredFertiliser: " + i + " RequiredIngredient: " + j);
//				if (this.modelData.getIngredients().getIngredient().get(j).isActive() == false){
//					this.setIngredientActive(Integer.toString(j), true);
//				}
//				System.out.println("RequiredIngredients of RequiredFertiliser: " + model.getIdsOfRequiredIngredientsFromRequiredFertiliser(fertiliser));
//				if (this.modelData.getIngredients().getIngredient().get(j).isActive() == true &&
//						this.modelData.getIngredients().getIngredient().get(j).isActive() == true &&
//						model.getIdsOfRequiredIngredientsFromRequiredFertiliser(fertiliser).contains(j) == true){
//					
//					int index = model.getIndexOfRequiredIngredientFromIngredientId(fertiliser, j);
//					RequiredIngredient requiredIngredient = fertiliser.getRequiredIngredients().getRequiredIngredient().get(index);
//					System.out.println("Status von requiredIngredient: " + requiredIngredient.isActive());
//					
//					if (requiredIngredient.isActive() == false){
//						this.setRequiredIngredientActive(fertiliser, Integer.toString(j), true);
//					}
//					if (requiredIngredient.isActive() == true && 
//							(requiredIngredient.isActive() == true || 
//								(this.ShowDeletedRequiredIngredient[0] == i && 
//								this.ShowDeletedRequiredIngredient[1] == 1
//								)
//							)
//						){
//						out += "<tr>";
//						if (this.showID == true){
//							out += "<td>"+requiredIngredient.getIngredientId()+"</td>";
//						}
//						out += this.getDeleteRequiredIngredientButton(i, index);
//						out += "<td>"+this.modelData.getIngredients().getIngredient().get(requiredIngredient.getIngredientId()).getName()+"</td>";
//						
//						if (i == this.getChangeRequiredFeritliser() && index == model.getIndexOfRequiredIngredientFromIngredientId(fertiliser, this.getChangeRequiredIngredientId())){
//							out += "<form action=\"Controller\" method=\"post\" />";
//							out += "<td><input type=\"text\" class=\"form-control\" name=\"changeIngredientPercentMin\" value=\""+requiredIngredient.getPercentMin()+"\"/></td>";
//							out += "<td><input type=\"text\" class=\"form-control\" name=\"changeIngredientPercentMax\" value=\""+requiredIngredient.getPercentMax()+"\"/></td>";
//							out += "<td>";
//							out += this.getImageAndToolTip(Images.Done);
//							out += "</td>";
//							out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+i+"\"/>";
//							out += "<input type=\"hidden\" name=\"requiredIngredientId\" value=\""+j+"\"/>";
//							out += "<input type=\"hidden\" name=\"action\" value=\"38_saveEditRequiredIngredient\"/>";
//							out += "</form>";
//						}else{
//							out += "<td>"+requiredIngredient.getPercentMin()+"</td>";
//							out += "<td>"+requiredIngredient.getPercentMax()+"</td>";
//							out += "<form action=\"Controller\" method=\"post\" />";
//							out += "</select>";
//							out += "<td>";
//							out += this.getImageAndToolTip(Images.Edit);
//							out += "</td>";
//							out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+i+"\"/>";
//							out += "<input type=\"hidden\" name=\"requiredIngredientId\" value=\""+j+"\"/>";
//							out += "<input type=\"hidden\" name=\"action\" value=\"39_editRequiredIngredient\"/>";
//							out += "</form>";
//						}
//						out += "</tr>\n";
//					}
//				}
//			}
//			
//			if (this.AddRequiredIngredients[1] == 1 && this.AddRequiredIngredients[0] == i){
//				out += this.getRowAddRequiredIngredient(i);
//			}
//			out += "<br>";
//		}
//		out += "</table>\n";
		return out;
	}
	
	/**
	 * Dies Funktion gibt die Zeile zurueck mit der eine neue Zutat hinzugefuegt werden kann.
	 * 
	 * @return Gibt die Zeile zum hinzufügen von Zutaten als String zurueck.
	 * 
	 * @autor: Eddi M.
	 */
	private String getRowAddRequiredFertiliser(){
		String out = "";
		
		out += "<form type=\"hidden\" action=\"Controller\" method=\"post\" />";
		out += "<tr>";
		if (this.showID == true){
			
			out += "<td bgcolor=\"black\"></td>";
		}
		out += "<td bgcolor=\"black\"></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"fertiliserName\" placeholder=\"name\" autofocus/></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"fertiliserAmount\" placeholder=\"Amount\"/></td>";
		out += "<td><select class=\"form-control\" name=\"fertiliserUnit\" >";
		for( Units u : Units.values()){
			out += "<option>"+u.value()+"</option>";
		}
		out += "</select>";
		out += "<td>";
		out += this.getImageAndToolTip(Images.Done);
		out += "</td>";
		out += "<input type=\"hidden\" name=\"action\" value=\"51_addRequiredFertiliser\"/>";
		out += "</tr>\n";
		out += "</form>";
		
		return out;
	}
	
	/**
	 * Dies Funktion gibt die Zeile zurück mit der ein neuer RequiredIngredient hinzugefügt werden kann.
	 * 
	 * @param requiredFertiliserId - Integer der Id des RequiredFertiliser
	 * @return - String der Html Zeile
	 * @autor: Eddi M.
	 */
	private String getRowAddRequiredIngredient(int requiredFertiliserId){
		String out = "";
		
		out += "<form type=\"hidden\" action=\"Controller\" method=\"post\" />";
		out += "<tr>";
		if (this.showID == true){
			out += "<td bgcolor=\"black\"></td>";
			out += "<td bgcolor=\"black\"></td>";
		}else if (this.showID == false){
			out += "<td bgcolor=\"black\"></td>";
		}
		out += "<td><select class=\"form-control\" name=\"addRequiredIngredientName\" autofocus >";
		for( int g=0; g< this.getTotalIngredients(); g++ ){
			if (this.modelData.getIngredients().getIngredient().get(g).isActive() == true){
				out += "<option>"+this.modelData.getIngredients().getIngredient().get(g).getName()+"</option>";
			}
		}
		out += "</select></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"addIngredientPercentMin\" /></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"addIngredientPercentMax\" /></td>";
		out += "<td>";
		out += this.getImageAndToolTip(Images.Done);
		out += "</td>";
		out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+requiredFertiliserId+"\">";
		out += "<input type=\"hidden\" name=\"action\" value=\"37_addRequiredIngredient\"/>";
		out += "</tr>\n";
		out += "</form>";
		
		return out;
	}
	
	/**
	 * Diese Funktion fuegt den button zum loeschen in die Tabelle ein.
	 * 
	 * @parm Integerwert der die ID des Ingredients wiedergibt.
	 * @return Es wird der Button als html String zurück gegeben
	 * @autor: Eddi M.
	 */
	private String getDeleteRequiredFertiliserButton(RequiredFertiliser fertiliser){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		if (this.getShowDeletedRequiredFertiliser() == true && fertiliser.isActive() == false){
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			out += this.getImageAndToolTip(Images.Add);
			out += "</td>";
			out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+fertiliser.getId()+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"59_undeleteRequiredFertilisers\" />";
			out += "</form>";
		}else{
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			if(right.equals("x") || right.equals("w")) {
				out += this.getImageAndToolTip(Images.Delete);
			}
			out += "</td>";
			out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+fertiliser.getId()+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"58_deleteRequiredFertilisers\" />";
			out += "</form>";
		}
		return out;
	}
		
	/**
	 * Diese Funktion zeigt je nach Status des RequiredIngredients ein Button zum loeschen oder ein Button zum hinzufuegen.
	 * Des Weiteren haegt die Darstellung davon, ob die geloeschten RequiredIngredients eingezeigt werden sollen.
	 * 
	 * @param requiredFertiliserId - Integer
	 * @param ingredientIndex - Integer
	 * @return - String
	 * @autor: Eddi M.
	 */
	private String getDeleteRequiredIngredientButton(int requiredFertiliserId, int ingredientIndex){
		String out = "";
		RequiredFertiliser requiredFertiliser= this.modelData.getRequiredFertiliser().get(requiredFertiliserId);
		RequiredIngredient requiredIngredient = requiredFertiliser.getRequiredIngredients().getRequiredIngredient().get(ingredientIndex);
		if (
			(this.ShowDeletedRequiredIngredient[0] == requiredFertiliserId && this.ShowDeletedRequiredIngredient[1] == 1) && 
			requiredIngredient.isActive() == false
			){
			
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			out += this.getImageAndToolTip(Images.Add);
			out += "</td>";
			out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+requiredFertiliserId+"\"/>";
			out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+requiredIngredient.getIngredientId()+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"41_undeleteRequiredIngredients\" />";
			out += "</form>";
		}else{
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			out += this.getImageAndToolTip(Images.Delete);
			out += "</td>";
			out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+requiredFertiliserId+"\"/>";
			out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+requiredIngredient.getIngredientId()+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"40_deleteRequiredIngredient\" />";
			out += "</form>";
			
		}
		
		return out;
		
	}
	
	/**
	 * Liefert die HtmlCode fuer die SolutionTable
	 * 
	 * @return - String
	 * @autor: Eddi M.
	 */
	public String getSolutionTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"3\">Loesung</th></tr>\n";
		out += "<tr><th align=\"left\" colspan=\"2\">Solver Status: </th><td colspan=\"1\">"+this.modelData.getSolverStatus()+"</td></tr>\n";
		
		if(this.isSolved()){
			for (int i=0; i< this.getTotalSolutions(); i++ ){
				Solution solution = this.modelData.getSolution().get(i);
				out += "<tr><th align=\"left\" colspan=\"2\">Solution Id: </th><td colspan=\"2\">"+i+"</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"2\">Optimal: </th><td colspan=\"2\">"+solution.isOptimal()+"</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"2\">Status: </th><td colspan=\"2\">"+solution.getStatus()+"</td></tr>\n";
				out += "<tr><th align=\"left\" colspan=\"2\">Datum der Loesung: </th><td colspan=\"2\">"+solution.getSolvedAt()+"</td></tr>\n";
				if(solution.isOptimal()){
					out += "<tr><th align=\"left\" colspan=\"2\">Cost:</th><td colspan=\"1\">"+solution.getCost()+"</td><td colspan=\"1\">&euro;</td></tr>\n";
					out += "<tr><th align=\"left\" colspan=\"4\">Produktions-Programm:</th></tr>\n";
					out += "<tr><th colspan=\"1\">Id</th><th colspan=\"1\">Name</th><th colspan=\"2\">Menge</th></tr>\n";
					for(int j=0; j< solution.getProduction().size(); j++){
						out += "<tr>";
						Production production 				= solution.getProduction().get(j);
						int requiredFertilserId 			= production.getRequiredFertillserId();
						List<UsedFertiliser> usedFertilsers = production.getUsedFertiliser();
						List<UsedIngredient> usedIngredients = production.getUsedIngredient();
						out += "<td colspan=\"1\">"+requiredFertilserId+"</td>";
						out += "<td colspan=\"1\">"+this.modelData.getRequiredFertiliser().get(requiredFertilserId).getName()+"</td>";
						out += "<td colspan=\"1\">"+solution.getProduction().get(j).getAmount()+"</td>";
						out += "<td colspan=\"1\">"+this.modelData.getRequiredFertiliser().get(requiredFertilserId).getUnit().name()+"</td>";
						out += "</tr>\n";
						
						out += "<tr>";
						out += "<td colspan=\"1\"></td>";
						out += "<td colspan=\"3\">Vorhandene Duenger:</td>";
						out += "</tr>\n";
						for(int k=0; k<usedFertilsers.size(); k++){
							int usedFertilsersId = usedFertilsers.get(k).getPresentFertillserIdId();
							out += "<tr>";
							out += "<td colspan=\"1\"></td>";
							out += "<td colspan=\"1\"><i>"+this.modelData.getPresentFertiliser().get(usedFertilsersId).getName()+"</i></td>";
							out += "<td colspan=\"1\"><i>"+usedFertilsers.get(k).getQuantity()+"</i></td>";
							out += "<td colspan=\"1\"><i>"+this.modelData.getPresentFertiliser().get(usedFertilsersId).getUnit().name()+"</i></td>";
							out += "</tr>\n";
						}

						out += "<tr>";
						out += "<td colspan=\"1\"></td>";
						out += "<td colspan=\"3\">zusaetzliche Zutaten:</td>";
						out += "</tr>\n";
						for(int k=0; k<usedIngredients.size(); k++){
							int usedIngredientsId = usedIngredients.get(k).getIngredientIdId();
							out += "<tr>";
							out += "<td colspan=\"1\"></td>";
							out += "<td colspan=\"1\"><i>"+this.modelData.getIngredients().getIngredient().get(usedIngredientsId).getName()+"</i></td>";
							out += "<td colspan=\"1\"><i>"+usedIngredients.get(k).getQuantity()+"</i></td>";
							out += "<td colspan=\"1\"><i>"+this.modelData.getIngredients().getIngredient().get(usedIngredientsId).getUnit().name()+"</i></td>";
							out += "</tr>\n";
						}
					}
				}
			}
		}
		out += "</table>\n";
		return out;
	}
	
	/**
	 * Setzt den status des Ingredients auf den boolischen Wert b.
	 * 
	 * @param ingredientId - String
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setIngredientActive(String ingredientId, boolean b){
		int id = Integer.parseInt(ingredientId);
		model.setIngredientActive(model.getIngredient(id), b);
	}
	
	/**
	 * Setzt den status des PresentFertilisers auf den boolischen Wert b.
	 * 
	 * @param fertiliserId - String
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setPresentFertiliserActive(String fertiliserId, boolean b){
		int id = Integer.parseInt(fertiliserId);
		model.setPresentFertiliserActive(model.getPresentFertiliser(id), b);
	}
	
	/**
	 * Setzt den status des RequiredFertilisers auf den boolischen Wert b.
	 * 
	 * @param fertiliserId - String
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setRequiredFertiliserActive(String fertiliserId, boolean b){
		int id = Integer.parseInt(fertiliserId);
		model.setRequiredFertiliserActive(model.getRequiredFertiliser(id), b);
	}
	
	/**
	 * Setzt den Status des PresentIngredients auf den boolischen Wert b.
	 * Es muss die IngredientId und der PresentFertiliser übergeben werden.
	 * 
	 * @param presentFertiliser - PresentFertiliser
	 * @param strPresentIngredientId - String
	 * @param b - boolean
	 * 
	 * @autor: Eddi M.
	 */
	private void setPresentIngredientActive(PresentFertiliser presentFertiliser, String strPresentIngredientId, boolean b){
		int presentIngredientId = Integer.parseInt(strPresentIngredientId);
		
		this.setPresentIngredientActive(presentFertiliser, presentIngredientId, b);
	}
	
	/**
	 * Setzt den Status des PresentIngredients auf den boolischen Wert b.
	 * Es muss die IngredientId und der PresentFertiliser übergeben werden.
	 * 
	 * @param presentFertiliser - String
	 * @param strPresentIngredientId - String
	 * @param b - boolean
	 * 
	 * @autor: Eddi M.
	 */
	public void setPresentIngredientActive(String strPresentFertiliserId, String strPresentIngredientId, boolean b){
		int presentFertiliserId = Integer.parseInt(strPresentFertiliserId);
		int presentIngredientId = Integer.parseInt(strPresentIngredientId);
		PresentFertiliser presentFertiliser = model.getPresentFertiliser(presentFertiliserId);
		
		this.setPresentIngredientActive(presentFertiliser, presentIngredientId, b);
	}
	
	/**
	 * Setzt den Status des PresentIngredients auf den boolischen Wert b.
	 * Es muss die IngredientId und der PresentFertiliser übergeben werden.
	 * 
	 * @param presentFertiliser - PresentFertiliser
	 * @param strPresentIngredientId - Integer
	 * @param b - boolean
	 * @autor: Eddi M.
	 */
	private void setPresentIngredientActive(PresentFertiliser presentFertiliser, int presentIngredientId, boolean b){
		int index = model.getIndexOfPresentIngredientFromIngredientId(presentFertiliser, presentIngredientId);
		PresentIngredient presentIngredient = presentFertiliser.getPresentIngredients().getPresentIngredient().get(index);
		
		model.setPresentIngredientActive(presentIngredient, b);
	}
	
	/**
	 * Setzt den Status des RequiredIngredients auf den boolischen Wert b.
	 * Es muss die IngredientId und der RequiredFertiliser übergeben werden.
	 * 
	 * @param requiredFertiliser - RequiredFertiliser
	 * @param strRequiredIngredientId - String
	 * @param b - boolean
	 * @autor: Eddi M.
	 */
	private void setRequiredIngredientActive(RequiredFertiliser requiredFertiliser, String strRequiredIngredientId, boolean b){
		int requiredIngredientId = Integer.parseInt(strRequiredIngredientId);
		
		this.setRequiredIngredientActive(requiredFertiliser, requiredIngredientId, b);
	}
	
	/**
	 * Setzt den Status des RequiredIngredients auf den boolischen Wert b.
	 * Es muss die IngredientId und der RequiredFertiliser übergeben werden.
	 * 
	 * @param requiredFertiliser - String
	 * @param strRequiredIngredientId - String
	 * @param b - boolean
	 * @autor: Eddi M.
	 */
	public void setRequiredIngredientActive(String strRequiredFertiliserId, String strRequiredIngredientId, boolean b){
		int requiredFertiliserId = Integer.parseInt(strRequiredFertiliserId);
		int requiredIngredientId = Integer.parseInt(strRequiredIngredientId);
		RequiredFertiliser requiredFertiliser = model.getRequiredFertiliser(requiredFertiliserId);
		
		this.setRequiredIngredientActive(requiredFertiliser, requiredIngredientId, b);
	}
	
	/**
	 * Setzt den Status des RequiredIngredients auf den boolischen Wert b.
	 * Es muss die IngredientId und der RequiredFertiliser übergeben werden.
	 * 
	 * @param requiredFertiliser - RequiredFertiliser
	 * @param strRequiredIngredientId - Integer
	 * @param b - boolean
	 * @autor: Eddi M.
	 */
	private void setRequiredIngredientActive(RequiredFertiliser requiredFertiliser, int requiredIngredientId, boolean b){
		int index = model.getIndexOfRequiredIngredientFromIngredientId(requiredFertiliser, requiredIngredientId);
		RequiredIngredient requiredIngredient = requiredFertiliser.getRequiredIngredients().getRequiredIngredient().get(index);
		
		model.setRequiredIngredientActive(requiredIngredient, b);
	}
	
	public String getHiddenModdelId(){
		String out = "<input type=\"hidden\" name=\"modelId\" value=\""+this.modelData.getId()+"\">";
		return out;
	}
	
	
	/**
	 * lieftert den HTML String zur Modell-übersicht
	 * @return
	 */
	public String getModelsOverview(){
		String[] ids 	= Model.getModelIds();
		String out 		= "";
		out += "<div class=\"container\">";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		out += "<tr><th>ID</th><th>Name</th><th colspan=\"3\">Model Management</th></tr>\n";
		for(int i=0; i<ids.length; i++){
			String right 	= GetUserRightInModel(ids[i],getNutzer());
			if(!GetUserInModel(ids[i],getNutzer())) {
				out += "<!---";
			}
			String refModel = "Controller?action=03_showModel&modelId="+ids[i];
			out += "<tr><td>"+ids[i]+"</td>";
			out += "<td><button class='btn btn-xs btn-warning' onclick='window.location.href=\""+refModel+"\"'>"+Model.get(ids[i]).getModelData().getName()+"</button></td>";
			refModel = "Controller?action=15_removeModel&modelId="+ids[i];
			if(right.equals("x")) {
				out += "<td><button class='btn btn-xs btn-danger' onclick='window.location.href=\""+refModel+"\"'>Delete</button></td>\n";
			}else {
				out += "<td><button class='btn btn-xs btn-danger' onclick='window.location.href=\""+refModel+"\"' disabled>Delete</button></td>\n";
			}
			refModel = "Controller?action=11_saveModel&modelId="+ids[i];
			out += "<td><button class='btn btn-xs btn-success' onclick='window.location.href=\""+refModel+"\"'>Save</button></td></tr>\n";
			if(!GetUserInModel(ids[i],nutzer)) {
				out += "-->";
			}
		}
		out += "</table>\n";
		out += "</div>";
		return out;
	}
	
	/**
	 * liefert den HTML String zum Hinzufügen eines Modells
	 * 
	 * @return
	 */
	public String getModelAddView(){
		String out = "";
		out += "<div class=\"container\">";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		//out += "<tr><th colspan=\"4\">Add a Model</th></tr>";
		out += "<tr><th>Model ID</th><th>Model Name</th></tr>";
		out += "<tr>";
		out += "<td><input type=\"text\"  class=\"form-control\" name=\"modelId\" size=\"10\" ></td>";
		out += "<td><input type=\"text\" class=\"form-control\" name=\"modelName\" size=\"10\" ></td>";
		out += "</tr>";
		out	+= "</table>\n";
		out += "</div>";
		return out;
	}
	
	public void resetSolution(){
		this.modelData.getSolution().clear();
		this.modelData.setSolved(false);
		this.modelData.setSolverStatus("unsolved");
	}
	
	/**
	 * speichert das Model als xml Datei
	 * 
	 */
	public void save(){
		//System.out.println("call save");
		model.printDoc();
	}
	
	/**
	 * löst das Modell
	 * Dazu werden die Modelldaten an den Solver übergeben.
	 * Nach der Lösung werden die Lösungsdaten in die Klasse data.model
	 * übertragen
	 */
	public void solve(){
		
		ObjectFactory	factory = new ObjectFactory();

		boolean ok = false;
		Cmpl m;
		CmplSet supplySet, demandSet,ingredientSet;
		CmplParameter purchase_price, stock, demand, a, aMin, aMax; 
		
//		int anzahlSichtbarenPresentFertiliser = 0;
//		for(int i=0; i<this.getTotalPresentFertilisers(); i++){
//			if ( this.modelData.getPresentFertiliser().get(i).isActive() == true){
//				anzahlSichtbarenPresentFertiliser ++;
//			}
//		}
//		System.out.println("AnzahlDerSichtbarenPresentFertiliser: " + anzahlSichtbarenPresentFertiliser);
//		String[] supplyName 		= new String[anzahlSichtbarenPresentFertiliser];
//		double[] stockVal 			= new double[anzahlSichtbarenPresentFertiliser];
		
		String[] supplyName 		= new String[this.getTotalPresentFertilisers()];
		double[] stockVal 			= new double[this.getTotalPresentFertilisers()];
		
//		int gesetztePresentFertiliser = 0;
		
		for(int i=0; i<this.getTotalPresentFertilisers(); i++){
//			while (gesetztePresentFertiliser<anzahlSichtbarenPresentFertiliser){
				System.out.println("Die while funktion wird ausgefuert.");
//				if (modelData.getIngredients().getIngredient().get(i).isActive() == true){
//					gesetztePresentFertiliser++;
//					System.out.println("gesetztePresentFertiliser: " + gesetztePresentFertiliser);
					supplyName[i]			= this.modelData.getPresentFertiliser().get(i).getName();
					stockVal[i]				= this.modelData.getPresentFertiliser().get(i).getAmount();
//				}
//			}
		}
		

		String[] demandName 		= new String[this.getTotalRequiredFertilisers()];
		double[] demandVal 			= new double[this.getTotalRequiredFertilisers()];
		for(int i=0; i<this.getTotalRequiredFertilisers(); i++){
			demandName[i]			= this.modelData.getRequiredFertiliser().get(i).getName();
			demandVal[i]			= this.modelData.getRequiredFertiliser().get(i).getAmount();
		}
		
		int sichtbareIngredients = 0;
		for (int i=0; i<this.getTotalIngredients(); i++){
			Ingredient ingredient = this.model.getIngredient(i);
			if (ingredient.isActive() == true){
				sichtbareIngredients++;
			}
		}
		System.out.println("sichtbareIngredients: " + sichtbareIngredients);
		
		String[] ingredientName 	= new String[sichtbareIngredients];
		double[] purchase_priceVal 	= new double[sichtbareIngredients];
		
		int gesetzteIngredients = 0, t = 0;
		
		while (t<this.getTotalIngredients() && gesetzteIngredients < sichtbareIngredients){
			System.out.println("Ingredient: " + t + " wurde hinzugefuegt.");
			
			ingredientName[t]		= this.modelData.getIngredients().getIngredient().get(t).getName();
			purchase_priceVal[t]	= this.modelData.getIngredients().getIngredient().get(t).getPrice();
			gesetzteIngredients++;
			t++;
		}
		System.out.println("gesetzteIngredients: " + gesetzteIngredients);
		
		double[][] aVal 			= new double[this.getTotalIngredients()][this.getTotalPresentFertilisers()];
		for(int j=0; j<this.getTotalPresentFertilisers(); j++){
			PresentFertiliser presentFertilizer = this.modelData.getPresentFertiliser().get(j);
			for(int i=0; i<this.getTotalIngredients(); i++) aVal[i][j] = 0.0;
			for(int i=0; i<presentFertilizer.getPresentIngredients().getPresentIngredient().size(); i++){
				PresentIngredient presentIngredient = presentFertilizer.getPresentIngredients().getPresentIngredient().get(i);
				int ingredientIndex = presentIngredient.getIngredientId();
				aVal[ingredientIndex][j]	= presentIngredient.getPercent()/100.0;
			}
		}

		double[][] aMinVal 			= new double[this.getTotalIngredients()][this.getTotalRequiredFertilisers()];
		double[][] aMaxVal 			= new double[this.getTotalIngredients()][this.getTotalRequiredFertilisers()];
		for(int j=0; j<this.getTotalRequiredFertilisers(); j++){
			RequiredFertiliser requiredFertilizer = this.modelData.getRequiredFertiliser().get(j);
			for(int i=0; i<this.getTotalIngredients(); i++) {aMinVal[i][j]	= 0.0; aMaxVal[i][j]	= 0.0; }
			for(int i=0; i< requiredFertilizer.getRequiredIngredients().getRequiredIngredient().size(); i++){
				RequiredIngredient requiredIngredient = requiredFertilizer.getRequiredIngredients().getRequiredIngredient().get(i);
				int requiredIndex = requiredIngredient.getIngredientId();
				aMinVal[requiredIndex][j]	= requiredIngredient.getPercentMin()/100.0;
				aMaxVal[requiredIndex][j]	= requiredIngredient.getPercentMax()/100.0;
			}
		}

		try{
			m 		= new Cmpl(EDbean.CmplModel);
			
			supplySet	= new CmplSet("SUPPLY");
			supplySet.setValues(supplyName);
			
			demandSet	= new CmplSet("DEMAND");
			demandSet.setValues(demandName);
			
			ingredientSet	= new CmplSet("INGREDIENT");
			ingredientSet.setValues(ingredientName);
			
			purchase_price 	= new CmplParameter("purchase_price", ingredientSet);
			purchase_price.setValues(purchase_priceVal);
			
			stock 	= new CmplParameter("stock", supplySet);
			stock.setValues(stockVal);
			
			demand 	= new CmplParameter("demand", demandSet);
			demand.setValues(demandVal);
			
			a 	= new CmplParameter("a", ingredientSet, supplySet);
			a.setValues(aVal);
			
			aMin 	= new CmplParameter("aMin", ingredientSet, demandSet);
			aMin.setValues(aMinVal);
			
			aMax 	= new CmplParameter("aMax", ingredientSet, demandSet);
			aMax.setValues(aMaxVal);
			
			m.setSets(supplySet, demandSet, ingredientSet);
			m.setParameters(purchase_price, stock, demand, a, aMin, aMax);
			
			
			m.connect(EDbean.WebService);
			//m.debug(true);
			//m.setOutput(true);
			m.solve();
			//System.out.println("SolverStatus: "+m.solverStatus());
			if (m.solverStatus()==Cmpl.SOLVER_OK && m.nrOfSolutions()>0) {
				//System.out.println("No of Solutions: "+m.nrOfSolutions()+"   "+m.solutionPool().size());
				ok = true;
				this.modelData.setSolved(ok);
				this.modelData.setSolverStatus(m.solverStatusText());
				this.modelData.getSolution().clear();
				
				CmplSolution cmplsolution = m.solution();
				Solution solution = factory.createSolution();
				solution.setSolved(ok);
				solution.setSolvedAt(this.getModel().getTime());
				solution.setOptimal(cmplsolution.status().equals("optimal"));
				solution.setStatus(cmplsolution.status());
				solution.setCost(cmplsolution.value());
				CmplSolArray x 		= (CmplSolArray) m.getVarByName("x"); 
				CmplSolArray y 		= (CmplSolArray) m.getVarByName("y"); 
				CmplSolArray z 		= (CmplSolArray) m.getVarByName("z"); 
				
				String[] index = new String[2];
				for(int j=0; j<this.getTotalRequiredFertilisers(); j++){
					index[1] = (String)demandSet.get(j);
					Production production = factory.createProduction();
					production.setRequiredFertillserId(j);
					production.setAmount((Double)(z.get(index[1]).activity()));
					for(int i=0; i<this.getTotalPresentFertilisers(); i++){
						index[0] = (String)supplySet.get(i);
						UsedFertiliser usedFertilizer = factory.createUsedFertiliser();
						usedFertilizer.setPresentFertillserIdId(i);
						usedFertilizer.setQuantity((Double)(x.get(index).activity()));
						production.getUsedFertiliser().add(usedFertilizer);
					}
					for(int i=0; i<this.getTotalIngredients(); i++){
						index[0] = (String)ingredientSet.get(i);
						UsedIngredient usedIngredient = factory.createUsedIngredient();
						usedIngredient.setIngredientIdId(i);
						usedIngredient.setQuantity((Double)(y.get(index).activity()));
						production.getUsedIngredient().add(usedIngredient);
					}
					
					solution.getProduction().add(production);
				}
				this.modelData.getSolution().add(solution);

				/*
				System.out.print(m.solution().status()+"\t");
				System.out.println(m.solution().value());
				for(int i=0; i<m.nrOfVariables(); i++){
					System.out.print(m.solution().variables().get(i).name()+"\t");
					System.out.println(m.solution().variables().get(i).activity());
				}
				*/
			}else{
				ok = false; 
				this.modelData.setSolved(ok);
				this.modelData.setSolverStatus(m.solverStatusText());
				System.out.println("ErrorStatus: "+m.solverStatusText());
			}
		}catch(CmplException e){
			ok = false; 
			System.out.println("CmplException  "+e.toString());
		}

	}

	
	/**
	 * fügt ein neues Modell hinzu
	 * 
	 * @param modelId
	 * @param ModelName
	 * @param nrVariables
	 * @param nrConstraints
	 */
	public void addModel(String modelId, String ModelName){
		int anzVariables, anzConstraints;
		try{
			Model model = new Model(modelId);
			model.getModelData().setName(ModelName);
			Model.add(modelId, model);
		}catch(java.lang.NumberFormatException e){
			logger.info("Fehler Modell konnte nicht angelegt werden");
		}
	}
	
	/**
	 * entfernt ein Modell
	 * 
	 * @param modelId
	 */
	public void removeModel(String modelId){
		Model.remove(modelId);
	}
	
	private static boolean checkWebService(){
		boolean ok = true;
		try{
			Cmpl m = new Cmpl(EDbean.CmplModel);
			m.connect(EDbean.WebService);
		}catch(CmplException e){
			ok = false;
			logger.error("Cmplservice nicht erreichbar "+EDbean.WebService);
		}
		return ok;

	}
	
	/*
	 * Nutzermanagement
	 */
	
	/*
	 * Ausgabe aller Nutzer von dem aktiven Model
	 * 
	 * @author: Jonas G.
	 */
	public String getUserOverview() {
		String out = "";
		String modelId = model.getId();
		
		String rechteName	= "";
		out += "<div class=\"container\">";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		out += "<thead class='thead-light'><tr><td colspan='3'>Rechte¸bersicht</td></tr></thead>";
		out += "<tr><th>Name</th><th>Rechte</th><th>Action</th></tr>\n";
		if(checkIfModelIsInFile(modelId)) {
			String[] FileLineArray 	= allLineUserFile();
			String lineModel		= FileLineArray[linefile].split("/")[1];
			String[] lineModelUser	= lineModel.split(";");
			for(int x = 0; x < lineModelUser.length;x++) {
				String[] lineUser = lineModelUser[x].split("-");
				if(lineUser[1].equals("r")) {
					rechteName	= "Nur Lesen";
				}else if(lineUser[1].equals("w")){
					rechteName	= "Lesen und Schreiben";
				}else if(lineUser[1].equals("x")) {
					rechteName	= "Admin";
				}
				out += "<form action='Controller' method='POST'>";
				out += "<input type='hidden' name='modelId' value='"+modelId+"'>";
				out += "<input type='hidden' name='user' value='"+lineUser[0]+"'>";
				out += "<input type='hidden' name='action' value='30_DeleteUserModel'>";
				out += "<tr><td>"+lineUser[0]+"</td><td>"+rechteName+"</td><td><input type=\"submit\" class=\"btn btn-danger\" value=\"Lˆschen\" /></td></tr>";
				out += "</form>";
			}
		}
		out += "</table></div></br>";
		return out;
	}
	
	/*
	 * Speicher ein neues Model in die User Datei
	 * 
	 * @param modelId Model ID
	 * @param nutzer Name des Benutzers
	 * @param rechte Rechte des jeweiligen Benutzers
	 * 
	 * @author Jonas G.
	 */
	
	public void saveRightModelNew(String modelId, String nutzer, String rechte) {
		String[] modelFile			= allLineUserFile();
		String str					= modelId+"/"+nutzer+"-"+rechte+";";
		BufferedWriter outputWriter = null;
		  try {
			outputWriter = new BufferedWriter(new FileWriter(filepath));
			for (int i = 0; i < modelFile.length; i++) {
			    // Maybe:
			    outputWriter.write(modelFile[i]);
			    outputWriter.newLine();
			  }
			  outputWriter.write(str);
			  outputWriter.flush();  
			  outputWriter.close();  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Speichert das Model in der User Datei
	 * 
	 * @param modelId Model ID
	 * @param nutzer Name des Benutzers
	 * @param rechte Rechte des jeweiligen Benutzers
	 * 
	 * @author Jonas G.
	 * 
	 */
	
	public void saveRightModel(String modelId, String nutzer, String rechte) {
		System.out.println("saveRightModel ("+modelId+" - "+nutzer+" - "+rechte+")");
		String modelLine	= writeModelRightLine(modelId, nutzer, rechte);
		String[] modelFile	= getLineArray();
		if(rightcheck) {
			modelFile[linefile]	= modelLine;
		}
		BufferedWriter outputWriter = null;
		  try {
			outputWriter = new BufferedWriter(new FileWriter(filepath));
			for (int i = 0; i < modelFile.length; i++) {
			    // Maybe:
			    outputWriter.write(modelFile[i]);
			    outputWriter.newLine();
			  }
			  outputWriter.flush();  
			  outputWriter.close();  
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	/*
	 * Schreibt die Zeile in der Datei fuer das jeweilige Model
	 * 
	 * @param modelId Model ID
	 * @param nutzer Name des Benutzers
	 * @param rechte Rechte des jeweiligen Benutzers
	 * 
	 * @author Jonas G.
	 */
	
	private String writeModelRightLine(String modelId, String nutzer, String rechte) {
		String lineModelUserRights	= modelId+"/";
		boolean checkupdate			= false;
		if(f.exists() && !f.isDirectory() && checkIfModelIsInFile(modelId) && GetUserRightInModel(modelId, getNutzer()).equals("x")) {
			String[] FileLineArray 	= allLineUserFile();
			String lineModel		= FileLineArray[linefile].split("/")[1];
			//System.out.println(lineModel);
			String[] lineModelUser	= lineModel.split(";");
			for(int x = 0; x < lineModelUser.length;x++) {
				String[] lineUserRight = lineModelUser[x].split("-");
				if(lineUserRight[0].equalsIgnoreCase(nutzer)) {
					lineModelUserRights += nutzer+"-"+rechte+";";
					checkupdate			= true;
				}else {
					lineModelUserRights += lineModelUser[x]+";";
				}
				System.out.println(lineUserRight[0]);
				System.out.println(lineModelUserRights);
			}
			if(!checkupdate) {
				lineModelUserRights += nutzer+"-"+rechte+";";
			}
			rightcheck = true;
		}else{
			logger.error("Error at writeModelRightLine");
		}
		return lineModelUserRights;
	}
	
	/*
	 * Ueberprueft ob der Benutzer in der Zeile vom Model liegt
	 * 
	 * @param modelId Model ID
	 * @param nutzer Nutzername
	 * 
	 * @author Jonas G.
	 */
	
	public boolean GetUserInModel(String modelId, String nutzer) {
		boolean checker	= false;
		if(checkIfModelIsInFile(modelId)) {
			String[] FileLineArray 	= allLineUserFile();
			String lineModel		= FileLineArray[linefile].split("/")[1];
			String[] lineModelUser	= lineModel.split(";");
			for(int x = 0; x < lineModelUser.length;x++) {
				String[] lineUser = lineModelUser[x].split("-");
				//System.out.println(lineUser[1]);
				if(lineUser[0].equalsIgnoreCase(nutzer)) {
					checker = true;
				}
			}
		}
		//System.out.println("GetUserInModel("+modelId+" - "+nutzer+"): " + checker);
		return checker;
	}
	
	/*
	 * Gibt die Rechte eines Benutzer aus dem Model zurueck
	 * 
	 * @param modelId Model ID
	 * @param nutzer Name des Benutzers
	 * 
	 * @author Jonas G.
	 */
	
	public String GetUserRightInModel(String modelId, String nutzer) {
		String userRight		= "";
		if(checkIfModelIsInFile(modelId)) {
			String[] FileLineArray 	= allLineUserFile();
			String lineModel		= FileLineArray[linefile].split("/")[1];
			String[] lineModelUser	= lineModel.split(";");
			for(int x = 0; x < lineModelUser.length;x++) {
				String[] lineUser = lineModelUser[x].split("-");
				if(lineUser[0].equalsIgnoreCase(nutzer)) {
					userRight = lineUser[1];
				}
			}
		}
		//System.out.println("GetUserRightInModel("+modelId+" - "+nutzer+"): " + userRight);
		return userRight;
	}
	
	/*
	 * Ueberprueft ob das Model in der Benutzerdatei exestiert
	 * 
	 * @param modelId Model ID
	 * 
	 * @author Jonas G.
	 */
	
	private boolean checkIfModelIsInFile(String modelId) {
		boolean checker	= false;
		String[] FileLineArray = allLineUserFile();
		String[] LineSplitter;
		for(int x = 0; x < FileLineArray.length;x++) {
			LineSplitter = FileLineArray[x].split("/");
			if(LineSplitter[0].equalsIgnoreCase(modelId)) {
				checker = true;
				linefile=x;
			}
		}
		//System.out.println("checkIfModelIsInFile("+modelId+"): " + checker);
		return checker;
	}
	
	public String[] getLineArray() {
		return LineArray;
	}

	public void setLineArray(String[] lineArray) {
		LineArray = lineArray;
	}
	
	/*
	 * Gibt die User Datei als String Array zurueck
	 * 
	 * @return Alle Zeilen des Files als String Array
	 */

	private String[] allLineUserFile() {
		int linenumber 		= 0;
		String[] FileArray 	= null;
		try{
    		if(f.exists()){
    		    FileReader fr = new FileReader(f);
    		    LineNumberReader lnr = new LineNumberReader(fr);
    		    
	            while (lnr.readLine() != null){
	            	linenumber++;
	            }
    	        lnr.close();
    		}else{
    			logger.error("User Model File does not exist");
    		}

			FileArray = new String[linenumber];
			String line;
			int counter = 0;
			
			FileReader fileReader = new FileReader(f);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				FileArray[counter] = line;
				counter++;
			}
			bufferedReader.close();
			fileReader.close();
		}catch(IOException e){
    		e.printStackTrace();
    	}
		setLineArray(FileArray);
		return FileArray;
	}
	
	/*
	 * Loescht ein User auf dem Modell
	 * 
	 * @param modelId Model ID
	 * @param nutzer Benutzer
	 */
	
	public void deleteUserOfModel(String modelId, String nutzer) {
		String lineModelUserRights	= modelId+"/";
		
		if(checkIfModelIsInFile(modelId) && GetUserRightInModel(modelId, getNutzer()).equals("x") && GetUserInModel(modelId, nutzer)) {
			String[] FileLineArray 	= allLineUserFile();
			String lineModel		= FileLineArray[linefile].split("/")[1];
			//System.out.println(lineModel);
			String[] lineModelUser	= lineModel.split(";");
			for(int x = 0; x < lineModelUser.length;x++) {
				String[] lineUserRight = lineModelUser[x].split("-");
				if(lineUserRight[0].equalsIgnoreCase(nutzer)) {

				}else {
					lineModelUserRights += lineModelUser[x]+";";
				}
			}
			rightcheck = true;
			
			//Write File
			String[] modelFile	= allLineUserFile();
			modelFile[linefile]	= lineModelUserRights;
			
			BufferedWriter outputWriter = null;
			  try {
				outputWriter = new BufferedWriter(new FileWriter(filepath));
				for (int i = 0; i < modelFile.length; i++) {
				    // Maybe:
				    outputWriter.write(modelFile[i]);
				    outputWriter.newLine();
				  }
				  outputWriter.flush();  
				  outputWriter.close();  
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}else {
			logger.error("Error at deleteUserOfModel");
		}
	}
}