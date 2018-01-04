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
import xmlData.User;

/**
 * Diese Klasse speichert und liest alle Modelldaten ein/aus 
 * einer xml Datei Detailinformationen sind der Methode getModelData zu entnehmen.
 * 
 * @author Eddi M.
 *
 */
public class Model {

	static Logger logger = Logger.getLogger(Model.class);

	/**
	 * Die relative Wurzel fuer die Modelldateien
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
		User user = this.factory.createUser();
			user.setName("Eddi M.");
			user.setUserId(1);
		
		this.modelData 	= factory.createFertiliser();
		this.modelData.setId(mid);
		this.modelData.setName(mid);
		this.modelData.setCreatedAt(this.getTime());
		this.modelData.setUser(user);
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
	 * Ermittelt den Ingredient anhand der Id und gibt ihn zurueck.
	 * 
	 * @param id - Integer
	 * @return - Ingredient
	 * @autor: Eddi M.
	 */
	public Ingredient getIngredient(int id){
		Ingredient ingredient;
		
		ingredient = this.modelData.getIngredients().getIngredient().get(id);
		
		return ingredient;
	}

	/**
	 * Ermoeglicht das Anlegen von neuen Ingredients
	 * 
	 * @param id - Integer
	 * @param name  - String
	 * @param price - double
	 * @param unit - Units
	 * @autor: Eddi M.
	 */
	public void addIngredient(int id, String name, double price, Units unit){
		Ingredient 	ingredient = this.createIngredient(id, name, price, unit);
		this.modelData.getIngredients().getIngredient().add(ingredient);
	}
	
	/**
	 * Speichert die Aenderung von Ingredients ab.
	 * 
	 * @param ingredient - Ingredient
	 * @param price - double
	 * @param unit - Unit
	 * @autor: Eddi M.
	 */
	public void changeIngredient(Ingredient ingredient, double price, Units unit){
		ingredient.setPrice(price);
		ingredient.setUnit(unit);
	}
	
	/**
	 * Erstellt ein Element vom Typ Ingredient, welches zurueck gegeben wird.
	 * 
	 * @param id - Integer
	 * @param name - String
	 * @param price - double
	 * @param unit - Units
	 * @return - Ingredient
	 * @autor: Eddi M.
	 */
	private Ingredient createIngredient(int id, String name, double price, Units unit){
		Ingredient ingredient;
		
		ingredient = factory.createIngredient();
		ingredient.setId(id);
		ingredient.setName(name);
		ingredient.setUnit(unit);
		ingredient.setPrice(price);
		ingredient.setActive(true);
		return ingredient;
	}
	
	/**
	 * Gibt eine Liste der IngredientIds wieder.
	 * 
	 * @return - Zurueck gegeben wird eine ArrayListe mit allen Ids gespeichert als Integer.
	 */
	public ArrayList<Integer> getIdsOfIngredients(){
		ArrayList<Integer> listId = new ArrayList<Integer>();
		
		for (int i=0; i < modelData.getIngredients().getIngredient().size(); i++){
			listId.add(modelData.getIngredients().getIngredient().get(i).getId());
		}
		return listId;
	}
	
	/**
	 * Diese Funktion gibt die IngredientId als Integer zurueck. Eingabewert ist der Name des Ingredient als String.
	 * Wurde keine IngredientId fuer den Namen gefunden wird -1 zurueck gegeben.
	 * 
	 * @param name - String
	 * @return - Integer
	 * @autor: Eddi M.
	 */
	public int getIngredientIdFromIngredientName(String name){
		int result = -1;
		for (int i=0; i<this.modelData.getIngredients().getIngredient().size(); i++){
			
			if (this.modelData.getIngredients().getIngredient().get(i).getName().equals(name)){
				result = this.modelData.getIngredients().getIngredient().get(i).getId();
			}
		}
		
		return result;
	}

