
package sd.tp1.clt.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pictureClass complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pictureClass">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="erased" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="lamportClock" type="{http://srv.tp1.sd/}lamportClock" minOccurs="0"/>
 *         &lt;element name="server" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pictureClass", propOrder = {
    "name",
    "erased",
    "lamportClock",
    "server"
})
public class PictureClass {

    protected String name;
    protected boolean erased;
    protected LamportClock lamportClock;
    protected String server;

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
     * Gets the value of the erased property.
     * 
     */
    public boolean isErased() {
        return erased;
    }

    /**
     * Sets the value of the erased property.
     * 
     */
    public void setErased(boolean value) {
        this.erased = value;
    }

    /**
     * Gets the value of the lamportClock property.
     * 
     * @return
     *     possible object is
     *     {@link LamportClock }
     *     
     */
    public LamportClock getLamportClock() {
        return lamportClock;
    }

    /**
     * Sets the value of the lamportClock property.
     * 
     * @param value
     *     allowed object is
     *     {@link LamportClock }
     *     
     */
    public void setLamportClock(LamportClock value) {
        this.lamportClock = value;
    }

    /**
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServer(String value) {
        this.server = value;
    }

}
