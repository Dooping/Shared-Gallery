
package sd.tp1.clt.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the sd.tp1.clt.ws package. 
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

    private final static QName _PrintCenas_QNAME = new QName("http://srv.tp1.sd/", "printCenas");
    private final static QName _PrintCenasResponse_QNAME = new QName("http://srv.tp1.sd/", "printCenasResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: sd.tp1.clt.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PrintCenas }
     * 
     */
    public PrintCenas createPrintCenas() {
        return new PrintCenas();
    }

    /**
     * Create an instance of {@link PrintCenasResponse }
     * 
     */
    public PrintCenasResponse createPrintCenasResponse() {
        return new PrintCenasResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrintCenas }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "printCenas")
    public JAXBElement<PrintCenas> createPrintCenas(PrintCenas value) {
        return new JAXBElement<PrintCenas>(_PrintCenas_QNAME, PrintCenas.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrintCenasResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "printCenasResponse")
    public JAXBElement<PrintCenasResponse> createPrintCenasResponse(PrintCenasResponse value) {
        return new JAXBElement<PrintCenasResponse>(_PrintCenasResponse_QNAME, PrintCenasResponse.class, null, value);
    }

}