	/**
	 * Setzt den Status von Ingredient auf den boolischen Wert b.
	 * 
	 * @param ingredient - Ingredient
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setIngredientActive(Ingredient ingredient, boolean b){
		ingredient.setActive(b);
	}
	
	/**
	 * Setzt den Status von PresentFertiliser auf den boolischen Wert b.
	 * 
	 * @param fertiliser - PresentFertiliser
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setPresentFertiliserActive(PresentFertiliser fertiliser, boolean b){
		fertiliser.setActive(b);
	}
	
	/**
	 * Setzt den Status von RequiredFertiliser auf den boolischen Wert b.
	 * 
	 * @param fertiliser - RequiredFertiliser
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setRequiredFertiliserActive(RequiredFertiliser fertiliser, boolean b){
		fertiliser.setActive(b);
	}
	
	/**
	 * Diese Funktion gibt den entsprechenden PresentFertiliser anhand seiner Id zurueck.
	 * 
	 * @param id - Id des PresentFertilisers
	 * @return PresentFertiliser
	 * 
	 * @autor: Eddi M.
	 */
	public PresentFertiliser getPresentFertiliser(int id){
		PresentFertiliser presentFertiliser;
		presentFertiliser = this.modelData.getPresentFertiliser().get(id);
		return presentFertiliser;
	}

	/**
	 * Diese Funktion gibt die PresentFertiliserId als Integer zurueck. Eingabewert ist der Name des PresentFertiliser als String.
	 * Wurde keine PresentFertiliserId fuer den Namen gefunden wird -1 zurueck gegeben.
	 * 
	 * @param name - String
	 * @return - Integer
	 * @autor: Eddi M.
	 */
	public int getIdOfPresentFertiliserFromPresentFertiliserName(String name){
		int result = -1;
		for (int i=0; i<this.modelData.getPresentFertiliser().size(); i++){
			
			if (this.modelData.getPresentFertiliser().get(i).getName().equals(name)){
				result = this.modelData.getPresentFertiliser().get(i).getId();
			}
		}
		
		return result;
	}
	
	/**
	 * Gibt eine Liste der PresentFertiliserIds wieder.
	 * 
	 * @return - Zurueck gegeben wird eine ArrayListe mit allen Ids gespeichert als Integer.
	 */
	public ArrayList<Integer> getIdsOfPresentFertilisers(){
		ArrayList<Integer> listId = new ArrayList<Integer>();
		
		for (int i=0; i < modelData.getPresentFertiliser().size(); i++){
			listId.add(modelData.getPresentFertiliser().get(i).getId());
		}
		return listId;
	}
	
	/**
	 * Diese Funktion gibt die RequiredFertiliserId als Integer zurueck. Eingabewert ist der Name des RequiredFertiliser als String.
	 * Wurde keine RequiredFertiliserId fuer den Namen gefunden wird -1 zurueck gegeben.
	 * 
	 * @param name - String
	 * @return - Integer
	 * @autor: Eddi M.
	 */
	public int getIdOfRequiredFertiliserFromRequiredFertiliserName(String name){
		int result = -1;
		for (int i=0; i<this.modelData.getRequiredFertiliser().size(); i++){
			
			if (this.modelData.getRequiredFertiliser().get(i).getName().equals(name)){
				result = this.modelData.getRequiredFertiliser().get(i).getId();
			}
		}
		
		return result;
	}
	
	/**
	 * Gibt eine Liste der RequiredFertiliserIds wieder.
	 * 
	 * @return - Zurueck gegeben wird eine ArrayListe mit allen Ids gespeichert als Integer.
	 */
	public ArrayList<Integer> getIdsOfRequiredFertilisers(){
		ArrayList<Integer> listId = new ArrayList<Integer>();
		
		for (int i=0; i < modelData.getRequiredFertiliser().size(); i++){
			listId.add(modelData.getRequiredFertiliser().get(i).getId());
		}
		return listId;
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
		PresentFertiliser presentFertiliser = this.createPresentFertiliser(id, name, amount, unit);
		
		presentFertiliser.setPresentIngredients(factory.createPresentIngredients());
		
		Iterator<PresentIngredient> iter = presentIngredients.iterator();
		while(iter.hasNext()){
			addPresentIngredient(presentFertiliser, iter.next());
		}
		
		this.modelData.getPresentFertiliser().add(presentFertiliser);
	}

