//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.01.04 at 12:57:10 AM CET 
//


package xmlData;

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
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="unit" type="{http://www.example.org/Fertiliser}units"/>
 *         &lt;element ref="{http://www.example.org/Fertiliser}presentIngredients"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="active" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="hasPresentIngredients" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
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
    "amount",
    "unit",
    "presentIngredients"
})
@XmlRootElement(name = "presentFertiliser")
public class PresentFertiliser {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String name;
    protected double amount;
    @XmlElement(required = true)
    @XmlSchemaType(name = "token")
    protected Units unit;
    @XmlElement(required = true)
    protected PresentIngredients presentIngredients;
    @XmlAttribute(name = "id", required = true)
    protected int id;
    @XmlAttribute(name = "active")
    protected Boolean active;
    @XmlAttribute(name = "hasPresentIngredients")
    protected Boolean hasPresentIngredients;

    /**
     * Gets the value of the name property.
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
     * Sets the value of the name property.
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
     * Gets the value of the amount property.
     * 
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(double value) {
        this.amount = value;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link Units }
     *     
     */
    public Units getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Units }
     *     
     */
    public void setUnit(Units value) {
        this.unit = value;
    }

    /**
     * Gets the value of the presentIngredients property.
     * 
     * @return
     *     possible object is
     *     {@link PresentIngredients }
     *     
     */
    public PresentIngredients getPresentIngredients() {
        return presentIngredients;
    }

    /**
     * Sets the value of the presentIngredients property.
     * 
     * @param value
     *     allowed object is
     *     {@link PresentIngredients }
     *     
     */
    public void setPresentIngredients(PresentIngredients value) {
        this.presentIngredients = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the active property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isActive() {
        if (active == null) {
            return true;
        } else {
            return active;
        }
    }

    /**
     * Sets the value of the active property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setActive(Boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the hasPresentIngredients property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isHasPresentIngredients() {
        if (hasPresentIngredients == null) {
            return false;
        } else {
            return hasPresentIngredients;
        }
    }

    /**
     * Sets the value of the hasPresentIngredients property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasPresentIngredients(Boolean value) {
        this.hasPresentIngredients = value;
    }

}
