package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;


import servlet.Controller;
import xmlData.Fertiliser;
import xmlData.Ingredient;
import xmlData.ObjectFactory;
import xmlData.PresentFertiliser;
import xmlData.PresentIngredient;
import xmlData.RequiredFertiliser;
import xmlData.RequiredIngredient;
import xmlData.Solution;
import xmlData.Units;

/**
 * Diese Klasse speichert und liest alle Modelldaten in/aus 
 * einer xml Datei Detailinformationen sind der Methode getModelData zu entnehmen.
 * Ausserdem werden alle bestehenden Modelle in einer 
 * Hashtabelle verwaltet.
 * @author Christian
 *
 */
public class Model {

	static Logger logger = Logger.getLogger(Model.class);

	/**
	 * Die relative Wurzel f�r die Modelldateien
	 */
	static String basisPfad = null;
	/**
	 * interne Hashtabelle, die jedem Modellnamen, die entsprechende Klassen-Instanz zuweist.
	 */
	static Hashtable<String, Model> modelle = null;
	
	private JAXBContext 		jaxbContext;
	private Fertiliser 			modelData = null;
	private ObjectFactory 		factory = null;
	private String   			datei = null;
	
	/**
	 * liefert die Hashtable mit allen erzeugten Modellen
	 * @return
	 */
	public static Hashtable<String, Model> getModelle(){
		return Model.modelle;
	}
	
	public static void setBasisPfad(String pfad){
		Model.basisPfad = pfad;
	}
	
	/**
	 * einlesen aller Modell Dateien
	 */
	public static void init(){
		Model.modelle = new Hashtable<String, Model>();
		File basis = new File(Model.basisPfad);
		//System.out.println("basisPfad: "+basis.getAbsolutePath());
		if(basis.isDirectory()){
			String[] mo = basis.list();
			File mo_file = null;
			if(mo != null){
				for(int i=0; i < mo.length; i++){
					mo_file = new File(basis.getAbsolutePath()+"/"+mo[i]);
					//System.out.println("gefunden: "+mo_file.getAbsolutePath());
					if(mo_file.isFile()){
						Model m = new Model(mo_file);
						if(m == null) System.out.println("Model.init  Einlesefehler von "+mo_file.getAbsolutePath());
						Model.modelle.put(m.getId(), m);
					}
				}
			}
		}
	}
	
	/**
	 * liefert das Modell mit modelId
	 * @param modelId
	 * @return
	 */
	public static Model get(String modelId){
		return Model.modelle.get(modelId);
	}
	
	/**
	 * f�gt neues Modell mit modelId der Hashtable hinzu
	 * @param modelId
	 * @param model
	 */
	public static void add(String modelId, Model model){
		if(! Model.modelle.containsKey(modelId)){
			Model.modelle.put(modelId, model);
			model.printDoc();
		}
	}
	
	/**
	 * entfernen eines Modells
	 * @param modelId
	 */
	public static void remove(String modelId){
		if( Model.modelle.containsKey(modelId)){
			Model model = Model.modelle.get(modelId);
			Model.modelle.remove(modelId);
			File f = new File(model.getDirectory()+model.datei);
			f.delete();
			//System.out.println(model.getDirectory()+model.datei);
		}
	}
	
	/**
	 * liefert Array mit den ID's aller Modelle
	 * @return
	 */
	public static String[] getModelIds(){
		int l = Model.modelle.size();
		String[] out	= new String[l];
		Enumeration<String> e = Model.modelle.keys();
		int i=0;
		while(e.hasMoreElements()){
			out[i++] = e.nextElement();
		}
		return out;
	}

	
	/**
	 * information Ueber alle erstellten Modelle
	 * @return
	 */
	public static String info(){
		String out = "";
		for(Enumeration e = Model.modelle.keys(); e.hasMoreElements(); ){
			Model model = Model.modelle.get(e.nextElement());
			out += model.modelData.getId()+"  "+model.modelData.getName();
			out += "\n";
		}
		return out;
	}
	