	/**
	 * Erstellt ein PresentFertiliser und speichert es in der xml-Datei ab.
	 * 
	 * @param id - Integer
	 * @param name - String
	 * @param amount - double
	 * @param unit - Units
	 * @autor: Eddi M.
	 */
	public void addPresentFertiliser(int id, String name, double amount, Units unit){
		System.out.println("addPresentFertiliserModel");
		System.out.println("id: " + id + " name: " + name + " amount: " + amount);
		PresentFertiliser presentFertiliser = this.createPresentFertiliser(id, name, amount, unit);
		
		this.modelData.getPresentFertiliser().add(presentFertiliser);
		if (this.printDoc() == true){
			System.out.println("Der Fertiliser wurde angelegt.");
		}else {
			System.out.println("Du bist am Arsch");
		}
	}

	/**
	 * Speichert die Aenderung von PresentFertiliser ab.
	 * 
	 * @param presentFertiliser - PresentFertiliser
	 * @param amount - double
	 * @param unit - Unit
	 * @autor: Eddi M.
	 */
	public void changePresentFertiliser(PresentFertiliser presentFertiliser, double amount, Units unit){
		presentFertiliser.setAmount(amount);
		presentFertiliser.setUnit(unit);
	}
	
	/**
	 * Erstellt ein Element vom Typ Ingredient, welches zurueck gegeben wird.
	 * 
	 * @param id - Integer
	 * @param name - String
	 * @param price - double
	 * @param unit - Units
	 * @return - Ingredient
	 * @autor: Eddi M.
	 */
	private PresentFertiliser createPresentFertiliser(int id, String name, double amount, Units unit){
		PresentFertiliser presentFertiliser;
		
		presentFertiliser = factory.createPresentFertiliser();
		presentFertiliser.setId(id);
		presentFertiliser.setName(name);
		presentFertiliser.setUnit(unit);
		presentFertiliser.setAmount(amount);
		presentFertiliser.setActive(true);
		presentFertiliser.setPresentIngredients(factory.createPresentIngredients());
		
		return presentFertiliser;
	}
	
	/**
	 * Ermittelt anhand des Index den PresentIngredient eines PresentFertiliser
	 * 
	 * @param presentFertiliser
	 * @param presentIngredientIndex - Integer
	 * @return
	 * @autor: Eddi M.
	 */
	public PresentIngredient getPresentIngredient(PresentFertiliser presentFertiliser, int presentIngredientIndex){
		PresentIngredient presentIngredient;
		int fId = presentFertiliser.getId();
		presentIngredient = this.modelData.getPresentFertiliser().get(fId).getPresentIngredients().getPresentIngredient().get(presentIngredientIndex);
		return presentIngredient;
	}
	
	/**
	 * Erstellt ein RequiredFertiliser und speichert alle im zugeordneten RequiredIngredients ab.
	 * 
	 * @param id
	 * @param name
	 * @param amount
	 * @param unit
	 * @param requiredIngredients
	 */
	public void addRequiredFertiliser(int id, String name, double amount, Units unit, ArrayList<RequiredIngredient> requiredIngredients){
		RequiredFertiliser requiredFertiliser = this.createRequiredFertiliser(id, name, amount, unit);
		
		requiredFertiliser.setRequiredIngredients(factory.createRequiredIngredients());
		
		Iterator<RequiredIngredient> iter = requiredIngredients.iterator();
		while(iter.hasNext()){
			addRequiredIngredient(requiredFertiliser, iter.next());
		}
		
		this.modelData.getRequiredFertiliser().add(requiredFertiliser);
	}

