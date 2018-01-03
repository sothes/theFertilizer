package bean;

import java.io.File;

/**
 * Diese Klasse ermoeglicht den Programmierer schnell und nach belieben die Icons/Bilder auszutauschen.
 * Die icons werden von der Klasse EDBean in die Html Tags implementiert.
 * 
 * @author Eddi M.
 *
 */
public enum Images {
	Edit("Icons" + File.separator + "ic_edit_black_36dp" + File.separator + "web" + File.separator + "ic_edit_black_36dp_1x.png", "bearbeiten", "Bearbeiten"),
	Done("Icons" + File.separator + "ic_done_black_36dp" + File.separator + "web" + File.separator + "ic_done_black_36dp_1x.png", "speichern", "Speichern"),
	Add("Icons" + File.separator + "ic_add_black_36dp" + File.separator + "web" + File.separator + "ic_add_black_36dp_1x.png", "hinzufuegen", "Hinzufuegen"),
	Delete("Icons" + File.separator + "ic_delete_black_36dp" + File.separator + "web" + File.separator + "ic_delete_black_36dp_1x.png", "loeschen", "Loeschen"),
	Visible("Icons" + File.separator + "ic_visibility_black_36dp" + File.separator + "web" + File.separator + "ic_visibility_black_36dp_1x.png", "sichtbar", "Die geloeschten Objekte anschauen"),
	Invisible("Icons" + File.separator + "ic_visibility_off_black_36dp" + File.separator + "web" + File.separator + "ic_visibility_off_black_36dp_1x.png", "unsichtbar", "Nur die ungeloeschten Objekte sehen"),
	Back("Icons" + File.separator + "ic_keyboard_backspace_black_36dp" + File.separator + "web" + File.separator + "ic_keyboard_backspace_black_36dp_1x.png", "einklappen", "einklappen"),
	FillUp("Icons" + File.separator + "ic_timelapse_black_36dp" + File.separator + "web" + File.separator + "ic_timelapse_black_36dp_1x.png", "auf 100 % rechnen", "auf 100 % rechnen");
	
	/**
	 * Das Attribut ImagePfad speichert den Dateipfad des für die Aktion gespeicherten Image.
	 * 
	 * Autor: Eddi M.
	 */
	private String ImagePfad;
	
	/**
	 * Das Attribut AltTag speichert den AltTag des Bildes ab.
	 * 
	 * Autor: Eddi M.
	 */
	private String AltTag;
	
	private String ToolTip;
	
	private Images(String imagePfad, String altTag, String toolTip){
		this.ImagePfad = imagePfad;
		this.AltTag = altTag;
		this.ToolTip = toolTip;
	}
	
	/**
	 * Mit dieser Funktion kann der Dateipfad des Icons/Images geladen werden.
	 * 
	 * @return Es wird ein String zurück gegeben.
	 * 
	 * Autor: Eddi M.
	 */
	public String getImagePfad(){
		return this.ImagePfad;
	}
	
	/**
	 * Mit dieser Funktion kann der AltTag des Icons/Images geladen werden.
	 * 
	 * @return Es wird ein String zurück gegeben.
	 * 
	 * @Autor: Eddi M.
	 */
	public String getAltTag(){
		return this.AltTag;
	}
	
	/**
	 * Mit dieser Funktion kann der ToolTip des Icon/Images geladen werden.
	 * 
	 * @return Es wird ein String zurück gegeben.
	 * 
	 * @Autor: Eddi M.
	 */
	public String getToolTip(){
		return this.ToolTip;
	}
	
}
