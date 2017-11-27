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
	 * Dieses Attribut ist für die Bearbeitung von vorhandenen Fertilisers da.
	 * 
	 */
	private int ChangePresentFertiliserId = -1;
	
	/**
	 * Name des aktuellen Nutzers
	 */
	private String 	nutzer;
	/**
	 * Das aktuelle Modell
	 */
	private Model	model;
	
	private Fertiliser		modelData;
	
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
	
	public void setModel(String modelId){
		//System.out.println("EDbean:setModel  modelId:"+modelId);
		this.model		= Model.get(modelId);
		this.modelData	= this.model.getModelData();
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
			System.out.println(i);
			if (this.modelData.getIngredients().getIngredient().get(i).getName().equals(name)){
				result = this.modelData.getIngredients().getIngredient().get(i).getId();
			}
		}
		System.out.println(result);
		return result;
	}
	
	public int getNrDüngerVorhanden(){
		return this.modelData.getPresentFertiliser().size();
	}
	
	public int getNrDüngerBenötigt(){
		return this.modelData.getRequiredFertiliser().size();
	}
	
	public int getNrLösungen(){
		return this.modelData.getSolution().size();
	}
	
	public void setChangePresentIngredientId(String sId){
		int id = Integer.parseInt(sId);
		this.ChangePresentIngredientId = id;
		System.out.println("ChangePresentIngredientId hat den wert von: "+id);
	}
	
	public int getChangePresentIngredientId(){
		return ChangePresentIngredientId;
	}
	
	public void setChangePresentFertiliser(String sId){
		int id = Integer.parseInt(sId);
		this.ChangePresentFertiliserId = id;
		System.out.println("ChangePresentFertiliser hat einen Wert von: " + id);
	}
	
	public int getChangePresentFeritliser(){
		return this.ChangePresentFertiliserId;
	}
	
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
	
	public void addPresentIngredient(String fId, String name, String percent){
		int presentIngredientId = this.getNrZutaten(name);
		System.out.println("Die PresentFertiliserId " + fId);
		System.out.println("Die Id der Zutat " + presentIngredientId);
		try{
			int presentFertiliserId = Integer.parseInt(fId);
			double p = Double.parseDouble(percent);
			this.aktualisierePercent(presentFertiliserId, presentIngredientId, p);
			this.model.addPresentIngredient(presentFertiliserId, presentIngredientId, p);
			System.out.println("Es wurde ein PresentIngredient angelegt");
		}catch(NumberFormatException e){
			this.model.addPresentIngredient(0, 0, 0.0);
		}
	}
	
	public void changePresentIngredient(String fId, String s_presentIngredientId, String percent){
		int presentIngredientId = Integer.parseInt(s_presentIngredientId);
		try{
			int presentFertiliserId = Integer.parseInt(fId);
			double p = Double.parseDouble(percent);
			this.aktualisierePercent(presentFertiliserId, presentIngredientId, p);
			this.changePresentIngredient(presentFertiliserId, presentIngredientId, p);
			System.out.println("Es wurde ein PresentIngredient geändert");
		}catch(NumberFormatException e){
			System.out.println("Es konnte der PresentIngredient nicht geändert werden");
		}
	}
	
	public void changePresentIngredient(int fertiliserId, int presentIngredientId, double percent){
		this.model.changePresentIngredient(fertiliserId, presentIngredientId, percent);
		
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
	 * Diese Funktion aktualisiert die Prozentwerte der vorhandenen PresentIngrediens, wenn der Gesamtwert über 100 % sein sollte.
	 * Die Prozente werden automatisch angepasst, sodass das gleiche Verhältnis bestehen bleibt.
	 * 
	 * @param fId
	 * @param presentIngredientId
	 * @param percentOff
	 * 
	 * Autor: Eddi M.
	 */
	public void aktualisierePercent(int fId, int presentIngredientId, double percent){
		System.out.println("Die FertiliserId lautet: " + fId);
		PresentFertiliser fertiliser = this.modelData.getPresentFertiliser().get(fId);
		double sumPercent = 0.0;
		for(int j=0; j< fertiliser.getPresentIngredients().getPresentIngredient().size(); j++){
			if (j != presentIngredientId ){
				System.out.println("j hat den Wert: " + j + " der Wert von PresentIngredientId ist: " + presentIngredientId);
				PresentIngredient pi = fertiliser.getPresentIngredients().getPresentIngredient().get(j);
				sumPercent += pi.getPercent();
			}
		}
		sumPercent += percent;
		System.out.println("Die Summer der Percent aller Ingredients lautet: " + sumPercent);
		if (sumPercent > 100){
			System.out.println("SumPercent: " + sumPercent + " PersentOff: " + percent);
			for(int j=0; j< fertiliser.getPresentIngredients().getPresentIngredient().size(); j++){
				if (j != presentIngredientId ){
					PresentIngredient pi = fertiliser.getPresentIngredients().getPresentIngredient().get(j);
					
					double neuPercent = runden((100 - percent) * (pi.getPercent()/(sumPercent - percent)), 2);
					System.out.println("Der neues Prozentwert beträgt " + neuPercent );
					changePresentIngredient(fId, j, neuPercent);
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
	
	/**
	 * liefert HTML String f�r ZutatenTableau
	 * @return
	 */
	public String getZutatenTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"5\">Zutaten</th></tr>\n";
		out += "<tr><th>Id</th><th>Name</th><th>Preis [&euro;/Einheit]</th><th>Einheit</th></tr>\n";
		for (int i=0; i< this.getNrZutaten(); i++ ){
			out += "<tr>";
			
			if (i == this.getChangeIngredientId()){
				out += "<form action=\"Controller\" method=\"post\" />";
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getId()+"</td>";
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
				out += "<td><input type=\"submit\" value=\"save\" /></td>";
				out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+this.modelData.getIngredients().getIngredient().get(i).getId()+"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"07_saveEditIngredient\"/>";
				out += "</form>";
			}else{
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getId()+"</td>";
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getName()+"</td>";
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getPrice()+"</td>";
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getUnit()+"</td>";
				out += "<form action=\"Controller\" method=\"post\" />";
				out += "</select>";
				out += "<td><input type=\"submit\" value=\"edit\" /></td>";
				out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+i+"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"06_editIngredient\"/>";
				out += "</form>";
			}
			out += "</tr>\n";
		}
		out += "<form action=\"Controller\" method=\"post\" />";
		out += "<tr>";
		out += "<td bgcolor=\"black\"></td>";
		out += "<td><input type=\"text\" name=\"addIngredientName\" /></td>";
		out += "<td><input type=\"text\" name=\"addIngredientPrice\" /></td>";
		out += "<td><select name=\"addIngredientUnit\" >";
		for( Units u : Units.values()){
			out += "<option>"+u.value()+"</option>";
		}
		out += "</select>";
		out += "<td><input type=\"submit\" value=\"add\" /></td>";
		out += "<input type=\"hidden\" name=\"action\" value=\"04_addIngredient\"/>";
		out += "</tr>\n";
		out += "</form>";
		out += "</table>\n";
		return out;
	}
	
	/**
	 * liefert HTML String für Vorhandenen Dünger
	 * @return
	 */
	public String getVorhandenDüngerTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"4\">Vorhandene Duenger</th></tr>\n";
		
		for (int i=0; i< this.getNrDüngerVorhanden(); i++ ){
			PresentFertiliser fertiliser = this.modelData.getPresentFertiliser().get(i);
			out += "<tr><td colspan=\"4\">";
			out += "<b>ID: </b>"+fertiliser.getId()+"<br/>";
			out += "<b>Name: </b>"+fertiliser.getName()+"<br/>";
			out += "<b>Bestand: </b>"+fertiliser.getAmount()+"<br/>";
			out += "<b>Einheit: </b>"+fertiliser.getUnit()+"<br/>";
			out += "<b>Bestandteile:</b>";
			out += "</td></tr>\n";
			out += "<tr><th>Id</th><th>Name</th><th>%</th></tr>\n";
			for(int j=0; j< fertiliser.getPresentIngredients().getPresentIngredient().size(); j++){
				PresentIngredient pi = fertiliser.getPresentIngredients().getPresentIngredient().get(j);
				out += "<tr>";
				out += "<td>"+pi.getIngredientId()+"</td>";
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(pi.getIngredientId()).getName()+"</td>";
				
				if (i == this.getChangePresentFeritliser() && j == this.getChangePresentIngredientId()){
					out += "<form action=\"Controller\" method=\"post\" />";
					
					out += "<td><input type=\"text\" name=\"changeIngredientPercent\" value=\""+pi.getPercent()+"\"/></td>";
					
					out += "<td><input type=\"submit\" value=\"save\" /></td>";
					out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+i+"\"/>";
					out += "<input type=\"hidden\" name=\"presentIngredientId\" value=\""+j+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"09_saveEditPresentIngredient\"/>";
					out += "</form>";
				}else{
					out += "<td>"+pi.getPercent()+"</td>";
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "</select>";
					out += "<td><input type=\"submit\" value=\"edit\" /></td>";
					out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+i+"\"/>";
					out += "<input type=\"hidden\" name=\"presentIngredientId\" value=\""+j+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"08_editPresentIngredient\"/>";
					out += "</form>";
				}
				out += "</tr>\n";
			}
			
			//-----------------------------------------------------------
			
			/**
			 * Neue Zutaten können hinzugefügt werden.
			 * Zeile zum einfügen.
			 * autor: Edgar Muss
			 */
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<tr>";
			out += "<td bgcolor=\"black\"></td>";
			out += "<td><select name=\"addPresentIngredientName\" >";
			for( int g=0; g< this.getNrZutaten(); g++ ){
				out += "<option>"+this.modelData.getIngredients().getIngredient().get(g).getName()+"</option>";
			}
			out += "</select></td>";
			out += "<td><input type=\"text\" name=\"addIngredientPercent\" /></td>";
			out += "<td><input type=\"submit\" value=\"add\" /></td>";
			out += "<input type=\"hidden\" name=\"presetFertiliserId\" value=\""+i+"\">";
			out += "<input type=\"hidden\" name=\"action\" value=\"05_addPresentIngredient\"/>";
			out += "</tr>\n";
			out += "</form>";
			
			//-----------------------------------------------------------
			
			out += "<br>";
		}
		out += "</table>\n";
		return out;
	}
	
	/**
	 * liefert HTML String für benötigte Dünger
	 * @return
	 */
	public String getBenötigteDüngerTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"4\">Benoetigte Duenger</th></tr>\n";
		
		for (int i=0; i< this.getNrDüngerBenötigt(); i++ ){
			RequiredFertiliser fertiliser = this.modelData.getRequiredFertiliser().get(i);
			out += "<tr><td colspan=\"4\">";
			out += "<b>ID: </b>"+fertiliser.getId()+"<br/>";
			out += "<b>Name: </b>"+fertiliser.getName()+"<br/>";
			out += "<b>Bestand: </b>"+fertiliser.getAmount()+"<br/>";
			out += "<b>Einheit: </b>"+fertiliser.getUnit()+"<br/>";
			out += "<b>Bestandteile:</b>";
			out += "</td></tr>\n";
			out += "<tr><th>Id</th><th>Name</th><th>min %</th><th>max %</th></tr>\n";
			for(int j=0; j< fertiliser.getRequiredIngredients().getRequiredIngredient().size(); j++){
				RequiredIngredient ri = fertiliser.getRequiredIngredients().getRequiredIngredient().get(j);
				out += "<tr>";
				out += "<td>"+ri.getIngredientId()+"</td>";
				out += "<td>"+this.modelData.getIngredients().getIngredient().get(ri.getIngredientId()).getName()+"</td>";
				out += "<td>"+ri.getPercentMin()+"</td>";
				out += "<td>"+ri.getPercentMax()+"</td>";
				out += "</tr>\n";
			}
		}
		out += "</table>\n";
		return out;
	}
	
	public String getSolutionTableau(){
		String out = "";
		out += "<table border=\"1\">\n";
		out += "<tr><th colspan=\"3\">Loesung</th></tr>\n";
		out += "<tr><th align=\"left\" colspan=\"2\">Solver Status: </th><td colspan=\"1\">"+this.modelData.getSolverStatus()+"</td></tr>\n";
		
		if(this.isSolved()){
			for (int i=0; i< this.getNrLösungen(); i++ ){
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
		String[] supplyName 		= new String[this.getNrDüngerVorhanden()];
		double[] stockVal 			= new double[this.getNrDüngerVorhanden()];
		for(int i=0; i<this.getNrDüngerVorhanden(); i++){
			supplyName[i]			= this.modelData.getPresentFertiliser().get(i).getName();
			stockVal[i]				= this.modelData.getPresentFertiliser().get(i).getAmount();
		}

		String[] demandName 		= new String[this.getNrDüngerBenötigt()];
		double[] demandVal 			= new double[this.getNrDüngerBenötigt()];
		for(int i=0; i<this.getNrDüngerBenötigt(); i++){
			demandName[i]			= this.modelData.getRequiredFertiliser().get(i).getName();
			demandVal[i]			= this.modelData.getRequiredFertiliser().get(i).getAmount();
		}

		String[] ingredientName 	= new String[this.getNrZutaten()];
		double[] purchase_priceVal 	= new double[this.getNrZutaten()];
		for(int j=0; j<this.getNrZutaten(); j++){
			ingredientName[j]		= this.modelData.getIngredients().getIngredient().get(j).getName();
			purchase_priceVal[j]	= this.modelData.getIngredients().getIngredient().get(j).getPrice();
		}
		
		double[][] aVal 			= new double[this.getNrZutaten()][this.getNrDüngerVorhanden()];
		for(int j=0; j<this.getNrDüngerVorhanden(); j++){
			PresentFertiliser presentFertilizer = this.modelData.getPresentFertiliser().get(j);
			for(int i=0; i<this.getNrZutaten(); i++) aVal[i][j] = 0.0;
			for(int i=0; i<presentFertilizer.getPresentIngredients().getPresentIngredient().size(); i++){
				PresentIngredient presentIngredient = presentFertilizer.getPresentIngredients().getPresentIngredient().get(i);
				int ingredientIndex = presentIngredient.getIngredientId();
				aVal[ingredientIndex][j]	= presentIngredient.getPercent()/100.0;
			}
		}

		double[][] aMinVal 			= new double[this.getNrZutaten()][this.getNrDüngerBenötigt()];
		double[][] aMaxVal 			= new double[this.getNrZutaten()][this.getNrDüngerBenötigt()];
		for(int j=0; j<this.getNrDüngerBenötigt(); j++){
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
				for(int j=0; j<this.getNrDüngerBenötigt(); j++){
					index[1] = (String)demandSet.get(j);
					Production production = factory.createProduction();
					production.setRequiredFertillserId(j);
					production.setAmount((Double)(z.get(index[1]).activity()));
					for(int i=0; i<this.getNrDüngerVorhanden(); i++){
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
