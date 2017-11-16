//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.12.01 um 12:08:33 PM CET 
//


package xmlData;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the xmlData package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: xmlData
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PresentIngredients }
     * 
     */
    public PresentIngredients createPresentIngredients() {
        return new PresentIngredients();
    }

    /**
     * Create an instance of {@link PresentIngredient }
     * 
     */
    public PresentIngredient createPresentIngredient() {
        return new PresentIngredient();
    }

    /**
     * Create an instance of {@link Ingredient }
     * 
     */
    public Ingredient createIngredient() {
        return new Ingredient();
    }

    /**
     * Create an instance of {@link Production }
     * 
     */
    public Production createProduction() {
        return new Production();
    }

    /**
     * Create an instance of {@link UsedFertiliser }
     * 
     */
    public UsedFertiliser createUsedFertiliser() {
        return new UsedFertiliser();
    }

    /**
     * Create an instance of {@link UsedIngredient }
     * 
     */
    public UsedIngredient createUsedIngredient() {
        return new UsedIngredient();
    }

    /**
     * Create an instance of {@link Fertiliser }
     * 
     */
    public Fertiliser createFertiliser() {
        return new Fertiliser();
    }

    /**
     * Create an instance of {@link Ingredients }
     * 
     */
    public Ingredients createIngredients() {
        return new Ingredients();
    }

    /**
     * Create an instance of {@link PresentFertiliser }
     * 
     */
    public PresentFertiliser createPresentFertiliser() {
        return new PresentFertiliser();
    }

    /**
     * Create an instance of {@link RequiredFertiliser }
     * 
     */
    public RequiredFertiliser createRequiredFertiliser() {
        return new RequiredFertiliser();
    }

    /**
     * Create an instance of {@link RequiredIngredients }
     * 
     */
    public RequiredIngredients createRequiredIngredients() {
        return new RequiredIngredients();
    }

    /**
     * Create an instance of {@link RequiredIngredient }
     * 
     */
    public RequiredIngredient createRequiredIngredient() {
        return new RequiredIngredient();
    }

    /**
     * Create an instance of {@link Solution }
     * 
     */
    public Solution createSolution() {
        return new Solution();
    }

}
