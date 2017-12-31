package bean;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
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
 * Diese Bean Klasse organisiert die Sachlogik der WebAnwendung
 * Bei allen Datenzugriffen wird auf die Klasse data.Model zugegriffen
 * Für die Problemlösung wird auf die Klasse bean.Solver zugegriffen. 
 * @author Christian
 *
 */
public class EDbean implements Serializable{

	/**
	 * Url des verwendeten WebService
	 * 
	 */
	//public static final String 	WebService	= "http://127.0.0.1:8008";
	public static final String 	WebService	= "http://194.95.45.70:8008";

	/**
	 * Absoluter Verzeichnisname des Verzeichnisses in dem die ModellDateien gespeichert werden
	 */
	public static final String ModelDir 	= "/Users/Max/Documents/workspace/Fertiliser_0.2/testDir/";
	//public static final String ModelDir 		= "/home/mitarbeiter/cmueller/or_model/Fertilizer/";

	public static final String CmplModel 		= "/Users/Max/Documents/workspace/Fertiliser_0.2/cmpl/Fertilizer.cmpl";
	//public static final String CmplModel 		= "/Users/Max/Documents/workspace/Fertilizer_0.1/cmpl/Fertilizer.cmpl";

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
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen Ingredients da.
	 * 
	 * Erstellt von: Edgar M.
	 */
	private int ChangeIngredientId = -1;
	
	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen PresentIngredients da.
	 * 
	 */
	private int ChangePresentIngredientId = -1;
	
	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen RequiredIngredients da.
	 * 
	 */
	private int ChangeRequiredIngredientId = -1;
	
	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen Fertilisers da.
	 * 
	 */
	private int ChangePresentFertiliserId = -1;
	
	/**
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen Fertilisers da.
	 * 
	 */
	private int ChangeRequiredFertiliserId = -1;
	
	/**
	 * Über AddIngredient wird die Sichtbarkeit der Zeile zum hinzufügen von neuen Zutaten geregelt.
	 * 
	 * Autor: Eddi M.
	 */
	private boolean AddIngredient;
	
	/**
	 * Über AddPresentIngredients wird die Sichtbarkeit der Zeile zum hinzufügen von neuen Zutaten geregelt.
	 * Dabei wird in der ersten Zeile die Id des PresentFertiliser gespeichert und in der zweiten Zeile 1 für sichtbar
	 * und 0 für nicht sichtbar.
	 * 
	 * Autor: Eddi M.
	 */
	private int[] AddPresentIngredients;
	
	private int[] AddRequiredIngredients;
	
	/**
	 * Dieses Attribut regelt die Sichtbarkeit der ID Spalte
	 * 
	 * Autor: Eddi M.
	 */
	private boolean showID = true;
	
	/**
	 * Name des aktuellen Nutzers
	 */
	private String 	nutzer;
	/**
	 * Das aktuelle Modell
	 */
	private Model	model;
	
	private Fertiliser		modelData;
	
	private boolean ShowDeletedIngredients;
	
	private int[] ShowDeletedPresentIngredient;
	
	private int[] ShowDeletedRequiredIngredient;
	
	/**
	 * Initialisierung der Webanwendung,
	 * wird vom Controller aufgerufen
	 * setzt EDbean.parameterFile, Proxy Daten, ...
	 * @throws Exception 
	 */
	public static void init(String servletPath) throws Exception {
		Model.setBasisPfad(EDbean.ModelDir);
		Model.init();
		if(! EDbean.checkWebService()) throw new Exception("Webservice nicht gefunden, siehe log.");

	}
	
	public EDbean(){
		this.nutzer	= "Gast";
	}
	
	public void setNutzer(String nutzer){
		this.nutzer	= nutzer;
	}
	
	public String getNutzer(){
		return this.nutzer;
	}
	
	public void setShowDeletedIngredient(boolean b){
		this.setAddPresentIngredients("0", false);
		this.setAddRequiredIngredients("0", false);
		this.ShowDeletedIngredients = b;
	}
	
	public boolean getShowDeletedIngredient(){
		return this.ShowDeletedIngredients;
	}
	
	
	public void setModel(String modelId){
		this.model		= Model.get(modelId);
		this.modelData	= this.model.getModelData();
		
		this.setAddPresentIngredients("0", false);
		this.setAddRequiredIngredients("0", false);
		this.setShowDeletedPresentIngredient("0", false);
		this.setShowDeletedRequiredIngredient("0", false);
	}
	
	public Model getModel(){
		return this.model;
	}
	
	
	public String getDatum(){
		Calendar today = Calendar.getInstance();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(today.getTime());
	}
	
	public String getModelName(){
		return this.modelData.getName();
	}
	
	public void setModelName(String name){
		this.modelData.setName(name);
	}
	
	public boolean isSolved(){
		return this.modelData.getSolution().size()>0 && this.modelData.isSolved();
	}
	
	public String getSolutionStatus(int solId){
		return this.modelData.getSolution().get(solId).getStatus();
	}
	
	public int getNrZutaten(){
		return this.modelData.getIngredients().getIngredient().size();
	}
	
	//Noch nicht perfekt!!
	public int getNrZutaten(String name){
		int result = 0;
		for (int i=0; i<getNrZutaten();i++){
			
			if (this.modelData.getIngredients().getIngredient().get(i).getName().equals(name)){
				result = this.modelData.getIngredients().getIngredient().get(i).getId();
			}
		}
		
		return result;
	}
	
