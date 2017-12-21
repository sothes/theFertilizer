package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import bean.EDbean;

/**
 * Das Controller Servlet nimmt die Nutzer Eingaben entgegen 
 * und gibt die Eingaben an die Bean zur Bearbeitung der 
 * Sachlogik weiter. Fuer die Ausgabe wird der Request an
 * eine geeignete jsp weitergereicht. 
 *
 */
@WebServlet("/Controller")
 public class Controller extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 static final long serialVersionUID = 42L;
	 static Logger logger = Logger.getLogger(Controller.class);
	 
	public Controller() {
		super();
	}   	
	
	public void init(ServletConfig conf) throws ServletException{
		super.init(conf);
		String path = conf.getServletContext().getRealPath("/");
		PropertyConfigurator.configure(path+"log4j.properties");
		try {
			EDbean.init(path);
		} catch (Exception e) {
			System.out.println("Controller.init  Einlesefehler oder Webservice nicht erreichbar");
		}
	}
	
	/**
	 * ruft doAction auf
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doAction(request, response);
	}  	
	
	/**
	 * ruft doAction auf
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doAction(request, response);
	}   	  	    
	
	/**
	 * nimmt die Eingaben entgegen, reicht sie an die Bean
	 * zur Verarbeitung weiter. Zur Erzeugung einer Ausgabe
	 * werden Request und Response an eine jsp weitergeleitet.
	 * Jeder Request enthaelt einen Parameter action, der den 
	 * Bearbeitungs-Zustand angibt. Die aktuellen Zustaende werden
	 * in einer Session gespeichert.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("text/plain");
		PrintWriter writer = response.getWriter();
		HttpSession session = request.getSession();
		
		//Session besorgen
		EDbean model = null;
		model = (EDbean) session.getAttribute("ed");
		if(model == null){
			model = new EDbean();
			session.setAttribute("ed",model);
		}

		// Bestimmung des aktuellen Nutzers
		String nutzer = null;
		if(EDbean.ProduktionSyst) nutzer = request.getRemoteUser();
		else nutzer = request.getParameter("nutzer");
		
		//Verteiler
		String action = request.getParameter("action");
		String target = null;
		RequestDispatcher dispacher = null;

		if(action == null){
			//Einstiegspunkt
			if(nutzer != null) model.setNutzer(nutzer);
			target = "02_selectModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("00_Abmelden")){

			session.invalidate();
			target = "index.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("02_selectModel")){
			target = "02_selectModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("03_showModel")){
			String modelId = request.getParameter("modelId");
			model.setModel(modelId);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("04_addIngredient")){
			String name = request.getParameter("addIngredientName");
			String price = request.getParameter("addIngredientPrice");
			String unit = request.getParameter("addIngredientUnit");
			model.addIngredient(name, price, unit);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("05_addPresentIngredient")){
			String id = request.getParameter("presetFertiliserId");
			String name = request.getParameter("addPresentIngredientName");
			String percent = request.getParameter("addIngredientPercent");
			model.addOrChangePresentIngredient(id , name, percent);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("06_editIngredient")){
			String ingredientId = request.getParameter("ingredientId");
			System.out.println(ingredientId);
			int id = Integer.parseInt(ingredientId);
			model.setChangeIngredientId(id);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("07_saveEditIngredient")){
			String ingredientId = request.getParameter("ingredientId");
			String price = request.getParameter("addIngredientPrice");
			String unit = request.getParameter("addIngredientUnit");
			System.out.println("Id: " + ingredientId + " Price: "+ price + " unit: "+ unit);
			model.changeIngredient(ingredientId, price, unit);
			model.setChangeIngredientId(-1);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("08_editPresentIngredient")){
			String fertiliserId = request.getParameter("presentFertiliserId");
			String ingredientId = request.getParameter("presentIngredientId");
			System.out.println("FertiliserId: " + fertiliserId + " IngredientId: " + ingredientId);
			model.setChangePresentIngredientId(ingredientId);
			model.setChangePresentFertiliser(fertiliserId);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("09_saveEditPresentIngredient")){
			String fertiliserId = request.getParameter("presentFertiliserId");
			String ingredientId = request.getParameter("presentIngredientId");
			String percent = request.getParameter("changeIngredientPercent");
			System.out.println("FertiliserId: " + fertiliserId + " IngredientId: "+ ingredientId + " Percent: "+ percent);
			model.addOrChangePresentIngredient(fertiliserId, ingredientId, percent);
			model.setChangePresentIngredientId("-1");
			model.setChangePresentFertiliser("-1");
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("10_solveModel")){
			String modelId = request.getParameter("modelId");
			model.setModel(modelId);
			model.resetSolution();
			model.save();
			model.solve();
			model.save();
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("11_saveModel")){
			String modelId = request.getParameter("modelId");
			model.setModel(modelId);
			model.save();
			target = "02_selectModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("14_addModel")){
			String modelId = request.getParameter("modelId");
			String modelName = request.getParameter("modelName");
			model.addModel(modelId, modelName);
			target = "02_selectModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("15_removeModel")){
			String modelId 			= request.getParameter("modelId");
			model.removeModel(modelId);
			target = "02_selectModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("20_showRowAdding")){
			model.setAddIngredient(true);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("21_NoShowRowAdding")){
			model.setAddIngredient(false);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("22_deleteIngredients")){
			String ingredientId = request.getParameter("ingredientId");
			System.out.println("You are going to delete the Ingredients: " + ingredientId);
			model.setIngredientActive(ingredientId, false);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("23_showDeletedIngredients")){
			model.setShowDeletedIngredient(true);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("24_dontshowDeletedIngredients")){
			model.setShowDeletedIngredient(false);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("25_undeleteIngredients")){
			String Str_ingredientId = request.getParameter("ingredientId");
			model.setIngredientActive(Str_ingredientId, true);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("26_showRowPresentIngredientAdding")){
			String fertiliserId = request.getParameter("presentFertiliserId");
			model.setAddPresentIngredients(fertiliserId, true);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("27_NoShowRowPresentIngredientAdding")){
			String fertiliserId = request.getParameter("presentFertiliserId");
			model.setAddPresentIngredients(fertiliserId, false);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else if(action.equals("28_deletePresentIngredient")){
			String fertiliserId = request.getParameter("fertiliserId");
			String ingredientId = request.getParameter("ingredientId");
			//System.out.println("You are going to delete the PresentIngredients: " + ingredientId);
			target = "03_showModel.jsp";
			dispacher = request.getRequestDispatcher(target);
			dispacher.forward(request, response);
		}
		else{
			writer.println("Error: action= "+action);
		}
		writer.println("Hier ist Controller");
		writer.close();
	}
	
	/**
	 * Die Werte der Parameter name_0 ... name_length werden zu einem Array
	 * zusammengefasst.
	 * @param req
	 * @param name
	 * @param length
	 * @return
	 */
	private String[] getArray(HttpServletRequest req, String name, int length){
		String[] out = new String[length];
		for(int i=0; i<length; i++){
			out[i] = req.getParameter(name+"_"+i);
		}
		return out;
	}
	
	/**
	 * Die Werte der Parameter name_0_0 ... name_lengthRow_lengthCol werden zu einer Matrix
	 * zusammengefasst.
	 * @param req
	 * @param name
	 * @param lengthRow
	 * @param lengthCol
	 * @return
	 */
	private String[][] getMatrix(HttpServletRequest req, String name, int lengthRow, int lengthCol){
		String[][] out = new String[lengthRow][lengthCol];
		for(int i=0; i<lengthRow; i++){
			for(int j=0; j<lengthCol; j++){
				out[i][j] = req.getParameter(name+"_"+i+"_"+j);
				//System.out.println("i: "+i+"  j: "+j+" value: "+out[i][j]);
			}
		}
		return out;
	}
	
}
