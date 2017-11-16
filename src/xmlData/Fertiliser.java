//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.12.01 um 12:08:33 PM CET 
//


package xmlData;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="autor" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element ref="{http://www.example.org/Fertiliser}ingredients"/>
 *         &lt;element ref="{http://www.example.org/Fertiliser}presentFertiliser" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.example.org/Fertiliser}requiredFertiliser" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.example.org/Fertiliser}solution" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="createdAt" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="solved" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="solverStatus" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "autor",
    "description",
    "ingredients",
    "presentFertiliser",
    "requiredFertiliser",
    "solution"
})
@XmlRootElement(name = "Fertiliser")
public class Fertiliser {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String name;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String autor;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String description;
    @XmlElement(required = true)
    protected Ingredients ingredients;
    @XmlElement(required = true)
    protected List<PresentFertiliser> presentFertiliser;
    @XmlElement(required = true)
    protected List<RequiredFertiliser> requiredFertiliser;
    protected List<Solution> solution;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String id;
    @XmlAttribute(name = "createdAt")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String createdAt;
    @XmlAttribute(name = "solved", required = true)
    protected boolean solved;
    @XmlAttribute(name = "solverStatus")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String solverStatus;

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der autor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Legt den Wert der autor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutor(String value) {
        this.autor = value;
    }

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Ruft den Wert der ingredients-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Ingredients }
     *     
     */
    public Ingredients getIngredients() {
        return ingredients;
    }

    /**
     * Legt den Wert der ingredients-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Ingredients }
     *     
     */
    public void setIngredients(Ingredients value) {
        this.ingredients = value;
    }

    /**
     * Gets the value of the presentFertiliser property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the presentFertiliser property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPresentFertiliser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PresentFertiliser }
     * 
     * 
     */
    public List<PresentFertiliser> getPresentFertiliser() {
        if (presentFertiliser == null) {
            presentFertiliser = new ArrayList<PresentFertiliser>();
        }
        return this.presentFertiliser;
    }

    /**
     * Gets the value of the requiredFertiliser property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredFertiliser property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredFertiliser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequiredFertiliser }
     * 
     * 
     */
    public List<RequiredFertiliser> getRequiredFertiliser() {
        if (requiredFertiliser == null) {
            requiredFertiliser = new ArrayList<RequiredFertiliser>();
        }
        return this.requiredFertiliser;
    }

    /**
     * Gets the value of the solution property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the solution property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSolution().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Solution }
     * 
     * 
     */
    public List<Solution> getSolution() {
        if (solution == null) {
            solution = new ArrayList<Solution>();
        }
        return this.solution;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der createdAt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Legt den Wert der createdAt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedAt(String value) {
        this.createdAt = value;
    }

    /**
     * Ruft den Wert der solved-Eigenschaft ab.
     * 
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * Legt den Wert der solved-Eigenschaft fest.
     * 
     */
    public void setSolved(boolean value) {
        this.solved = value;
    }

    /**
     * Ruft den Wert der solverStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolverStatus() {
        return solverStatus;
    }

    /**
     * Legt den Wert der solverStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolverStatus(String value) {
        this.solverStatus = value;
    }

}
