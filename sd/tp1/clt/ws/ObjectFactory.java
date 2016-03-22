
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

    private final static QName _GetPicture_QNAME = new QName("http://srv.tp1.sd/", "getPicture");
    private final static QName _ListPictures_QNAME = new QName("http://srv.tp1.sd/", "ListPictures");
    private final static QName _DeleteAlbum_QNAME = new QName("http://srv.tp1.sd/", "deleteAlbum");
    private final static QName _UploadPictureResponse_QNAME = new QName("http://srv.tp1.sd/", "uploadPictureResponse");
    private final static QName _AlbumAlreadyExistsException_QNAME = new QName("http://srv.tp1.sd/", "AlbumAlreadyExistsException");
    private final static QName _DeletePictureResponse_QNAME = new QName("http://srv.tp1.sd/", "deletePictureResponse");
    private final static QName _UploadPicture_QNAME = new QName("http://srv.tp1.sd/", "uploadPicture");
    private final static QName _ListAlbums_QNAME = new QName("http://srv.tp1.sd/", "ListAlbums");
    private final static QName _AlbumNotFoundException_QNAME = new QName("http://srv.tp1.sd/", "AlbumNotFoundException");
    private final static QName _GetPictureResponse_QNAME = new QName("http://srv.tp1.sd/", "getPictureResponse");
    private final static QName _ListAlbumsResponse_QNAME = new QName("http://srv.tp1.sd/", "ListAlbumsResponse");
    private final static QName _ListPicturesResponse_QNAME = new QName("http://srv.tp1.sd/", "ListPicturesResponse");
    private final static QName _PictureAlreadyExistsException_QNAME = new QName("http://srv.tp1.sd/", "PictureAlreadyExistsException");
    private final static QName _DeleteAlbumResponse_QNAME = new QName("http://srv.tp1.sd/", "deleteAlbumResponse");
    private final static QName _CreatAlbumResponse_QNAME = new QName("http://srv.tp1.sd/", "creatAlbumResponse");
    private final static QName _PictureNotfoundException_QNAME = new QName("http://srv.tp1.sd/", "PictureNotfoundException");
    private final static QName _GalleryNotFoundException_QNAME = new QName("http://srv.tp1.sd/", "GalleryNotFoundException");
    private final static QName _IOException_QNAME = new QName("http://srv.tp1.sd/", "IOException");
    private final static QName _CreatAlbum_QNAME = new QName("http://srv.tp1.sd/", "creatAlbum");
    private final static QName _DeletePicture_QNAME = new QName("http://srv.tp1.sd/", "deletePicture");
    private final static QName _UploadPictureArg1_QNAME = new QName("", "arg1");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: sd.tp1.clt.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PictureNotfoundException }
     * 
     */
    public PictureNotfoundException createPictureNotfoundException() {
        return new PictureNotfoundException();
    }

    /**
     * Create an instance of {@link CreatAlbumResponse }
     * 
     */
    public CreatAlbumResponse createCreatAlbumResponse() {
        return new CreatAlbumResponse();
    }

    /**
     * Create an instance of {@link PictureAlreadyExistsException }
     * 
     */
    public PictureAlreadyExistsException createPictureAlreadyExistsException() {
        return new PictureAlreadyExistsException();
    }

    /**
     * Create an instance of {@link DeleteAlbumResponse }
     * 
     */
    public DeleteAlbumResponse createDeleteAlbumResponse() {
        return new DeleteAlbumResponse();
    }

    /**
     * Create an instance of {@link ListAlbumsResponse }
     * 
     */
    public ListAlbumsResponse createListAlbumsResponse() {
        return new ListAlbumsResponse();
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
     * Create an instance of {@link GetPictureResponse }
     * 
     */
    public GetPictureResponse createGetPictureResponse() {
        return new GetPictureResponse();
    }

    /**
     * Create an instance of {@link ListAlbums }
     * 
     */
    public ListAlbums createListAlbums() {
        return new ListAlbums();
    }

    /**
     * Create an instance of {@link UploadPicture }
     * 
     */
    public UploadPicture createUploadPicture() {
        return new UploadPicture();
    }

    /**
     * Create an instance of {@link DeletePicture }
     * 
     */
    public DeletePicture createDeletePicture() {
        return new DeletePicture();
    }

    /**
     * Create an instance of {@link CreatAlbum }
     * 
     */
    public CreatAlbum createCreatAlbum() {
        return new CreatAlbum();
    }

    /**
     * Create an instance of {@link IOException }
     * 
     */
    public IOException createIOException() {
        return new IOException();
    }

    /**
     * Create an instance of {@link GalleryNotFoundException }
     * 
     */
    public GalleryNotFoundException createGalleryNotFoundException() {
        return new GalleryNotFoundException();
    }

    /**
     * Create an instance of {@link UploadPictureResponse }
     * 
     */
    public UploadPictureResponse createUploadPictureResponse() {
        return new UploadPictureResponse();
    }

    /**
     * Create an instance of {@link ListPictures }
     * 
     */
    public ListPictures createListPictures() {
        return new ListPictures();
    }

    /**
     * Create an instance of {@link DeleteAlbum }
     * 
     */
    public DeleteAlbum createDeleteAlbum() {
        return new DeleteAlbum();
    }

    /**
     * Create an instance of {@link GetPicture }
     * 
     */
    public GetPicture createGetPicture() {
        return new GetPicture();
    }

    /**
     * Create an instance of {@link DeletePictureResponse }
     * 
     */
    public DeletePictureResponse createDeletePictureResponse() {
        return new DeletePictureResponse();
    }

    /**
     * Create an instance of {@link AlbumAlreadyExistsException }
     * 
     */
    public AlbumAlreadyExistsException createAlbumAlreadyExistsException() {
        return new AlbumAlreadyExistsException();
    }

    /**
     * Create an instance of {@link PictureClass }
     * 
     */
    public PictureClass createPictureClass() {
        return new PictureClass();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPicture }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "getPicture")
    public JAXBElement<GetPicture> createGetPicture(GetPicture value) {
        return new JAXBElement<GetPicture>(_GetPicture_QNAME, GetPicture.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAlbum }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "deleteAlbum")
    public JAXBElement<DeleteAlbum> createDeleteAlbum(DeleteAlbum value) {
        return new JAXBElement<DeleteAlbum>(_DeleteAlbum_QNAME, DeleteAlbum.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadPictureResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "uploadPictureResponse")
    public JAXBElement<UploadPictureResponse> createUploadPictureResponse(UploadPictureResponse value) {
        return new JAXBElement<UploadPictureResponse>(_UploadPictureResponse_QNAME, UploadPictureResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlbumAlreadyExistsException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "AlbumAlreadyExistsException")
    public JAXBElement<AlbumAlreadyExistsException> createAlbumAlreadyExistsException(AlbumAlreadyExistsException value) {
        return new JAXBElement<AlbumAlreadyExistsException>(_AlbumAlreadyExistsException_QNAME, AlbumAlreadyExistsException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePictureResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "deletePictureResponse")
    public JAXBElement<DeletePictureResponse> createDeletePictureResponse(DeletePictureResponse value) {
        return new JAXBElement<DeletePictureResponse>(_DeletePictureResponse_QNAME, DeletePictureResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UploadPicture }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "uploadPicture")
    public JAXBElement<UploadPicture> createUploadPicture(UploadPicture value) {
        return new JAXBElement<UploadPicture>(_UploadPicture_QNAME, UploadPicture.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPictureResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "getPictureResponse")
    public JAXBElement<GetPictureResponse> createGetPictureResponse(GetPictureResponse value) {
        return new JAXBElement<GetPictureResponse>(_GetPictureResponse_QNAME, GetPictureResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ListPicturesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "ListPicturesResponse")
    public JAXBElement<ListPicturesResponse> createListPicturesResponse(ListPicturesResponse value) {
        return new JAXBElement<ListPicturesResponse>(_ListPicturesResponse_QNAME, ListPicturesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PictureAlreadyExistsException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "PictureAlreadyExistsException")
    public JAXBElement<PictureAlreadyExistsException> createPictureAlreadyExistsException(PictureAlreadyExistsException value) {
        return new JAXBElement<PictureAlreadyExistsException>(_PictureAlreadyExistsException_QNAME, PictureAlreadyExistsException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAlbumResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "deleteAlbumResponse")
    public JAXBElement<DeleteAlbumResponse> createDeleteAlbumResponse(DeleteAlbumResponse value) {
        return new JAXBElement<DeleteAlbumResponse>(_DeleteAlbumResponse_QNAME, DeleteAlbumResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatAlbumResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "creatAlbumResponse")
    public JAXBElement<CreatAlbumResponse> createCreatAlbumResponse(CreatAlbumResponse value) {
        return new JAXBElement<CreatAlbumResponse>(_CreatAlbumResponse_QNAME, CreatAlbumResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PictureNotfoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "PictureNotfoundException")
    public JAXBElement<PictureNotfoundException> createPictureNotfoundException(PictureNotfoundException value) {
        return new JAXBElement<PictureNotfoundException>(_PictureNotfoundException_QNAME, PictureNotfoundException.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link IOException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "IOException")
    public JAXBElement<IOException> createIOException(IOException value) {
        return new JAXBElement<IOException>(_IOException_QNAME, IOException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreatAlbum }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "creatAlbum")
    public JAXBElement<CreatAlbum> createCreatAlbum(CreatAlbum value) {
        return new JAXBElement<CreatAlbum>(_CreatAlbum_QNAME, CreatAlbum.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePicture }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://srv.tp1.sd/", name = "deletePicture")
    public JAXBElement<DeletePicture> createDeletePicture(DeletePicture value) {
        return new JAXBElement<DeletePicture>(_DeletePicture_QNAME, DeletePicture.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg1", scope = UploadPicture.class)
    public JAXBElement<byte[]> createUploadPictureArg1(byte[] value) {
        return new JAXBElement<byte[]>(_UploadPictureArg1_QNAME, byte[].class, UploadPicture.class, ((byte[]) value));
    }

}
