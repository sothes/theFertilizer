package bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
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
	public static final String ModelDir 	= "C:\\Users\\jonas.goslicki\\OneDrive - Outsmart Group B.V\\Uni\\3.Semster\\Dynamisches Internetworking\\Belegarbeit\\Eddy_Eric_Jonas_Version\\Mueller_Project\\testDir\\";
	//public static final String ModelDir 		= "/home/mitarbeiter/cmueller/or_model/Fertilizer/";

	public static final String CmplModel 		= "C:\\Users\\jonas.goslicki\\OneDrive - Outsmart Group B.V\\Uni\\3.Semster\\Dynamisches Internetworking\\Belegarbeit\\Eddy_Eric_Jonas_Version\\Mueller_Project\\cmpl\\Fertilizer.cmpl";
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
	
	/*
	 * F�rs Nutzermanagment
	 */
	
	private String filepath	= ModelDir+"user.usr";
	private File f 			= new File(filepath);
	private int linefile	= 0;
	private String[] LineArray;
	private boolean	rightcheck	= false;
	
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
	
	public String getId() {
		return this.modelData.getId();
	}
	
	public void setShowDeletedIngredient(boolean b){
		this.setAddPresentIngredients("0", false);
		this.ShowDeletedIngredients = b;
	}
	
	public boolean getShowDeletedIngredient(){
		return this.ShowDeletedIngredients;
	}
	
	public void setModel(String modelId){
		this.model		= Model.get(modelId);
		this.modelData	= this.model.getModelData();
		this.setAddPresentIngredients("0", false);
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
	
	public void setAddIngredient(boolean b){
		if (b == true){
			this.setAddPresentIngredients("0", false);
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
		}else {
			sichtbarkeitAsInt = 0;
		}
		 
		this.AddPresentIngredients[0] = presentFertiliserId;
		this.AddPresentIngredients[1] = sichtbarkeitAsInt;
	}
	
	public int[] getAddPresentIngredients(){
		return this.AddPresentIngredients;
	}
	
	public void setShowID(boolean b){
		this.showID = b;
	}
	
	public boolean getShowID(){
		return this.showID;
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
	
	public void addOrChangePresentIngredient(String fId, String name, String percent){
		int presentIngredientId, presentFertiliserId;
		double p;
		presentIngredientId = this.getNrZutaten(name);
		System.out.println("----Der presentIngredientId ist: "+ presentIngredientId);
		
		try {
			presentFertiliserId = Integer.parseInt(fId);
			p = Double.parseDouble(percent);
		} catch(NumberFormatException e){
			presentFertiliserId = 0;
			p = 0.0;
		}
		
		if (this.model.getIdOfPresentIngredientsFromPresentFertiliser(model.getPresentFertiliser(presentFertiliserId)).contains(presentIngredientId) == true){
			
			this.changePresentIngredient(presentFertiliserId, presentIngredientId, 0);
			this.aktualisierePercent(presentFertiliserId, presentIngredientId, p);
			this.changePresentIngredient(presentFertiliserId, presentIngredientId, p);
		}else {
			this.aktualisierePercent(presentFertiliserId, presentIngredientId, p);
			this.addPresentIngredient(presentFertiliserId, presentIngredientId, p);
		}
	}
	
	public void addPresentIngredient(int fertiliserId, int presentIngredientId, double percent){
		this.model.addPresentIngredient(fertiliserId, presentIngredientId, percent);
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
		
		PresentFertiliser fertiliser = this.modelData.getPresentFertiliser().get(fId);
		
		double sumPercent = 0.0;
		sumPercent += this.getTotalPercentOfPresentIngredients(fId);
		sumPercent += percent;
		if (sumPercent > 100){
			for (int i=0; i< this.getNrZutaten(); i++){
				if (this.modelData.getIngredients().getIngredient().get(i).isActive() == true && this.modelData.getIngredients().getIngredient().get(i).getActive() == true && model.getIdOfPresentIngredientsFromPresentFertiliser(model.getPresentFertiliser(fId)).contains(i) == true ){
					if (i != presentIngredientId ){
						PresentIngredient pi = model.getPresentIngredient(fertiliser, i);
						double neuPercent = runden((100 - percent) * (pi.getPercent()/(sumPercent - percent)), 2);
						changePresentIngredient(fId, i, neuPercent);
					}
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
	 * liefert HTML String fuer ZutatenTableau
	 * @return
	 */
	public String getZutatenTableau(){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = getUserOverview();
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
			out += "<input type=\"submit\" value=\"dont show deleted\"  class='btn btn-xs btn-danger'/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"24_dontshowDeletedIngredients\"/>";
			out += "</form>";
		}else {
			if(right.equals("x") || right.equals("w")) {
				out += "<form action=\"Controller\" method=\"post\" />";
				out += "<input type=\"submit\" value=\"show deleted\"  class='btn btn-xs btn-danger'/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"23_showDeletedIngredients\"/>";
				out += "</form>";
			}else {
				out += "";
			}
		}
		out += "</th><th>Name</th><th>Preis [&euro;/Einheit]</th><th>Einheit</th>";
		out += "<form action=\"Controller\" method=\"post\" />";
		if (this.AddIngredient == false){
			if(right.equals("x") || right.equals("w")) {
				out += "<td>";
				out += "<input id=\"image\" type=\"image\" src=\"if_add_326505.png\" alt=\"add451\" />";
				out += "</td></tr>\n";
				out += "<input type=\"hidden\" name=\"action\" value=\"20_showRowAdding\"/>";
			}else {
				out += "<td></td></tr>";
			}
		}else if (this.AddIngredient == true){
			out += "<td>";
			out += "<input id=\"image\" type=\"image\" src=\"if_Arrow_Back_1063891.png\" alt=\"back456\" />";
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
					out += this.getDeleteButton(i);
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getName()+"</td>";
					out += "<td><input type=\"text\" class=\"form-control\" name=\"addIngredientPrice\" value=\""+this.modelData.getIngredients().getIngredient().get(i).getPrice()+ "\"/></td>";
					out += "<td><select name=\"addIngredientUnit\" class=\"form-control\">";
					for( Units u : Units.values()){
						if (u.value() == this.modelData.getIngredients().getIngredient().get(i).getUnit().value()){
							out += "<option selected>"+u.value()+"</option>";
						}else{
							out += "<option>"+u.value()+"</option>";
						}
					}
					out += "</select>";
					out += "<td><input type=\"submit\" class=\"btn btn-success\" value=\"save\" /></td>";
					out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+this.modelData.getIngredients().getIngredient().get(i).getId()+"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"07_saveEditIngredient\"/>";
					out += "</form>";
				}else{
					if (this.showID == true){
						out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getId()+"</td>";
					}
					out += this.getDeleteButton(i);
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getName()+"</td>";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getPrice()+"</td>";
					out += "<td>"+this.modelData.getIngredients().getIngredient().get(i).getUnit()+"</td>";
					out += "<form action=\"Controller\" method=\"post\" />";
					out += "</select>";
					if(right.equals("x") || right.equals("w")) {
						out += "<td><input type=\"image\" src=\"if_Pencil_1021030.png\" alt=\"edit501\" /></td>";
						out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+i+"\"/>";
						out += "<input type=\"hidden\" name=\"action\" value=\"06_editIngredient\"/>";
						out += "</form>";
					}else {
						out += "<td></td></form>";
					}
					
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
	 * Diese Funktion fügt die den delete button in die Tabelle ein.
	 * 
	 * @parm Integerwert der die ID des Ingredients wiedergibt.
	 * @return Es wird der Button als html String zurück gegeben
	 */
	private String getDeleteButton(int ingredientId){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		if (this.ShowDeletedIngredients == true && this.modelData.getIngredients().getIngredient().get(ingredientId).getActive() == false){
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			out += "<input id=\"image\" type=\"image\" src=\"if_edit-add_9254.png\" alt=\"Hinzuf�gen529\" />";
			out += "</td>";
			out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+ingredientId+"\"/>";
			out += "<input type=\"hidden\" name=\"action\" value=\"25_undeleteIngredients\" />";
			out += "</form>";
		}else{
			out += "<form action=\"Controller\" method=\"post\" />";
			out += "<td>";
			if(right.equals("x") || right.equals("w")) {
				out += "<input id=\"image\" type=\"image\" src=\"if_edit-delete_9259.png\" alt=\"X537\" />";
			}
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
		out += "<td><input id=\"image\" type=\"image\" src=\"if_add_326505.png\" alt=\"add473\" /></td>";
		out += "<input type=\"hidden\" name=\"action\" value=\"04_addIngredient\"/>";
		out += "</tr>\n";
		out += "</form>";
		
		System.out.println("getRowAddIngredients wurde ausgeführt");
		System.out.println(out);
		return out;
	}
	
	/**
	 * liefert HTML String für Vorhandenen Dünger
	 * @return
	 */
	public String getVorhandenDuengerTableau(){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		out += "<div class=\"container\">";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		if (this.showID == true){
			out += "<tr><td colspan=\"5\">";
		}else{
			out += "<tr><td colspan=\"4\">";
		}
		out += "<u>Vorhandene Duenger</u></th></tr>\n";
		
		for (int i=0; i< this.getNrDuengerVorhanden(); i++ ){
			PresentFertiliser fertiliser = this.modelData.getPresentFertiliser().get(i);
			if (this.showID == true){
				out += "<tr><td colspan=\"5\">";
			}else{
				out += "<tr><td colspan=\"4\">";
			}
			out += "<b>ID: </b>"+fertiliser.getId()+"<br/>";
			out += "<b>Name: </b>"+fertiliser.getName()+"<br/>";
			out += "<b>Bestand: </b>"+fertiliser.getAmount()+"<br/>";
			out += "<b>Einheit: </b>"+fertiliser.getUnit()+"<br/>";
			out += "<b>Bestandteile:</b>";
			out += "</td></tr>\n";
			out += "<tr>";
			if (this.showID == true){
				out += "<th>Id</th>";
			}
			out += "<th>delete</th>";
			out += "<th>Name</th><th>";
			out += " " + this.getTotalPercentOfPresentIngredients(i) + " %</th>";
			out += "<th>";
			
			// Addbutton im Menu
			out += "<form action=\"Controller\" method=\"post\" />";
			if (this.AddPresentIngredients[1] == 1 && this.AddPresentIngredients[0] == i){
				out += "<input id=\"image\" type=\"image\" src=\"if_Arrow_Back_1063891.png\" alt=\"back622\" />";
				out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
				out += "<input type=\"hidden\" name=\"action\" value=\"27_NoShowRowPresentIngredientAdding\"/>";
			}else{
				if(right.equals("x") || right.equals("w")) {
					out += "<input id=\"image\" type=\"image\" src=\"if_add_326505.png\" alt=\"add626\" />";
					out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+ i +"\"/>";
					out += "<input type=\"hidden\" name=\"action\" value=\"26_showRowPresentIngredientAdding\"/>";
				}
			}
			out += "</form>";
			
			out += "</th>";
			out += "</tr>\n";
			for(int j=0; j< this.getNrZutaten(); j++){
				
				if (this.modelData.getIngredients().getIngredient().get(j).isActive() == false){
					this.setIngredientActive(Integer.toString(j), true);
				}
				
				if (this.modelData.getIngredients().getIngredient().get(j).isActive() == true &&
					this.modelData.getIngredients().getIngredient().get(j).getActive() == true &&
					model.getIdOfPresentIngredientsFromPresentFertiliser(fertiliser).contains(j) == true){
					
					int index = model.getIndexOfPresentIngredientFromIngredientId(fertiliser, j);
					PresentIngredient pi = fertiliser.getPresentIngredients().getPresentIngredient().get(index);
					if (pi.isActive() == false){
						this.setPresentIngredientActive(fertiliser, Integer.toString(j), true);
					}
					if (pi.isActive() == true && pi.getActive() == true){
					
						out += "<tr>";
						if (this.showID == true){
							out += "<td>"+pi.getIngredientId()+"</td>";
						}
						out += this.getDeleteButton(i, index);
						out += "<td>"+this.modelData.getIngredients().getIngredient().get(pi.getIngredientId()).getName()+"</td>";
						
						if (i == this.getChangePresentFeritliser() && index == this.getChangePresentIngredientId()){
							out += "<form action=\"Controller\" method=\"post\" />";
							out += "<td><input type=\"text\" name=\"changeIngredientPercent\" value=\""+pi.getPercent()+"\"/></td>";
							out += "<td><input type=\"submit\" value=\"save\" /></td>";
							out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+i+"\"/>";
							out += "<input type=\"hidden\" name=\"presentIngredientId\" value=\""+index+"\"/>";
							out += "<input type=\"hidden\" name=\"action\" value=\"09_saveEditPresentIngredient\"/>";
							out += "</form>";
						}else{
							//System.out.println("ich bin noch innerhalb der if");
							out += "<td>"+pi.getPercent()+"</td>";
							out += "<form action=\"Controller\" method=\"post\" />";
							out += "</select>";
							if(right.equals("w") || right.equals("x")) {
								out += "<td><input type=\"image\" src=\"if_Pencil_1021030.png\" alt=\"edit671\" /></td>";
							}else {
								out += "<td></td>";
							}
							
							out += "<input type=\"hidden\" name=\"presentFertiliserId\" value=\""+i+"\"/>";
							out += "<input type=\"hidden\" name=\"presentIngredientId\" value=\""+index+"\"/>";
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
		
		out += "</table></span></div>\n";
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
		out += "<td><input id=\"image\" type=\"image\" src=\"if_add_326505.png\" alt=\"add718\" /></td>";
		out += "<input type=\"hidden\" name=\"presetFertiliserId\" value=\""+presentFertiliserId+"\">";
		out += "<input type=\"hidden\" name=\"action\" value=\"05_addPresentIngredient\"/>";
		out += "</tr>\n";
		out += "</form>";
		
		return out;
	}
	
	private String getDeleteButton(int presentFertiliserId, int ingredientId){
		String right = GetUserRightInModel(model.getId(),getNutzer());
		String out = "";
		
		out += "<form action=\"Controller\" method=\"post\" />";
		out += "<td>";
		if(right.equals("x") || right.equals("w")) {
			out += "<input id=\"image\" type=\"image\" src=\"if_edit-delete_9259.png\" alt=\"X732\" />";
		}
		out += "</td>";
		out += "<input type=\"hidden\" name=\"fertiliserId\" value=\""+presentFertiliserId+"\"/>";
		out += "<input type=\"hidden\" name=\"ingredientId\" value=\""+ingredientId+"\"/>";
		out += "<input type=\"hidden\" name=\"action\" value=\"28_deletePresentIngredient\" />";
		out += "</form>";
	
		return out;
		
		//---------
		//this.modelData.getPresentFertiliser().get(0).getPresentIngredients().getPresentIngredient().get(3);
		//---------
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
		
		for (int i=0; i< this.getNrZutaten(); i++){
			if (this.modelData.getIngredients().getIngredient().get(i).isActive() == true && this.modelData.getIngredients().getIngredient().get(i).getActive() == true && model.getIdOfPresentIngredientsFromPresentFertiliser(model.getPresentFertiliser(presentFertiliserId)).contains(i) == true ){
				int index = model.getIndexOfPresentIngredientFromIngredientId(model.getPresentFertiliser(presentFertiliserId), i);
				sum += this.modelData.getPresentFertiliser().get(presentFertiliserId).getPresentIngredients().getPresentIngredient().get(index).getPercent();
				
				//System.out.println("Percent: " + this.modelData.getPresentFertiliser().get(presentFertiliserId).getPresentIngredients().getPresentIngredient().get(index).getPercent() + " Fertiliser: " + presentFertiliserId);
			}
			
		}
		return this.runden(sum, 2);
	}
	
	/**
	 * liefert HTML String fuer benoetigte Duenger
	 * @return
	 */
	public String getBenoetigteDuengerTableau(){
		String out = "";
		out += "<div class=\"container\" >";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		out += "<tr><th colspan=\"4\"><u>Ben�tigte Duenger</u></th></tr>\n";
		
		for (int i=0; i< this.getNrDuengerBenoetigt(); i++ ){
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
		out += "</table> </div>\n";
		return out;
	}
	
	public String getSolutionTableau(){
		String out = "";
		out += "<div class=\"container\">";
		out += "<table border=\"1\" class='table table-bordered' style=' text-align: center;color:white' >\n";
		out += "<tr><th colspan=\"3\"><u>L�sung</u></th></tr>\n";
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
						out += "<td colspan=\"3\"><u>Zutaten:<u></td>";
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
		out += "</table></div>\n";
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
	 * @param ingredientId - String
	 * @param b - boolean
	 * 
	 * Autor: Eddi M.
	 */
	public void setPresentIngredientActive(PresentFertiliser presentFertiliser, String ingredientId, boolean b){
		int index, id = Integer.parseInt(ingredientId);
		index = model.getIndexOfPresentIngredientFromIngredientId(presentFertiliser, id);
		PresentIngredient presentIngredient = presentFertiliser.getPresentIngredients().getPresentIngredient().get(index);
		
		model.setPresentIngredientActive(presentIngredient, b);
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
		out += "<td><input type=\"text\" name=\"modelId\" size=\"10\" ></td>";
		out += "<td><input type=\"text\" name=\"modelName\" size=\"10\" ></td>";
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
	 * fuegt ein neues Modell hinzu
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
		out += "<thead class='thead-light'><tr><td colspan='3'>Rechte�bersicht</td></tr></thead>";
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
				out += "<tr><td>"+lineUser[0]+"</td><td>"+rechteName+"</td><td><input type=\"submit\" class=\"btn btn-danger\" value=\"L�schen\" /></td></tr>";
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
