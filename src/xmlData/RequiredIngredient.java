//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.12.01 um 12:08:33 PM CET 
//


package xmlData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ingredientId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="percentMin" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="percentMax" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ingredientId",
    "percentMin",
    "percentMax"
})
@XmlRootElement(name = "requiredIngredient")
public class RequiredIngredient {

    protected int ingredientId;
    protected double percentMin;
    protected double percentMax;

    /**
     * Ruft den Wert der ingredientId-Eigenschaft ab.
     * 
     */
    public int getIngredientId() {
        return ingredientId;
    }

    /**
     * Legt den Wert der ingredientId-Eigenschaft fest.
     * 
     */
    public void setIngredientId(int value) {
        this.ingredientId = value;
    }

    /**
     * Ruft den Wert der percentMin-Eigenschaft ab.
     * 
     */
    public double getPercentMin() {
        return percentMin;
    }

    /**
     * Legt den Wert der percentMin-Eigenschaft fest.
     * 
     */
    public void setPercentMin(double value) {
        this.percentMin = value;
    }

    /**
     * Ruft den Wert der percentMax-Eigenschaft ab.
     * 
     */
    public double getPercentMax() {
        return percentMax;
    }

    /**
     * Legt den Wert der percentMax-Eigenschaft fest.
     * 
     */
    public void setPercentMax(double value) {
        this.percentMax = value;
    }

}