	/**
	 * Erstellt ein RequiredFertiliser und speichert es in der xml-Datei ab.
	 * 
	 * @param id - Integer
	 * @param name - String
	 * @param amount - double
	 * @param unit - Units
	 * @autor: Eddi M.
	 */
	public void addRequiredFertiliser(int id, String name, double amount, Units unit){
		System.out.println("addRequiredFertiliserModel");
		System.out.println("id: " + id + " name: " + name + " amount: " + amount);
		RequiredFertiliser requiredFertiliser = this.createRequiredFertiliser(id, name, amount, unit);
		
		this.modelData.getRequiredFertiliser().add(requiredFertiliser);
		if (this.printDoc() == true){
			System.out.println("Der Fertiliser wurde angelegt.");
		}else {
			System.out.println("Du bist am Arsch");
		}
	}

	/**
	 * Speichert die Aenderung von RequiredFertiliser ab.
	 * 
	 * @param requiredFertiliser - RequiredFertiliser
	 * @param amount - double
	 * @param unit - Unit
	 * @autor: Eddi M.
	 */
	public void changeRequiredFertiliser(RequiredFertiliser requiredFertiliser, double amount, Units unit){
		requiredFertiliser.setAmount(amount);
		requiredFertiliser.setUnit(unit);
	}
	
	/**
	 * Erstellt ein Element vom Typ Ingredient, welches zurueck gegeben wird.
	 * 
	 * @param id - Integer
	 * @param name - String
	 * @param price - double
	 * @param unit - Units
	 * @return - Ingredient
	 * @autor: Eddi M.
	 */
	private RequiredFertiliser createRequiredFertiliser(int id, String name, double amount, Units unit){
		RequiredFertiliser requiredFertiliser;
		
		requiredFertiliser = factory.createRequiredFertiliser();
		requiredFertiliser.setId(id);
		requiredFertiliser.setName(name);
		requiredFertiliser.setUnit(unit);
		requiredFertiliser.setAmount(amount);
		requiredFertiliser.setActive(true);
		requiredFertiliser.setRequiredIngredients(factory.createRequiredIngredients());
		
		return requiredFertiliser;
	}

	/**
	 * Diese Funktion speichert die Daten unter einem neuen PresentIngredient ab.
	 * 
	 * @param pFertiliserId - Integer
	 * @param id - Integer
	 * @param percent - double
	 * @autor: Eddi M.
	 */
	public void addPresentIngredient(int pFertiliserId, int id, double percent){
		PresentIngredient pi = createPresentIngredient(id, percent);
		PresentFertiliser pf = getPresentFertiliser(pFertiliserId);
		addPresentIngredient(pf, pi);
	}