	/**
	 * Konstruktor zum Einlesen einer als xml-Datei abgespeicherten Modells
	 * 
	 * @param modelFile	File in dem das Model gespeichert ist
	 */
	public Model(File modelFile){
		this.factory	= new ObjectFactory();
		try {
			this.jaxbContext = JAXBContext.newInstance("xmlData");
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.datei 		= modelFile.getName();
		this.modelData	= null;
		//System.out.println("Datei : "+this.datei);
		if(modelFile.isFile()){
			//System.out.println(modelFile.getAbsolutePath());
			try {
				this.modelData = this.unmarshalXml(new FileInputStream(modelFile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Konstruktor zum Anlegen eines neuen Modells. 
	 * 
	 * @param mid				ModellId
	 * @param anzVariables
	 * @param anzConstraints
	 */
	/**
	 * @param mid
	 */
	/**
	 * @param mid
	 */
	public Model(String mid){
		this.datei = mid+".xml";
		this.factory 	= new ObjectFactory();
		try {
			this.jaxbContext = JAXBContext.newInstance("xmlData");
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// erzeugt Demo CakeEvent
		Ingredient 			ingredient;
		PresentFertiliser 	presentFertiliser;
		RequiredFertiliser 	requiredFertiliser;
		PresentIngredient	presentIngredient;
		RequiredIngredient	requiredIngredient;

		
		this.modelData 	= factory.createFertiliser();
		this.modelData.setId(mid);
		this.modelData.setName(mid);
		this.modelData.setCreatedAt(this.getTime());
		this.modelData.setAutor("Eddi Miller");
		this.modelData.setDescription("Mischduenger");
		this.modelData.setSolved(false);
		this.modelData.setSolverStatus("unsolved");
		this.modelData.setIngredients(factory.createIngredients());
		
		//Standard Zutaten
		ArrayList<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(createIngredient(0, "Nitrat", 450.0, Units.TONNEN));
		ingredients.add(createIngredient(1, "Phosphat", 300.0, Units.TONNEN));
		ingredients.add(createIngredient(2, "Torf", 80.0, Units.TONNEN));
		
		Iterator<Ingredient> iter = ingredients.iterator();
		while (iter.hasNext()){
			this.modelData.getIngredients().getIngredient().add(iter.next());
		}
		
		ArrayList<PresentIngredient> presentIngredients1 = new ArrayList<>();
		ArrayList<PresentIngredient> presentIngredients2 = new ArrayList<>();
		
		presentIngredients1.add(createPresentIngredient(0,15.0));
		presentIngredients1.add(createPresentIngredient(1,30.0));
		presentIngredients1.add(createPresentIngredient(2,55.0));
		
		presentIngredients2.add(createPresentIngredient(0,25.0));
		presentIngredients2.add(createPresentIngredient(1,10.0));
		presentIngredients2.add(createPresentIngredient(2,65.0));
		
		addPresentFertiliser(0, "Duenger1", 20.0, Units.TONNEN, presentIngredients1);
		addPresentFertiliser(1, "Duenger2", 20.0, Units.TONNEN, presentIngredients2);
	
		
		ArrayList<RequiredIngredient> requiredIngredients1 = new ArrayList<>();
		ArrayList<RequiredIngredient> requiredIngredients2 = new ArrayList<>();
		
		requiredIngredients1.add(createRequiredIngredient(0, 20.0, 25.0));
		requiredIngredients1.add(createRequiredIngredient(1, 15.0, 20.0));
		requiredIngredients1.add(createRequiredIngredient(2, 0.0, 100.0));
		
		requiredIngredients2.add(createRequiredIngredient(0, 25.0, 30.0));
		requiredIngredients2.add(createRequiredIngredient(1, 10.0, 15.0));
		requiredIngredients2.add(createRequiredIngredient(2, 0.0, 100.0));
		
		addRequiredFertiliser(0, "Duenger3", 10, Units.TONNEN, requiredIngredients1);
		addRequiredFertiliser(1, "Duenger4", 25, Units.TONNEN, requiredIngredients2);
	}
	
	/**
	 * Ermöglicht das Anlegen von neuen Ingredients
	 * 
	 * @param id
	 * @param name
	 * @param price
	 * @param unit
	 */
	public void addIngredient(int id, String name, double price, Units unit){
		Ingredient 	ingredient;
		
		ingredient = factory.createIngredient();
		ingredient.setId(id);
		ingredient.setName(name);
		ingredient.setUnit(unit);
		ingredient.setPrice(price);
		this.modelData.getIngredients().getIngredient().add(ingredient);
	}
	
	public void changeIngredient(Ingredient ingredient, double price, Units unit){
		ingredient.setPrice(price);
		ingredient.setUnit(unit);
	}
	
	/**
	 * Erstellt ein Element vom Typ Ingredient, welches zurück gegeben wird.
	 * 
	 * @param id
	 * @param name
	 * @param price
	 * @param unit
	 * @return
	 */
	public Ingredient createIngredient(int id, String name, double price, Units unit){
		Ingredient ingredient;
		
		ingredient = factory.createIngredient();
		ingredient.setId(id);
		ingredient.setName(name);
		ingredient.setUnit(unit);
		ingredient.setPrice(price);
		ingredient.setActive(true);
		return ingredient;
	}
	
	public void setIngredientActive(Ingredient ingredient, boolean b){
		ingredient.setActive(b);
	}
	
	/**
	 * Diese Funktion setzt den Status des uebergebenden PresentIngredient auf den boolischen Wert b.
	 * 
	 * @param presentIngredient - PresentIngredient
	 * @param b - Boolean
	 */
	public void setPresentIngredientActive(PresentIngredient presentIngredient, boolean b){
		presentIngredient.setActive(b);
	}
	
	/**
	 * Diese Funktion setzt den Status des uebergebenden RequiredIngredient auf den boolischen Wert b.
	 * 
	 * @param requiredIngredient - RequiredIngredient
	 * @param b - Boolean
	 */
	public void setRequiredIngredientActive(RequiredIngredient requiredIngredient, boolean b){
		requiredIngredient.setActive(b);
	}
	
	/**
	 * Erstellt ein RequiredIngredient und Speichert es unter dem mitgegebenden RequiredFertiliser
	 * 
	 * @param requiredFertiliser
	 * @param id
	 * @param percent
	 */
	public void addRequiredIngredient(RequiredFertiliser requiredFertiliser, int id, double percentMin, double percentMax){
		RequiredIngredient	requiredIngredient;
		
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(id);
		requiredIngredient.setPercentMin(percentMin);
		requiredIngredient.setPercentMax(percentMax);
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
	}
	
	/**
	 * Ermittelt die Ingredient anhand der Id
	 * 
	 * @param id
	 * @return
	 * 
	 * Autor: Edgar M.
	 */
	public Ingredient getIngredient(int id){
		Ingredient ingredient;
		
		ingredient = this.modelData.getIngredients().getIngredient().get(id);
		
		return ingredient;
	}
	
	/**
	 * Erstellt ein PresentIngredient und Speichert es unter dem mitgegebenden PresentFertiliser
	 * 
	 * @param presentFertiliser
	 * @param id
	 * @param percent
	 */
	public void addPresentIngredient(PresentFertiliser presentFertiliser, int id, double percent){
		PresentIngredient	presentIngredient;
		
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(id);
		presentIngredient.setPercent(percent);
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
	}
	
	/**
	 * Diese Funktion gibt den entsprechenden PresentFertiliser anhand seiner Id zurück.
	 * 
	 * @param id - Id des PresentFertilisers
	 * @return PresentFertiliser
	 * 
	 * Autor: Eddi M.
	 */
	public PresentFertiliser getPresentFertiliser(int id){
		PresentFertiliser presentFertiliser;
		presentFertiliser = this.modelData.getPresentFertiliser().get(id);
		return presentFertiliser;
	}
	
	/**
	 * Diese Funktion gibt den entsprechenden Required Fertiliser anhand seiner Id zurück.
	 * 
	 * @param id - Id des RequiredFertilisers
	 * @return RequiredFertiliser
	 * 
	 * Autor: Eddi M.
	 */
	public RequiredFertiliser getRequiredFertiliser(int id){
		RequiredFertiliser requiredFertiliser;
		requiredFertiliser = this.modelData.getRequiredFertiliser().get(id);
		return requiredFertiliser;
	}
	
	/**
	 * Ermittelt anhand des Index den PresentIngredient eines PresentFertiliser
	 * 
	 * @param presentFertiliser
	 * @param presentIngredientIndex - Integer
	 * @return
	 * 
	 * Autor: Eddi M.
	 */
	public PresentIngredient getPresentIngredient(PresentFertiliser presentFertiliser, int presentIngredientIndex){
		PresentIngredient presentIngredient;
		int fId = presentFertiliser.getId();
		presentIngredient = this.modelData.getPresentFertiliser().get(fId).getPresentIngredients().getPresentIngredient().get(presentIngredientIndex);
		return presentIngredient;
	}
	
	public void addPresentIngredient(int pFertiliserId, int id, double percent){
		PresentIngredient pi = createPresentIngredient(id, percent);
		PresentFertiliser pf = getPresentFertiliser(pFertiliserId);
		addPresentIngredient(pf, pi);
	}
	
	public void addRequiredIngredient(int FertiliserId, int id, double percentMin, double percentMax){
		RequiredIngredient requiredIngredient = createRequiredIngredient(id, percentMin, percentMax);
		RequiredFertiliser requiredFertiliser = getRequiredFertiliser(FertiliserId);
		addRequiredIngredient(requiredFertiliser, requiredIngredient);
	}
	
	/**
	 * Speichert ein PresentIngredient unter dem mitgegebendem PresentFertiliser
	 * 
	 * @param presentFertiliser
	 * @param presentIngredient
	 */
 	public void addPresentIngredient(PresentFertiliser presentFertiliser, PresentIngredient presentIngredient){
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
	}
	
	/**
	 * Die Funktion erstellt PresentIngredients, um die im Anschluss in einem Array zu speichern
	 * und einem PresentFertiliser zuordnen kann.
	 * 
	 * @param id
	 * @param percent
	 * @return
	 */
	public PresentIngredient createPresentIngredient(int id, double percent){
		PresentIngredient	presentIngredient;
		
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(id);
		presentIngredient.setPercent(percent);
		presentIngredient.setActive(true);
		return presentIngredient;
	}
	
	public void changePresentIngredient(int presentFertiliserId, int ingredientIndex, double percent){
		PresentFertiliser presentFertiliser = getPresentFertiliser(presentFertiliserId);
		changePresentIngredient(presentFertiliser, ingredientIndex, percent);
	}
	
	public void changePresentIngredient(PresentFertiliser presentFertiliser, int ingredientIndex, double percent){
		PresentIngredient	presentIngredient;
		
		presentIngredient = getPresentIngredient(presentFertiliser, ingredientIndex);
		presentIngredient.setPercent(percent);
	}
	
	/**
	 * Gibt eine Liste der PresentIngredientId eines PresentFertiliser wieder.
	 * 
	 * @param presentFertiliser - Object PresentFertiliser über welchem interiert wird, um die Ids zu erhalten.
	 * @return - Zurück gegeben wird eine ArrayListe mit allen Ids gespeichert als Integer.
	 */
	public ArrayList<Integer> getIdOfPresentIngredientsFromPresentFertiliser(PresentFertiliser presentFertiliser){
		ArrayList<Integer> listId = new ArrayList<Integer>();
		
		for (int i=0; i < presentFertiliser.getPresentIngredients().getPresentIngredient().size(); i++){
			listId.add(presentFertiliser.getPresentIngredients().getPresentIngredient().get(i).getIngredientId());
		}
		return listId;
	}
	
	/**
	 * Die Funktion getIndexOfPresentIngredientFromIngredientId gibt den Index eines PresentIngredient zurück. Eingabewert sind der PresentFertiliser und die IngredientId.
	 * 
	 * @param presentFertiliser - Als Objekt vom Typ PresentFertiliser
	 * @param ingredientId - Integer
	 * @return - Zurück gegeben wird die Index des PresentIngredients als vorhanden oder -1 als kein PresentIngredient mit der ID vorhanden ist.
	 * 
	 * Autor: Eddi M.
	 */
	public int getIndexOfPresentIngredientFromIngredientId(PresentFertiliser presentFertiliser, int ingredientId){
		ArrayList<Integer> listId = this.getIdOfPresentIngredientsFromPresentFertiliser(presentFertiliser);
			
		if (listId.contains(ingredientId) == true){
			int index = 0;
			while (ingredientId != presentFertiliser.getPresentIngredients().getPresentIngredient().get(index).getIngredientId()){
				index += 1;
			}
			return index;
		}else {
			return  -1;
		}
		
	}
	
	/**
	 * Ermittelt anhand des Index den RequiredIngredient eines RequiredFertiliser
	 * 
	 * @param requiredFertiliser
	 * @param requiredIngredientIndex - Integer
	 * @return
	 * 
	 * Autor: Eddi M.
	 */
	public RequiredIngredient getRequiredIngredient(RequiredFertiliser requiredFertiliser, int requiredIngredientIndex){
		RequiredIngredient requiredIngredient;
		int fId = requiredFertiliser.getId();
		requiredIngredient = this.modelData.getRequiredFertiliser().get(fId).getRequiredIngredients().getRequiredIngredient().get(requiredIngredientIndex);
		return requiredIngredient;
	}
	
	public void changeRequiredIngredient(int requiredFertiliserId, int ingredientIndex, double percentMin, double percentMax){
		RequiredFertiliser requiredFertiliser = getRequiredFertiliser(requiredFertiliserId);
		changeRequiredIngredient(requiredFertiliser, ingredientIndex, percentMin, percentMax);
	}
	
	public void changeRequiredIngredient(RequiredFertiliser requiredFertiliser, int ingredientIndex, double percentMin, double percentMax){
		RequiredIngredient	requiredIngredient;
		
		requiredIngredient = getRequiredIngredient(requiredFertiliser, ingredientIndex);
		requiredIngredient.setPercentMin(percentMin);
		requiredIngredient.setPercentMax(percentMax);
	}
	
	/**
	 * Gibt eine Liste der RequiredIngredientId eines RequiredFertiliser wieder.
	 * 
	 * @param requiredFertiliser - Object RequiredFertiliser, über welchem interiert wird, um die Ids zu erhalten.
	 * @return - Zurück gegeben wird eine ArrayListe mit allen Ids gespeichert als Integer.
	 */
	public ArrayList<Integer> getIdOfRequiredIngredientsFromRequiredFertiliser(RequiredFertiliser requiredFertiliser){
		ArrayList<Integer> listId = new ArrayList<Integer>();
		
		for (int i=0; i < requiredFertiliser.getRequiredIngredients().getRequiredIngredient().size(); i++){
			listId.add(requiredFertiliser.getRequiredIngredients().getRequiredIngredient().get(i).getIngredientId());
		}
		return listId;
	}
	
	/**
	 * Die Funktion getIndexOfRequiredIngredientFromIngredientId gibt den Index eines RequiredIngredient zurück. Eingabewert sind der RequiredFertiliser und die IngredientId.
	 * 
	 * @param presentFertiliser - Als Objekt vom Typ RequiredFertiliser
	 * @param ingredientId - Integer
	 * @return - Zurück gegeben wird die Index des RequiredIngredients, wenn die Id vorhanden ist oder -1 wenn sie es nicht ist.
	 * 
	 * Autor: Eddi M.
	 */
	public int getIndexOfRequiredIngredientFromIngredientId(RequiredFertiliser requiredFertiliser, int ingredientId){
		ArrayList<Integer> listId = this.getIdOfRequiredIngredientsFromRequiredFertiliser(requiredFertiliser);
			
		if (listId.contains(ingredientId) == true){
			int index = 0;
			while (ingredientId != requiredFertiliser.getRequiredIngredients().getRequiredIngredient().get(index).getIngredientId()){
				index += 1;
			}
			return index;
		}else {
			return  -1;
		}
		
	}
	
	/**
	 * Speichert ein RequiredIngredient unter dem mitgegebendem RequiredFertiliser ab.
	 * 
	 * @param requiredFertiliser
	 * @param requiredIngredient
	 */
	public void addRequiredIngredient(RequiredFertiliser requiredFertiliser, RequiredIngredient requiredIngredient){
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
	}
	
	/**
	 * Erstellt ein RequiredIngredient und gibt es als Rückgabewert zurück.
	 * 
	 * @param id
	 * @param minPercent
	 * @param maxPercent
	 * @return
	 */
	public RequiredIngredient createRequiredIngredient(int id, double minPercent, double maxPercent){
		RequiredIngredient requiredIngredient;
		
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(id);
		requiredIngredient.setPercentMin(minPercent);
		requiredIngredient.setPercentMax(maxPercent);
		requiredIngredient.setActive(true);
		return requiredIngredient;
	}
	
	/**
	 * Erstellt ein PresentFertiliser und speichert alle im zugeordneten PresentIngredients ab.
	 * 
	 * @param id
	 * @param name
	 * @param amount
	 * @param unit
	 * @param presentIngredients
	 */
	public void addPresentFertiliser(int id, String name, double amount, Units unit, ArrayList<PresentIngredient> presentIngredients){
		PresentFertiliser presentFertiliser;
		
		presentFertiliser = factory.createPresentFertiliser();
		presentFertiliser.setId(id);
		presentFertiliser.setName(name);
		presentFertiliser.setAmount(amount);
		presentFertiliser.setUnit(unit);
		presentFertiliser.setPresentIngredients(factory.createPresentIngredients());
		
		Iterator<PresentIngredient> iter = presentIngredients.iterator();
		while(iter.hasNext()){
			addPresentIngredient(presentFertiliser, iter.next());
		}
		
		this.modelData.getPresentFertiliser().add(presentFertiliser);
	}
	
	/**
	 * Erstellt ein RequiredFertiliser und speichert im zugeordnete RequiredIngredients ab.
	 * 
	 * @param id
	 * @param name
	 * @param amount
	 * @param unit
	 * @param requiredIngredients
	 */
	public void addRequiredFertiliser(int id, String name, double amount, Units unit, ArrayList<RequiredIngredient> requiredIngredients){
		RequiredFertiliser requiredFertiliser;
		
		requiredFertiliser = factory.createRequiredFertiliser();
		requiredFertiliser.setId(id);
		requiredFertiliser.setName(name);
		requiredFertiliser.setAmount(amount);
		requiredFertiliser.setUnit(unit);
		requiredFertiliser.setRequiredIngredients(factory.createRequiredIngredients());
		
		Iterator<RequiredIngredient> iter = requiredIngredients.iterator();
		while(iter.hasNext()){
			addRequiredIngredient(requiredFertiliser, iter.next());
		}
		
		this.modelData.getRequiredFertiliser().add(requiredFertiliser);
	}
	
	
	/**
	 * zu Test Zwecken
	 * @param args
	 */
	public static void main(String[] args) {
		Model.setBasisPfad("./testDir/");
		Model.init();
		System.out.println(Model.info());
		
		Model model = new Model("testx");
		model.printDoc();
	}
	
	/**
	 * liefert das Verzeichnis, in der die Modelle liegen
	 * 
	 * 	 * @return
	 */
	public String getDirectory(){
		String out = Model.basisPfad+this.datei;
		return out.substring(0, out.lastIndexOf('/')+1);
	}
	
	/**
	 * liefert Id des Modelles
	 * 
	 * @return
	 */
	public String getId(){
		return this.modelData.getId();
	}
	
	/**
	 * liefert die xmlDaten
	 * 
	 * @return
	 */
	public Fertiliser getModelData(){
		return this.modelData;
	}
	
	/**
	 * gibt doc in this.datei aus
	 * schreibt ins Dateisystem
	 * 
	 * @return			  true wenn alles ok
	 */
	public boolean printDoc(){
		boolean out = false;
		//System.out.println("Schreibe Problem auf: "+Model.basisPfad+this.datei);
		File file = new File(Model.basisPfad+this.datei);
		FileOutputStream f;
		try {
			f 	= 	new FileOutputStream(file);
			out =	this.marshalXml(f , modelData);
			f.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//System.out.println("call printDoc "+out+"   "+file.getAbsolutePath());
		return out;
	}
	
	/**
	 * gibt die aktuelle Zeit als String
	 * 
	 * @return
	 */
	public String getTime(){
		GregorianCalendar cal = new GregorianCalendar();
		DateFormat tag  = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat time = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		return tag.format(cal.getTime())+"  "+time.format(cal.getTime());
	}
	
	private Fertiliser unmarshalXml(InputStream xmlStream){
		Fertiliser model = null;
		try {
		Unmarshaller u = this.jaxbContext.createUnmarshaller();
		Object paraXML = u.unmarshal(xmlStream);
		model = (Fertiliser) paraXML;
		} catch (JAXBException je) { }
		return model;
		}
	
	private boolean marshalXml(OutputStream xmlStream, Fertiliser model){
		boolean out = true;
		Marshaller m;
		try {
		m = this.jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"Schema.xsd");
		m.marshal(model, xmlStream);
		} catch (JAXBException e) {out = false;}
		return out;
		}
}