	/**
	 * Gibt die Anzahl der PresentFertiliser wieder.
	 * 
	 * @return - Integer als Anzahl der PresentFertiliser.
	 */
	public int getNrDuengerVorhanden(){
		return this.modelData.getPresentFertiliser().size();
	}
	
	public int getNrDuengerBenoetigt(){
		return this.modelData.getRequiredFertiliser().size();
	}
	
	public int getNrLoesungen(){
		return this.modelData.getSolution().size();
	}
	
	public void setChangePresentIngredientId(String sId){
		int id = Integer.parseInt(sId);
		this.ChangePresentIngredientId = id;
	}
	
	public int getChangePresentIngredientId(){
		return ChangePresentIngredientId;
	}
	
	public void setChangePresentFertiliser(String sId){
		int id = Integer.parseInt(sId);
		this.ChangePresentFertiliserId = id;
	}
	
	public int getChangePresentFeritliser(){
		return this.ChangePresentFertiliserId;
	}
	
	public void setChangeRequiredFertiliser(String sId){
		int id = Integer.parseInt(sId);
		this.ChangeRequiredFertiliserId = id;
	}
	
	public int getChangeRequiredFeritliser(){
		return this.ChangeRequiredFertiliserId;
	}
	
	public void setChangeRequiredIngredientId(String sId){
		int id = Integer.parseInt(sId);
		this.ChangeRequiredIngredientId = id;
	}
	
	public int getChangeRequiredIngredientId(){
		return ChangeRequiredIngredientId;
	}
	
	public void setAddIngredient(boolean b){
		if (b == true){
			this.setAddPresentIngredients("0", false);
			this.setAddRequiredIngredients("0", false);
		}
		this.AddIngredient = b;
	}
	
	public boolean getAddIngredient(){
		return AddIngredient;
	}
	
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
	
	public int[] getAddPresentIngredients(){
		return this.AddPresentIngredients;
	}
	
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
	
	public int[] getAddRequiredIngredients(){
		return this.AddRequiredIngredients;
	}
	
	/**
	 * Diese Funktion regelt die Sichtbarkeit der geloeschten Prequiredngredients.
	 * 
	 * @param strPresentFertiliserId - String
	 * @param sichtbarkeit - Boolean
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
	
	public int[] getShowDeletedPresentIngredient(){
		return this.ShowDeletedPresentIngredient;
	}
	
	/**
	 * Diese Funktion regelt die Sichtbarkeit der geloeschten RequiredIngredients.
	 * 
	 * @param strRequiredFertiliserId - String
	 * @param sichtbarkeit - Boolean
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
	
	public int[] getShowDeletedRequiredIngredient(){
		return this.ShowDeletedRequiredIngredient;
	}
	
	public void setShowID(boolean b){
		this.showID = b;
	}
	
	public boolean getShowID(){
		return this.showID;
	}
	
	/**
	 * Diese Funktion nimmt String werte und Speichert den Ingredient ab.
	 * 
	 * @param name
	 * @param price
	 * @param unit
	 */
	public void addIngredient(String name, String price, String unit){
		int id = this.getNrZutaten();
		try{
			double p = Double.parseDouble(price);
			this.model.addIngredient(id, name, p, Units.valueOf(unit.toUpperCase()));
		}catch(NumberFormatException e){
			this.model.addIngredient(id, name, 0.0, Units.valueOf(unit.toUpperCase()));
		}
	}
	
