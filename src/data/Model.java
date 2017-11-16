package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

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
	 * Die relative Wurzel für die Modelldateien
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
	 * fügt neues Modell mit modelId der Hashtable hinzu
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
	 * information über alle erstellten Modelle
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
	 * @param mid				ModellId
	 * @param anzVariables
	 * @param anzConstraints
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
		this.modelData.setAutor("Christian Müller");
		this.modelData.setDescription("Mischdünger");
		this.modelData.setSolved(false);
		this.modelData.setSolverStatus("unsolved");
		this.modelData.setIngredients(factory.createIngredients());
		
		ingredient = factory.createIngredient();
		ingredient.setId(0);
		ingredient.setName("Nitrat");
		ingredient.setUnit(Units.TONNEN);
		ingredient.setPrice(450.0);
		this.modelData.getIngredients().getIngredient().add(ingredient);
		ingredient = factory.createIngredient();
		ingredient.setId(1);
		ingredient.setName("Phosphat");
		ingredient.setUnit(Units.TONNEN);
		ingredient.setPrice(300.0);
		this.modelData.getIngredients().getIngredient().add(ingredient);
		ingredient = factory.createIngredient();
		ingredient.setId(2);
		ingredient.setName("Torf");
		ingredient.setUnit(Units.TONNEN);
		ingredient.setPrice(80.0);
		this.modelData.getIngredients().getIngredient().add(ingredient);
		
		
		presentFertiliser = factory.createPresentFertiliser();
		presentFertiliser.setId(0);
		presentFertiliser.setName("Duenger1");
		presentFertiliser.setAmount(20.0);
		presentFertiliser.setUnit(Units.TONNEN);
		presentFertiliser.setPresentIngredients(factory.createPresentIngredients());
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(0);
		presentIngredient.setPercent(15.0);
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(1);
		presentIngredient.setPercent(30.0);
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(2);
		presentIngredient.setPercent(55.0);
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
		this.modelData.getPresentFertiliser().add(presentFertiliser);
		
		presentFertiliser = factory.createPresentFertiliser();
		presentFertiliser.setId(1);
		presentFertiliser.setName("Duenger2");
		presentFertiliser.setAmount(20.0);
		presentFertiliser.setUnit(Units.TONNEN);
		presentFertiliser.setPresentIngredients(factory.createPresentIngredients());
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(0);
		presentIngredient.setPercent(25.0);
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(1);
		presentIngredient.setPercent(10.0);
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
		presentIngredient = factory.createPresentIngredient();
		presentIngredient.setIngredientId(2);
		presentIngredient.setPercent(65.0);
		presentFertiliser.getPresentIngredients().getPresentIngredient().add(presentIngredient);
		this.modelData.getPresentFertiliser().add(presentFertiliser);

		requiredFertiliser = factory.createRequiredFertiliser();
		requiredFertiliser.setId(0);
		requiredFertiliser.setName("Duenger3");
		requiredFertiliser.setAmount(10.0);
		requiredFertiliser.setUnit(Units.TONNEN);
		requiredFertiliser.setRequiredIngredients(factory.createRequiredIngredients());
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(0);
		requiredIngredient.setPercentMin(20.0);
		requiredIngredient.setPercentMax(25.0);
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(1);
		requiredIngredient.setPercentMin(15.0);
		requiredIngredient.setPercentMax(20.0);
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(2);
		requiredIngredient.setPercentMin(0.0);
		requiredIngredient.setPercentMax(100.0);
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
		this.modelData.getRequiredFertiliser().add(requiredFertiliser);

		requiredFertiliser = factory.createRequiredFertiliser();
		requiredFertiliser.setId(1);
		requiredFertiliser.setName("Duenger4");
		requiredFertiliser.setAmount(25.0);
		requiredFertiliser.setUnit(Units.TONNEN);
		requiredFertiliser.setRequiredIngredients(factory.createRequiredIngredients());
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(0);
		requiredIngredient.setPercentMin(25.0);
		requiredIngredient.setPercentMax(30.0);
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(1);
		requiredIngredient.setPercentMin(10.0);
		requiredIngredient.setPercentMax(15.0);
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
		requiredIngredient = factory.createRequiredIngredient();
		requiredIngredient.setIngredientId(2);
		requiredIngredient.setPercentMin(0.0);
		requiredIngredient.setPercentMax(100.0);
		requiredFertiliser.getRequiredIngredients().getRequiredIngredient().add(requiredIngredient);
		this.modelData.getRequiredFertiliser().add(requiredFertiliser);
	}
	
	public void addIngredient(int id, String name, double price, Units unit){
		Ingredient 	ingredient;
		ingredient = factory.createIngredient();
		ingredient.setId(id);
		ingredient.setName(name);
		ingredient.setUnit(unit);
		ingredient.setPrice(price);
		this.modelData.getIngredients().getIngredient().add(ingredient);
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
	 * 	 * @return
	 */
	public String getDirectory(){
		String out = Model.basisPfad+this.datei;
		return out.substring(0, out.lastIndexOf('/')+1);
	}
	
	/**
	 * liefert Id des Modelles
	 * @return
	 */
	public String getId(){
		return this.modelData.getId();
	}
	
	/**
	 * liefert die xmlDaten
	 * @return
	 */
	public Fertiliser getModelData(){
		return this.modelData;
	}
	
	/**
	 * gibt doc in this.datei aus
	 * schreibt ins Dateisystem
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