	/**
	 * Speichert ein PresentIngredient unter dem mitgegebendem PresentFertiliser
	 * 
	 * @param presentFertiliser
	 * @param presentIngredient
	 * @autor: Eddi M.
	 */
	private void addPresentIngredient(PresentFertiliser presentFertiliser, PresentIngredient presentIngredient){
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
		presentFertiliser.setHasPresentIngredients(true);
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
	 * Die Funktion erstellt PresentIngredients, um die im Anschluss in einem Array zu speichern
	 * und einem PresentFertiliser zuordnen kann.
	 * 
	 * @param id
	 * @param percent
	 * @return
	 */
	private PresentIngredient createPresentIngredient(int id, double percent){
		PresentIngredient	presentIngredient;
		
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(id);
		presentIngredient.setPercent(percent);
		presentIngredient.setActive(true);
		return presentIngredient;
	}

	/**
	 * Gibt eine Liste der PresentIngredientId eines PresentFertiliser wieder.
	 * 
	 * @param presentFertiliser - Object PresentFertiliser über welchem interiert wird, um die Ids zu erhalten.
	 * @return - Zurück gegeben wird eine ArrayListe mit allen Ids gespeichert als Integer.
	 */
	public ArrayList<Integer> getIdsOfPresentIngredientsFromPresentFertiliser(PresentFertiliser presentFertiliser){
		ArrayList<Integer> listId = new ArrayList<Integer>();
		
		if (presentFertiliser.isHasPresentIngredients() == true){
			for (int i=0; i < presentFertiliser.getPresentIngredients().getPresentIngredient().size(); i++){
				int ingredientId = presentFertiliser.getPresentIngredients().getPresentIngredient().get(i).getIngredientId();
				listId.add(ingredientId);
			}
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
		ArrayList<Integer> listId = this.getIdsOfPresentIngredientsFromPresentFertiliser(presentFertiliser);
			
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
	 * Diese Funktion setzt den Status des uebergebenden PresentIngredient auf den boolischen Wert b.
	 * 
	 * @param presentIngredient - PresentIngredient
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setPresentIngredientActive(PresentIngredient presentIngredient, boolean b){
		presentIngredient.setActive(b);
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

	/**
	 * Erstellt ein RequiredIngredient und Speichert es unter dem mitgegebenden RequiredFertiliser
	 * 
	 * @param requiredFertiliser - RequiredFertiliser
	 * @param id - Integer
	 * @param percentMin - double
	 * @param percentMax - double
	 * @autor: Eddi M.
	 */
	public void addRequiredIngredient(RequiredFertiliser requiredFertiliser, int id, double percentMin, double percentMax){
		RequiredIngredient	requiredIngredient;
		
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(id);
		requiredIngredient.setPercentMin(percentMin);
		requiredIngredient.setPercentMax(percentMax);
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
	}

	public void addRequiredIngredient(int FertiliserId, int id, double percentMin, double percentMax){
		RequiredIngredient requiredIngredient = createRequiredIngredient(id, percentMin, percentMax);
		RequiredFertiliser requiredFertiliser = getRequiredFertiliser(FertiliserId);
		addRequiredIngredient(requiredFertiliser, requiredIngredient);
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
	 * Erstellt ein RequiredIngredient und gibt es als Rückgabewert zurück.
	 * 
	 * @param id
	 * @param minPercent
	 * @param maxPercent
	 * @return
	 */
	private RequiredIngredient createRequiredIngredient(int id, double minPercent, double maxPercent){
		RequiredIngredient requiredIngredient;
		
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(id);
		requiredIngredient.setPercentMin(minPercent);
		requiredIngredient.setPercentMax(maxPercent);
		requiredIngredient.setActive(true);
		return requiredIngredient;
	}

	/**
	 * Gibt eine Liste der RequiredIngredientId eines RequiredFertiliser wieder.
	 * 
	 * @param requiredFertiliser - Object RequiredFertiliser, über welchem interiert wird, um die Ids zu erhalten.
	 * @return - Zurück gegeben wird eine ArrayListe mit allen Ids gespeichert als Integer.
	 */
	public ArrayList<Integer> getIdsOfRequiredIngredientsFromRequiredFertiliser(RequiredFertiliser requiredFertiliser){
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
		ArrayList<Integer> listId = this.getIdsOfRequiredIngredientsFromRequiredFertiliser(requiredFertiliser);
			
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
	 * Diese Funktion setzt den Status des uebergebenden RequiredIngredient auf den boolischen Wert b.
	 * 
	 * @param requiredIngredient - RequiredIngredient
	 * @param b - Boolean
	 * @autor: Eddi M.
	 */
	public void setRequiredIngredientActive(RequiredIngredient requiredIngredient, boolean b){
		requiredIngredient.setActive(b);
	}

	/**
	 * Diese Funktion gibt den entsprechenden Required Fertiliser anhand seiner Id zurueck.
	 * 
	 * @param id - Id des RequiredFertilisers
	 * @return RequiredFertiliser
	 * 
	 * @autor: Eddi M.
	 */
	public RequiredFertiliser getRequiredFertiliser(int id){
		RequiredFertiliser requiredFertiliser;
		requiredFertiliser = this.modelData.getRequiredFertiliser().get(id);
		return requiredFertiliser;
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
		
		File file = new File(Model.basisPfad+this.datei);
		FileOutputStream f;
		try {
			f 	= 	new FileOutputStream(file);
			out =	this.marshalXml(f , modelData);
			f.close();
		} catch (IOException e1) {
		
			e1.printStackTrace();
		}
		
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
