
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

    private final static QName _ListAlbums_QNAME = new QName("http://srv.tp1.sd/", "ListAlbums");
    private final static QName _AlbumNotFoundException_QNAME = new QName("http://srv.tp1.sd/", "AlbumNotFoundException");
    private final static QName _PrintCenas_QNAME = new QName("http://srv.tp1.sd/", "printCenas");
    private final static QName _ListAlbumsResponse_QNAME = new QName("http://srv.tp1.sd/", "ListAlbumsResponse");
    private final static QName _ListPictures_QNAME = new QName("http://srv.tp1.sd/", "ListPictures");
    private final static QName _ListPicturesResponse_QNAME = new QName("http://srv.tp1.sd/", "ListPicturesResponse");
    private final static QName _GalleryNotFoundException_QNAME = new QName("http://srv.tp1.sd/", "GalleryNotFoundException");
    private final static QName _PrintCenasResponse_QNAME = new QName("http://srv.tp1.sd/", "printCenasResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: sd.tp1.clt.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ListAlbumsResponse }
     * 
     */
    public ListAlbumsResponse createListAlbumsResponse() {
        return new ListAlbumsResponse();
    }

    /**
     * Create an instance of {@link ListPictures }
     * 
     */
    public ListPictures createListPictures() {
        return new ListPictures();
    }

    /**
     * Create an instance of {@link ListPicturesResponse }
     * 
     */
    public ListPicturesResponse createListPicturesResponse() {
        return new ListPicturesResponse();
    }

    /**
     * Create an instance of {@link AlbumNotFoundException }
     * 
     */
    public AlbumNotFoundException createAlbumNotFoundException() {
        return new AlbumNotFoundException();
    }

    /**
     * Create an instance of {@link PrintCenas }
     * 
     */
    public PrintCenas createPrintCenas() {
        return new PrintCenas();
    }

    /**
     * Create an instance of {@link ListAlbums }
     * 
     */
    public ListAlbums createListAlbums() {
        return new ListAlbums();
    }

    /**
     * Create an instance of {@link PrintCenasResponse }
     * 
     */
    public PrintCenasResponse createPrintCenasResponse() {
        return new PrintCenasResponse();
    }

    /**
     * Create an instance of {@link GalleryNotFoundException }
     * 
     */
    public GalleryNotFoundException createGalleryNotFoundException() {
        return new GalleryNotFoundException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListAlbums }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "ListAlbums")
    public JAXBElement<ListAlbums> createListAlbums(ListAlbums value) {
        return new JAXBElement<ListAlbums>(_ListAlbums_QNAME, ListAlbums.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlbumNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "AlbumNotFoundException")
    public JAXBElement<AlbumNotFoundException> createAlbumNotFoundException(AlbumNotFoundException value) {
        return new JAXBElement<AlbumNotFoundException>(_AlbumNotFoundException_QNAME, AlbumNotFoundException.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ListAlbumsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "ListAlbumsResponse")
    public JAXBElement<ListAlbumsResponse> createListAlbumsResponse(ListAlbumsResponse value) {
        return new JAXBElement<ListAlbumsResponse>(_ListAlbumsResponse_QNAME, ListAlbumsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListPictures }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "ListPictures")
    public JAXBElement<ListPictures> createListPictures(ListPictures value) {
        return new JAXBElement<ListPictures>(_ListPictures_QNAME, ListPictures.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListPicturesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "ListPicturesResponse")
    public JAXBElement<ListPicturesResponse> createListPicturesResponse(ListPicturesResponse value) {
        return new JAXBElement<ListPicturesResponse>(_ListPicturesResponse_QNAME, ListPicturesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GalleryNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "GalleryNotFoundException")
    public JAXBElement<GalleryNotFoundException> createGalleryNotFoundException(GalleryNotFoundException value) {
        return new JAXBElement<GalleryNotFoundException>(_GalleryNotFoundException_QNAME, GalleryNotFoundException.class, null, value);
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
