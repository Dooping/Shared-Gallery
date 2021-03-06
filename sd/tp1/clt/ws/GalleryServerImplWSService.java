
package sd.tp1.clt.ws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "GalleryServerImplWSService", targetNamespace = "http://srv.tp1.sd/", wsdlLocation = "http://localhost:9090/GalleryServerSOAP?wsdl")
public class GalleryServerImplWSService
    extends Service
{

    private final static URL GALLERYSERVERIMPLWSSERVICE_WSDL_LOCATION;
    private final static WebServiceException GALLERYSERVERIMPLWSSERVICE_EXCEPTION;
    private final static QName GALLERYSERVERIMPLWSSERVICE_QNAME = new QName("http://srv.tp1.sd/", "GalleryServerImplWSService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:9090/GalleryServerSOAP?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        GALLERYSERVERIMPLWSSERVICE_WSDL_LOCATION = url;
        GALLERYSERVERIMPLWSSERVICE_EXCEPTION = e;
    }

    public GalleryServerImplWSService() {
        super(__getWsdlLocation(), GALLERYSERVERIMPLWSSERVICE_QNAME);
    }

    public GalleryServerImplWSService(WebServiceFeature... features) {
        super(__getWsdlLocation(), GALLERYSERVERIMPLWSSERVICE_QNAME, features);
    }

    public GalleryServerImplWSService(URL wsdlLocation) {
        super(wsdlLocation, GALLERYSERVERIMPLWSSERVICE_QNAME);
    }

    public GalleryServerImplWSService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, GALLERYSERVERIMPLWSSERVICE_QNAME, features);
    }

    public GalleryServerImplWSService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public GalleryServerImplWSService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns GalleryServerImplWS
     */
    @WebEndpoint(name = "GalleryServerImplWSPort")
    public GalleryServerImplWS getGalleryServerImplWSPort() {
        return super.getPort(new QName("http://srv.tp1.sd/", "GalleryServerImplWSPort"), GalleryServerImplWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns GalleryServerImplWS
     */
    @WebEndpoint(name = "GalleryServerImplWSPort")
    public GalleryServerImplWS getGalleryServerImplWSPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://srv.tp1.sd/", "GalleryServerImplWSPort"), GalleryServerImplWS.class, features);
    }

    private static URL __getWsdlLocation() {
        if (GALLERYSERVERIMPLWSSERVICE_EXCEPTION!= null) {
            throw GALLERYSERVERIMPLWSSERVICE_EXCEPTION;
        }
        return GALLERYSERVERIMPLWSSERVICE_WSDL_LOCATION;
    }

}