	public void changeIngredient(String ingId, String price, String unit){
		try{
			int ingredientId = Integer.parseInt(ingId);
			double p = Double.parseDouble(price);
			this.model.changeIngredient(this.model.getIngredient(ingredientId), p, Units.valueOf(unit.toUpperCase()));
			
		}catch(NumberFormatException e){
			this.model.changeIngredient(this.model.getIngredient(0), 0.0, Units.valueOf(unit.toUpperCase()));
		}
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
	 * 
	 * Autor: Eddi M.
	 */
	public void addOrChangePresentIngredientViaName(String fId, String name, String percent){
		int presentIngredientId, presentFertiliserId;
		double p;
		presentIngredientId = this.getNrZutaten(name);
		
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
	 * 
	 * Autor: Eddi M.
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
	 * 
	 * Autor: Eddi M.
	 */
	public void addOrChangePresentIngredient(int presentFertiliserId, int presentIngredientId, double p){
		if (this.model.getIdOfPresentIngredientsFromPresentFertiliser(model.getPresentFertiliser(presentFertiliserId)).contains(presentIngredientId) == true){
			int index = this.model.getIndexOfPresentIngredientFromIngredientId(this.model.getPresentFertiliser(presentFertiliserId), presentIngredientId);
			this.changePresentIngredient(presentFertiliserId, index, p);
		}else {
			this.addPresentIngredient(presentFertiliserId, presentIngredientId, p);
		}
	}
	
	public void addPresentIngredient(int fertiliserId, int presentIngredientId, double percent){
		this.model.addPresentIngredient(fertiliserId, presentIngredientId, percent);
	}
	
	public void changePresentIngredient(String fId, String s_presentIngredientIndex, String percent){
		int presentIngredientIndex = Integer.parseInt(s_presentIngredientIndex);
		try{
			int presentFertiliserId = Integer.parseInt(fId);
			double p = Double.parseDouble(percent);
			this.changePresentIngredient(presentFertiliserId, presentIngredientIndex, p);
		}catch(NumberFormatException e){
			System.out.println("Es konnte der PresentIngredient nicht geändert werden");
		}
	}
	
	public void changePresentIngredient(int fertiliserId, int presentIngredientIndex, double percent){
		this.model.changePresentIngredient(fertiliserId, presentIngredientIndex, percent);
	}
	
	/**
	 * Diese Funktion entscheidet anhand der FertiliserId, des Namens des RequiredIngredient, ob die Angabe als neuer RequiredIngredient gespeichert oder
	 * ein vorhander Ingredient aktualisiert werden soll.
	 * Alle Parameter sind dabei als String zu übergeben.
	 * 
	 * Die Funktion fürt die zum Speichern notwendigen Funktionen aus.
	 * 
	 * @param fId - Die Id des Fertilisers als String
	 * @param name - Name des RequiredIngredient als String
	 * @param percent - Prozentwert des RequiredIngredients als String
	 * 
	 * Autor: Eddi M.
	 */
	public void addOrChangeRequiredIngredientViaName(String fId, String name, String percentMin, String percentMax){
		int requiredIngredientId, requiredFertiliserId;
		double pMin = 0.0, pMax = 0.0;
		requiredIngredientId = this.getNrZutaten(name);
		
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
	 * Alle Parameter sind dabei als String zu übergeben.
	 * 
	 * Die Funktion fürt die zum Speichern notwendigen Funktionen aus.
	 * 
	 * @param fId - Die Id des Fertilisers als String
	 * @param strRequiredIngredientId - Id des RequiredIngredients als String
	 * @param percent - Prozentwert des RequiredIngredients als String
	 * 
	 * Autor: Eddi M.
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
	 * 
	 * Autor: Eddi M.
	 */
	public void addOrChangeRequiredIngredient(int requiredFertiliserId, int requiredIngredientId, double percentMin, double percentMax){
		if (this.model.getIdOfRequiredIngredientsFromRequiredFertiliser(model.getRequiredFertiliser(requiredFertiliserId)).contains(requiredIngredientId) == true){
			int index = this.model.getIndexOfRequiredIngredientFromIngredientId(this.model.getRequiredFertiliser(requiredFertiliserId), requiredIngredientId);
			this.changeRequiredIngredient(requiredFertiliserId, index, percentMin, percentMax);
		}else {
			this.addRequiredIngredient(requiredFertiliserId, requiredIngredientId, percentMin, percentMax);
		}
	}
	
	public void addRequiredIngredient(int fertiliserId, int requiredIngredientId, double percentMin, double percentMax){
		this.model.addRequiredIngredient(fertiliserId, requiredIngredientId, percentMin, percentMax);
	}
	
	public void changeRequiredIngredient(int fertiliserId, int presentIngredientIndex, double percentMin, double percentMax){
		this.model.changeRequiredIngredient(fertiliserId, presentIngredientIndex, percentMin, percentMax);
	}
	
	/**
	 * Diese Funktion Rundet eine double Zahl Kaufmännisch auf die mitgegebene Nachkommastelle.
	 * 
	 * @param zahl
	 * @param stellenNachKomma
	 * @return
	 */
	public double runden(double zahl, int stellenNachKomma){
		int temp = (int) (zahl * Math.pow(10.0, (double) stellenNachKomma));
		zahl = (double)(temp);
		zahl = zahl / Math.pow(10.0, (double) stellenNachKomma);
		return zahl;
	}
	
	/**
	 * 
	 * 
	 */
	public void aktualisierePercent(String fId){
		int presentFertiliserId = Integer.parseInt(fId);
		
		this.aktualisierePercent(presentFertiliserId);
	}
	
	/**
	 * Diese Funktion aktualisiert die Prozentwerte der vorhandenen PresentIngrediens, wenn der Gesamtwert über 100 % sein sollte.
	 * Die Prozente werden automatisch angepasst, sodass das gleiche Verhältnis bestehen bleibt.
	 * 
	 * @param fId - Integer
	 * @param presentIngredientId
	 * 
	 * Autor: Eddi M.
	 */
	public void aktualisierePercent(int fId){
		
		PresentFertiliser fertiliser = this.modelData.getPresentFertiliser().get(fId);
		
		double sumPercent = 0.0;
		sumPercent += this.getTotalPercentOfPresentIngredients(fId);
		for (int i=0; i< this.getNrZutaten(); i++){
			if (this.modelData.getIngredients().getIngredient().get(i).isActive() == true && this.modelData.getIngredients().getIngredient().get(i).getActive() == true && model.getIdOfPresentIngredientsFromPresentFertiliser(model.getPresentFertiliser(fId)).contains(i) == true ){
				
				int index = model.getIndexOfPresentIngredientFromIngredientId(fertiliser, i);
				PresentIngredient pi = model.getPresentIngredient(fertiliser, index);
				
				if (pi.isActive() == false){
					this.setPresentIngredientActive(fertiliser, Integer.toString(i), true);
				}
				if (pi.getActive() == true){
					double neuPercent = runden((100) * (pi.getPercent()/(sumPercent)), 2);
					changePresentIngredient(fId, index, neuPercent);
				}
			}
		}
	}
	
	public void setChangeIngredientId(int id){
		this.ChangeIngredientId = id;
	}
	
	public int getChangeIngredientId(){
		return this.ChangeIngredientId;
	}
	
	//-------------------------------------
	/**
	 * liefert HTML String fuer ZutatenTableau
	 * @return
	 */
	public String getZutatenTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
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
			out += this.getImageAndToolTip(Images.Unvisible);
			out += "<input type=\"hidden\" name=\"action\" value=\"24_dontshowDeletedIngredients\"/>";
			out += "</form>";
		}else {
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "deleted ";
			out += this.getImageAndToolTip(Images.Visible);
			out += "<input type=\"hidden\" name=\"action\" value=\"23_showDeletedIngredients\"/>";
			out += "</form>";
		}
		out += "</th><th>Name</th><th>Preis [&euro;/Einheit]</th><th>Einheit</th>";
		out += "<form action=\"Controller\" method=\"post\" />";
		if (this.AddIngredient == false){
			out += "<td>";
			out += this.getImageAndToolTip(Images.Add);
			out += "</td></tr>\n";
			out += "<input type=\"hidden\" name=\"action\" value=\"20_showRowAdding\"/>";
		}else if (this.AddIngredient == true){
			out += "<td>";
			out += this.getImageAndToolTip(Images.Back);
			out += "</td></tr>\n";
			out += "<input type=\"hidden\" name=\"action\" value=\"21_NoShowRowAdding\"/>";
		}
		out += "</form>";
		
		for (int i=0; i< this.getNrZutaten(); i++ ){
			out += "<tr>";
			
			if (this.modelData.getIngredients().getIngredient().get(i).isActive() == false){
				this.setIngredientActive(Integer.toString(i), true);
			}
			if (this.modelData.getIngredients().getIngredient().get(i).isActive() == true && (this.modelData.getIngredients().getIngredient().get(i).getActive() == true || this.ShowDeletedIngredients == true)){
				
				if (i == this.getChangeIngredientId()){
					if (this.showID == true){
						out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getId()+"</td>";
					}
					out += this.getDeleteIngredientButton(i);
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getName()+"</td>";
					out += "<td><input type=\"text\" name=\"addIngredientPrice\" value=\""+this.modelData.getIngredients().getIngredient().get(i).getPrice()+ "\"/></td>";
					out += "<td><select name=\"addIngredientUnit\" >";
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
					out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+this.modelData.getIngredients().getIngredient().get(i).getId()+"\"/>";
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
					out += this.getImageAndToolTip(Images.Edit);
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
		out += "</table>\n";
		return out;
	}
	
	/**
	 * Diese Funktion gibt ein String zurück in dem der Image mit AltTag und Tooltip dargestellt wird.
	 * 
	 * @param image des Typs Images
	 * @return String
	 * 
	 * @Autor: Eddi M.
	 */
	private String getImageAndToolTip(Images image){
		String out="";
		
		out += "<input id=\"image\" type=\"image\" src=\"" + image.getImagePfad() + "\" alt=\""+ image.getAltTag() +"\" data-toggle=\"tooltip\" data-placement=\"top\" title=\""+ image.getToolTip() +"\" />";
		return out;
	}
	
	/**
	 * Diese Funktion fügt die den delete button in die Tabelle ein.
	 * 
	 * @parm Integerwert der die ID des Ingredients wiedergibt.
	 * @return Es wird der Button als html String zurück gegeben
	 */
	private String getDeleteIngredientButton(int ingredientId){
		String out = "";
		if (this.ShowDeletedIngredients == true && this.modelData.getIngredients().getIngredient().get(ingredientId).getActive() == false){
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
			out += this.getImageAndToolTip(Images.Delete);
			out += "</td>";
			out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+ingredientId+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"22_deleteIngredients\" />";
			out += "</form>";
		}
		
		return out;
	}
	
	/**
	 * Dies Funktion gibt die Zeile zurück mit der eine neue Zutat hinzugefügt werden kann.
	 * 
	 * @param String, welcher erweitert wird
	 * @return Gibt die Zeile zum hinzufügen von Zutaten als String zurück.
	 * 
	 * Autor: Eddi M.
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
		out += "<td><input type=\"text\" name=\"addIngredientName\" autofocus /></td>";
		out += "<td><input type=\"text\" name=\"addIngredientPrice\" /></td>";
		out += "<td><select name=\"addIngredientUnit\" >";
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
	 * @return
	 */
	public String getVorhandenDuengerTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		if (this.showID == true){
			out += "<tr><td colspan=\"5\">";
		}else{
			out += "<tr><td colspan=\"4\">";
		}
		out += "Vorhandene Duenger</th></tr>\n";
		
		for (int i=0; i< this.getNrDuengerVorhanden(); i++ ){
			PresentFertiliser fertiliser = this.modelData.getPresentFertiliser().get(i);
			if (this.showID == true){
				out += "<tr><td colspan=\"5\">";
				out += "<b>ID: </b>"+fertiliser.getId()+"<br/>";
			}else{
				out += "<tr><td colspan=\"4\">";
			}
			out += "<b>Name: </b>"+fertiliser.getName()+"<br/>";
			out += "<b>Bestand: </b>"+fertiliser.getAmount()+"<br/>";
			out += "<b>Einheit: </b>"+fertiliser.getUnit()+"<br/>";
			out += "<b>Bestandteile:</b>";
			out += "</td></tr>\n";
			out += "<tr>";
			if (this.showID == true){
				out += "<th>Id</th>";
			}
			out += "<th>";
			if (this.ShowDeletedPresentIngredient[0] == i && this.ShowDeletedPresentIngredient[1] == 1){
				out += "<form action=\"Controller\" method=\"post\" />";
				out += "deleted ";
				out += this.getImageAndToolTip(Images.Unvisible);
				out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"29_dontshowDeletedPresentIngredients\"/>";
				out += "</form>";
			}else{
				out += "<form action=\"Controller\" method=\"post\" />";
				out += "deleted ";
				out += this.getImageAndToolTip(Images.Visible);
				out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"30_showDeletedPresentIngredients\"/>";
				out += "</form>";
			}
			out += "</th>";
			out += "<th>Name</th><th>";
			
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "" + this.getTotalPercentOfPresentIngredients(i) + " % ";
			out += this.getImageAndToolTip(Images.FillUp);
			out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"32_aktualisierePercentOfPresentIngredients\"/>";
			out += "</form>";
			out += "</th>";
			out += "<th>";
			
			// Addbutton im Menu
			out += "<form action=\"Controller\" method=\"post\" />";
			if (this.AddPresentIngredients[1] == 1 && this.AddPresentIngredients[0] == i){
				out += this.getImageAndToolTip(Images.Back);
				out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"27_NoShowRowPresentIngredientAdding\"/>";
			}else{
				out += this.getImageAndToolTip(Images.Add);
				out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"26_showRowPresentIngredientAdding\"/>";
			}
			out += "</form>";
			
			out += "</th>";
			out += "</tr>\n";
			for(int j=0; j< this.getNrZutaten(); j++){
				
				System.out.println("RequiredFertiliser: " + i + " RequiredIngredient: " + j);
				if (this.modelData.getIngredients().getIngredient().get(j).isActive() == false){
					this.setIngredientActive(Integer.toString(j), true);
				}
				System.out.println("RequiredIngredients of RequiredFertiliser: " + model.getIdOfPresentIngredientsFromPresentFertiliser(fertiliser));
				if (this.modelData.getIngredients().getIngredient().get(j).isActive() == true &&
					this.modelData.getIngredients().getIngredient().get(j).getActive() == true &&
					model.getIdOfPresentIngredientsFromPresentFertiliser(fertiliser).contains(j) == true){
					
					int index = model.getIndexOfPresentIngredientFromIngredientId(fertiliser, j);
					PresentIngredient pi = fertiliser.getPresentIngredients().getPresentIngredient().get(index);
					System.out.println("Status von requiredIngredient: " + pi.getActive());
					if (pi.isActive() == false){
						this.setPresentIngredientActive(fertiliser, Integer.toString(j), true);
					}
					if (pi.isActive() == true && 
							(pi.getActive() == true || 
								(this.ShowDeletedPresentIngredient[0] == i && 
								this.ShowDeletedPresentIngredient[1] == 1
								)
							)
						){
					
						out += "<tr>";
						if (this.showID == true){
							out += "<td>"+pi.getIngredientId()+"</td>";
						}
						out += this.getDeletePresentIngredientButton(i, index);
						out += "<td>"+this.modelData.getIngredients().getIngredient().get(pi.getIngredientId()).getName()+"</td>";
						
						if (i == this.getChangePresentFeritliser() && index == model.getIndexOfPresentIngredientFromIngredientId(fertiliser, this.getChangePresentIngredientId())){
							out += "<form action=\"Controller\" method=\"post\" />";
							out += "<td><input type=\"text\" name=\"changeIngredientPercent\" value=\""+pi.getPercent()+"\"/></td>";
							out += "<td>";
							out += this.getImageAndToolTip(Images.Done);
							out += "</td>";
							out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+i+"\"/>";
							out += "<input type=\"hidden\" name=\"presentIngredientId\" value=\""+j+"\"/>";
							out += "<input type=\"hidden\" name=\"action\" value=\"09_saveEditPresentIngredient\"/>";
							out += "</form>";
						}else{
							out += "<td>"+pi.getPercent()+"</td>";
							out += "<form action=\"Controller\" method=\"post\" />";
							out += "</select>";
							out += "<td>";
							out += this.getImageAndToolTip(Images.Edit);
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
		}
		
		out += "</table>\n";
		return out;
	}
	
	/**
	 * Dies Funktion gibt die Zeile zurück mit der ein neuer PresentIngredient hinzugefügt werden kann.
	 * 
	 * @param presentFertiliserId - Integer der Id des PresentFertiliser
	 * @return - String der Html Zeile
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
		out += "<td><select name=\"addPresentIngredientName\" autofocus >";
		for( int g=0; g< this.getNrZutaten(); g++ ){
			if (this.modelData.getIngredients().getIngredient().get(g).getActive() == true){
				out += "<option>"+this.modelData.getIngredients().getIngredient().get(g).getName()+"</option>";
			}
			
		}
		out += "</select></td>";
		out += "<td><input type=\"text\" name=\"addIngredientPercent\" /></td>";
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
	 * Diese Funktion zeigt je nach Status des PresentIngredients ein Button zum loeschen oder ein Button zum hinzufuegen.
	 * Des Weiteren haegt die Darstellung davon, ob die geloeschten PresentIngredients eingezeigt werden sollen.
	 * 
	 * @param presentFertiliserId - Integer
	 * @param ingredientIndex - Integer
	 * @return
	 */
	private String getDeletePresentIngredientButton(int presentFertiliserId, int ingredientIndex){
		String out = "";
		PresentFertiliser presentFertiliser= this.modelData.getPresentFertiliser().get(presentFertiliserId);
		PresentIngredient presentIngredient = presentFertiliser.getPresentIngredients().getPresentIngredient().get(ingredientIndex);
		if (
			(this.ShowDeletedPresentIngredient[0] == presentFertiliserId && this.ShowDeletedPresentIngredient[1] == 1) && 
			presentIngredient.getActive() == false
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
			out += this.getImageAndToolTip(Images.Delete);
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
	 * @return
	 * 
	 * Autor: Eddi M.
	 */
	public double getTotalPercentOfPresentIngredients(int presentFertiliserId){
		double sum = 0.0;
		PresentFertiliser presentFertiliser = model.getPresentFertiliser(presentFertiliserId);
		
		for (int i=0; i< this.getNrZutaten(); i++){
			if (this.modelData.getIngredients().getIngredient().get(i).isActive() == true && this.modelData.getIngredients().getIngredient().get(i).getActive() == true && model.getIdOfPresentIngredientsFromPresentFertiliser(presentFertiliser).contains(i) == true){
				
				int index = model.getIndexOfPresentIngredientFromIngredientId(model.getPresentFertiliser(presentFertiliserId), i);
				PresentIngredient presentIngredient= model.getPresentIngredient(presentFertiliser, index);
				System.out.println("PresentFertiliserId " + presentFertiliserId + " PresentIngredientId: " + i + " index: " + index);
				if (presentIngredient.isActive() == false){
					this.setPresentIngredientActive(presentFertiliser, Integer.toString(i), true);
				}
				if (presentIngredient.isActive() == true && presentIngredient.getActive() == true){
					sum += presentIngredient.getPercent();
				}
			}
		}
		return this.runden(sum, 2);
	}
	
	/**
	 * liefert HTML String für benötigte Dünger
	 * @return
	 */
	public String getBenoetigteDuengerTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		if (this.showID == true){
			out += "<tr><td colspan=\"6\">";
		}else{
			out += "<tr><td colspan=\"5\">";
		}
		out += "Benoetigter Duenger</th></tr>\n";
		
		for (int i=0; i< this.getNrDuengerBenoetigt(); i++ ){
			RequiredFertiliser fertiliser = this.modelData.getRequiredFertiliser().get(i);
			if (this.showID == true){
				out += "<tr><td colspan=\"6\">";
				out += "<b>ID: </b>"+fertiliser.getId()+"<br/>";
			}else{
				out += "<tr><td colspan=\"5\">";
			}
			out += "<b>Name: </b>"+fertiliser.getName()+"<br/>";
			out += "<b>Bestand: </b>"+fertiliser.getAmount()+"<br/>";
			out += "<b>Einheit: </b>"+fertiliser.getUnit()+"<br/>";
			out += "<b>Bestandteile:</b>";
			out += "</td></tr>\n";
			out += "<tr>";
			if (this.showID == true){
				out += "<th>Id</th>";
			}
			out += "<th>";
			if (this.ShowDeletedRequiredIngredient[0] == i && this.ShowDeletedRequiredIngredient[1] == 1){
				out += "<form action=\"Controller\" method=\"post\" />";
				out += "deleted ";
				out += this.getImageAndToolTip(Images.Unvisible);
				out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"33_dontshowDeletedRequiredIngredients\"/>";
				out += "</form>";
			}else{
				out += "<form action=\"Controller\" method=\"post\" />";
				out += "deleted ";
				out += this.getImageAndToolTip(Images.Visible);
				out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"34_showDeletedRequiredIngredients\"/>";
				out += "</form>";
			}
			out += "</th>";
			out += "<th>Name</th><th>min %</th><th>max %</th>";
			
			out += "<th>";
			out += "<form action=\"Controller\" method=\"post\" />";
			if (this.AddRequiredIngredients[1] == 1 && this.AddRequiredIngredients[0] == i){
				out += this.getImageAndToolTip(Images.Back);
				out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"35_NoShowRowRequiredIngredientAdding\"/>";
			}else{
				out += this.getImageAndToolTip(Images.Add);
				out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"36_showRowRequiredIngredientAdding\"/>";
			}
			out += "</form>";
			out += "</th>";
			out += "</tr>\n";
			for(int j=0; j< this.getNrZutaten(); j++){
				
				System.out.println("RequiredFertiliser: " + i + " RequiredIngredient: " + j);
				if (this.modelData.getIngredients().getIngredient().get(j).isActive() == false){
					this.setIngredientActive(Integer.toString(j), true);
				}
				System.out.println("RequiredIngredients of RequiredFertiliser: " + model.getIdOfRequiredIngredientsFromRequiredFertiliser(fertiliser));
				if (this.modelData.getIngredients().getIngredient().get(j).isActive() == true &&
						this.modelData.getIngredients().getIngredient().get(j).getActive() == true &&
						model.getIdOfRequiredIngredientsFromRequiredFertiliser(fertiliser).contains(j) == true){
					
					int index = model.getIndexOfRequiredIngredientFromIngredientId(fertiliser, j);
					RequiredIngredient requiredIngredient = fertiliser.getRequiredIngredients().getRequiredIngredient().get(index);
					System.out.println("Status von requiredIngredient: " + requiredIngredient.getActive());
					
					if (requiredIngredient.isActive() == false){
						this.setRequiredIngredientActive(fertiliser, Integer.toString(j), true);
					}
					if (requiredIngredient.isActive() == true && 
							(requiredIngredient.getActive() == true || 
								(this.ShowDeletedRequiredIngredient[0] == i && 
								this.ShowDeletedRequiredIngredient[1] == 1
								)
							)
						){
						out += "<tr>";
						if (this.showID == true){
							out += "<td>"+requiredIngredient.getIngredientId()+"</td>";
						}
						out += this.getDeleteRequiredIngredientButton(i, index);
						out += "<td>"+this.modelData.getIngredients().getIngredient().get(requiredIngredient.getIngredientId()).getName()+"</td>";
						
						if (i == this.getChangeRequiredFeritliser() && index == model.getIndexOfRequiredIngredientFromIngredientId(fertiliser, this.getChangeRequiredIngredientId())){
							out += "<form action=\"Controller\" method=\"post\" />";
							out += "<td><input type=\"text\" name=\"changeIngredientPercentMin\" value=\""+requiredIngredient.getPercentMin()+"\"/></td>";
							out += "<td><input type=\"text\" name=\"changeIngredientPercentMax\" value=\""+requiredIngredient.getPercentMax()+"\"/></td>";
							out += "<td>";
							out += this.getImageAndToolTip(Images.Done);
							out += "</td>";
							out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+i+"\"/>";
							out += "<input type=\"hidden\" name=\"requiredIngredientId\" value=\""+j+"\"/>";
							out += "<input type=\"hidden\" name=\"action\" value=\"38_saveEditRequiredIngredient\"/>";
							out += "</form>";
						}else{
							out += "<td>"+requiredIngredient.getPercentMin()+"</td>";
							out += "<td>"+requiredIngredient.getPercentMax()+"</td>";
							out += "<form action=\"Controller\" method=\"post\" />";
							out += "</select>";
							out += "<td>";
							out += this.getImageAndToolTip(Images.Edit);
							out += "</td>";
							out += "<input type=\"hidden\" name=\"requiredFertiliserId\" value=\""+i+"\"/>";
							out += "<input type=\"hidden\" name=\"requiredIngredientId\" value=\""+j+"\"/>";
							out += "<input type=\"hidden\" name=\"action\" value=\"39_editRequiredIngredient\"/>";
							out += "</form>";
						}
						out += "</tr>\n";
					}
				}
			}
			
			if (this.AddRequiredIngredients[1] == 1 && this.AddRequiredIngredients[0] == i){
				out += this.getRowAddRequiredIngredient(i);
			}
			out += "<br>";
		}
		out += "</table>\n";
		return out;
		
	}
	
	/**
	 * Dies Funktion gibt die Zeile zurück mit der ein neuer RequiredIngredient hinzugefügt werden kann.
	 * 
	 * @param requiredFertiliserId - Integer der Id des RequiredFertiliser
	 * @return - String der Html Zeile
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
		out += "<td><select name=\"addRequiredIngredientName\" autofocus >";
		for( int g=0; g< this.getNrZutaten(); g++ ){
			if (this.modelData.getIngredients().getIngredient().get(g).getActive() == true){
				out += "<option>"+this.modelData.getIngredients().getIngredient().get(g).getName()+"</option>";
			}
			
		}
		out += "</select></td>";
		out += "<td><input type=\"text\" name=\"addIngredientPercentMin\" /></td>";
		out += "<td><input type=\"text\" name=\"addIngredientPercentMax\" /></td>";
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
		 * Diese Funktion zeigt je nach Status des RequiredIngredients ein Button zum loeschen oder ein Button zum hinzufuegen.
		 * Des Weiteren haegt die Darstellung davon, ob die geloeschten RequiredIngredients eingezeigt werden sollen.
		 * 
		 * @param requiredFertiliserId - Integer
		 * @param ingredientIndex - Integer
		 * @return
		 */
		private String getDeleteRequiredIngredientButton(int requiredFertiliserId, int ingredientIndex){
			String out = "";
			RequiredFertiliser requiredFertiliser= this.modelData.getRequiredFertiliser().get(requiredFertiliserId);
			RequiredIngredient requiredIngredient = requiredFertiliser.getRequiredIngredients().getRequiredIngredient().get(ingredientIndex);
			if (
				(this.ShowDeletedRequiredIngredient[0] == requiredFertiliserId && this.ShowDeletedRequiredIngredient[1] == 1) && 
				requiredIngredient.getActive() == false
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
	
	public String getSolutionTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"3\">Loesung</th></tr>\n";
		out += "<tr><th align=\"left\" colspan=\"2\">Solver Status: </th><td colspan=\"1\">"+this.modelData.getSolverStatus()+"</td></tr>\n";
		
		if(this.isSolved()){
			for (int i=0; i< this.getNrLoesungen(); i++ ){
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
						out += "<td colspan=\"3\">Zutaten:</td>";
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
	
	public void setIngredientActive(String ingredientId, boolean b){
		int id = Integer.parseInt(ingredientId);
		model.setIngredientActive(model.getIngredient(id), b);
	}
	
	/**
	 * Setzt den Status des PresentIngredients auf den boolischen Wert b.
	 * Es muss die IngredientId und der PresentFertiliser übergeben werden.
	 * 
	 * @param presentFertiliser - PresentFertiliser
	 * @param strPresentIngredientId - String
	 * @param b - boolean
	 * 
	 * Autor: Eddi M.
	 */
	public void setPresentIngredientActive(PresentFertiliser presentFertiliser, String strPresentIngredientId, boolean b){
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
	 * Autor: Eddi M.
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
	 * 
	 * Autor: Eddi M.
	 */
	public void setPresentIngredientActive(PresentFertiliser presentFertiliser, int presentIngredientId, boolean b){
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
	 * 
	 * Autor: Eddi M.
	 */
	public void setRequiredIngredientActive(RequiredFertiliser requiredFertiliser, String strRequiredIngredientId, boolean b){
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
	 * 
	 * Autor: Eddi M.
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
	 * 
	 * Autor: Eddi M.
	 */
	public void setRequiredIngredientActive(RequiredFertiliser requiredFertiliser, int requiredIngredientId, boolean b){
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
		String[] ids = Model.getModelIds();
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th>Id</th><th>Name</th><th colspan=\"3\">Model Management</th></tr>\n";
		for(int i=0; i<ids.length; i++){
			String refModel = "Controller?action=03_showModel&modelId="+ids[i];
			out += "<tr><td>"+ids[i]+"</td>";
			out += "<td><a href=\""+refModel+"\">"+Model.get(ids[i]).getModelData().getName()+"</a></td>";
			refModel = "Controller?action=15_removeModel&modelId="+ids[i];
			out += "<td><a href=\""+refModel+"\">delete</a></td>\n";
			refModel = "Controller?action=11_saveModel&modelId="+ids[i];
			out += "<td><a href=\""+refModel+"\">save</a></td></tr>\n";
		}
		out += "</table>\n";
		return out;
	}
	
	/**
	 * liefert den HTML String zum Hinzufügen eines Modells
	 * 
	 * @return
	 */
	public String getModelAddView(){
		String out = "";
		out	+= "<table border=\"1\">\n";
		//out += "<tr><th colspan=\"4\">Add a Model</th></tr>";
		out += "<tr><th>Model Id</th><th>Model Name</th></tr>";
		out += "<tr>";
		out += "<td><input type=\"text\" name=\"modelId\" size=\"10\" ></td>";
		out += "<td><input type=\"text\" name=\"modelName\" size=\"10\" ></td>";
		out += "</tr>";
		out	+= "</table>\n";
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
		String[] supplyName 		= new String[this.getNrDuengerVorhanden()];
		double[] stockVal 			= new double[this.getNrDuengerVorhanden()];
		for(int i=0; i<this.getNrDuengerVorhanden(); i++){
			supplyName[i]			= this.modelData.getPresentFertiliser().get(i).getName();
			stockVal[i]				= this.modelData.getPresentFertiliser().get(i).getAmount();
		}

		String[] demandName 		= new String[this.getNrDuengerBenoetigt()];
		double[] demandVal 			= new double[this.getNrDuengerBenoetigt()];
		for(int i=0; i<this.getNrDuengerBenoetigt(); i++){
			demandName[i]			= this.modelData.getRequiredFertiliser().get(i).getName();
			demandVal[i]			= this.modelData.getRequiredFertiliser().get(i).getAmount();
		}

		String[] ingredientName 	= new String[this.getNrZutaten()];
		double[] purchase_priceVal 	= new double[this.getNrZutaten()];
		for(int j=0; j<this.getNrZutaten(); j++){
			ingredientName[j]		= this.modelData.getIngredients().getIngredient().get(j).getName();
			purchase_priceVal[j]	= this.modelData.getIngredients().getIngredient().get(j).getPrice();
		}
		
		double[][] aVal 			= new double[this.getNrZutaten()][this.getNrDuengerVorhanden()];
		for(int j=0; j<this.getNrDuengerVorhanden(); j++){
			PresentFertiliser presentFertilizer = this.modelData.getPresentFertiliser().get(j);
			for(int i=0; i<this.getNrZutaten(); i++) aVal[i][j] = 0.0;
			for(int i=0; i<presentFertilizer.getPresentIngredients().getPresentIngredient().size(); i++){
				PresentIngredient presentIngredient = presentFertilizer.getPresentIngredients().getPresentIngredient().get(i);
				int ingredientIndex = presentIngredient.getIngredientId();
				aVal[ingredientIndex][j]	= presentIngredient.getPercent()/100.0;
			}
		}

		double[][] aMinVal 			= new double[this.getNrZutaten()][this.getNrDuengerBenoetigt()];
		double[][] aMaxVal 			= new double[this.getNrZutaten()][this.getNrDuengerBenoetigt()];
		for(int j=0; j<this.getNrDuengerBenoetigt(); j++){
			RequiredFertiliser requiredFertilizer = this.modelData.getRequiredFertiliser().get(j);
			for(int i=0; i<this.getNrZutaten(); i++) {aMinVal[i][j]	= 0.0; aMaxVal[i][j]	= 0.0; }
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
				for(int j=0; j<this.getNrDuengerBenoetigt(); j++){
					index[1] = (String)demandSet.get(j);
					Production production = factory.createProduction();
					production.setRequiredFertillserId(j);
					production.setAmount((Double)(z.get(index[1]).activity()));
					for(int i=0; i<this.getNrDuengerVorhanden(); i++){
						index[0] = (String)supplySet.get(i);
						UsedFertiliser usedFertilizer = factory.createUsedFertiliser();
						usedFertilizer.setPresentFertillserIdId(i);
						usedFertilizer.setQuantity((Double)(x.get(index).activity()));
						production.getUsedFertiliser().add(usedFertilizer);
					}
					for(int i=0; i<this.getNrZutaten(); i++){
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
}