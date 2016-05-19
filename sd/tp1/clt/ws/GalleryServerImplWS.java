
package sd.tp1.clt.ws;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "GalleryServerImplWS", targetNamespace = "http://srv.tp1.sd/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface GalleryServerImplWS {


    /**
     * 
     * @param arg0
     * @return
     *     returns java.util.List<sd.tp1.clt.ws.PictureClass>
     * @throws AlbumNotFoundException_Exception
     */
    @WebMethod(operationName = "ListPictures")
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "ListPictures", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.ListPictures")
    @ResponseWrapper(localName = "ListPicturesResponse", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.ListPicturesResponse")
    @Action(input = "http://srv.tp1.sd/GalleryServerImplWS/ListPicturesRequest", output = "http://srv.tp1.sd/GalleryServerImplWS/ListPicturesResponse", fault = {
        @FaultAction(className = AlbumNotFoundException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/ListPictures/Fault/AlbumNotFoundException")
    })
    public List<PictureClass> listPictures(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws AlbumNotFoundException_Exception
    ;

    /**
     * 
     * @return
     *     returns java.util.List<sd.tp1.clt.ws.AlbumFolderClass>
     * @throws GalleryNotFoundException_Exception
     */
    @WebMethod(operationName = "ListAlbums")
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "ListAlbums", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.ListAlbums")
    @ResponseWrapper(localName = "ListAlbumsResponse", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.ListAlbumsResponse")
    @Action(input = "http://srv.tp1.sd/GalleryServerImplWS/ListAlbumsRequest", output = "http://srv.tp1.sd/GalleryServerImplWS/ListAlbumsResponse", fault = {
        @FaultAction(className = GalleryNotFoundException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/ListAlbums/Fault/GalleryNotFoundException")
    })
    public List<AlbumFolderClass> listAlbums()
        throws GalleryNotFoundException_Exception
    ;

    /**
     * 
     * @param arg1
     * @param arg0
     * @throws AlbumNotFoundException_Exception
     * @throws PictureNotfoundException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "deletePicture", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.DeletePicture")
    @ResponseWrapper(localName = "deletePictureResponse", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.DeletePictureResponse")
    @Action(input = "http://srv.tp1.sd/GalleryServerImplWS/deletePictureRequest", output = "http://srv.tp1.sd/GalleryServerImplWS/deletePictureResponse", fault = {
        @FaultAction(className = AlbumNotFoundException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/deletePicture/Fault/AlbumNotFoundException"),
        @FaultAction(className = PictureNotfoundException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/deletePicture/Fault/PictureNotfoundException")
    })
    public void deletePicture(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1)
        throws AlbumNotFoundException_Exception, PictureNotfoundException_Exception
    ;

    /**
     * 
     * @param arg0
     * @throws AlbumAlreadyExistsException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "creatAlbum", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.CreatAlbum")
    @ResponseWrapper(localName = "creatAlbumResponse", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.CreatAlbumResponse")
    @Action(input = "http://srv.tp1.sd/GalleryServerImplWS/creatAlbumRequest", output = "http://srv.tp1.sd/GalleryServerImplWS/creatAlbumResponse", fault = {
        @FaultAction(className = AlbumAlreadyExistsException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/creatAlbum/Fault/AlbumAlreadyExistsException")
    })
    public void creatAlbum(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws AlbumAlreadyExistsException_Exception
    ;

    /**
     * 
     * @param arg0
     * @throws AlbumNotFoundException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "deleteAlbum", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.DeleteAlbum")
    @ResponseWrapper(localName = "deleteAlbumResponse", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.DeleteAlbumResponse")
    @Action(input = "http://srv.tp1.sd/GalleryServerImplWS/deleteAlbumRequest", output = "http://srv.tp1.sd/GalleryServerImplWS/deleteAlbumResponse", fault = {
        @FaultAction(className = AlbumNotFoundException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/deleteAlbum/Fault/AlbumNotFoundException")
    })
    public void deleteAlbum(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws AlbumNotFoundException_Exception
    ;

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns byte[]
     * @throws AlbumNotFoundException_Exception
     * @throws IOException_Exception
     * @throws PictureNotfoundException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getPicture", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.GetPicture")
    @ResponseWrapper(localName = "getPictureResponse", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.GetPictureResponse")
    @Action(input = "http://srv.tp1.sd/GalleryServerImplWS/getPictureRequest", output = "http://srv.tp1.sd/GalleryServerImplWS/getPictureResponse", fault = {
        @FaultAction(className = AlbumNotFoundException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/getPicture/Fault/AlbumNotFoundException"),
        @FaultAction(className = IOException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/getPicture/Fault/IOException"),
        @FaultAction(className = PictureNotfoundException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/getPicture/Fault/PictureNotfoundException")
    })
    public byte[] getPicture(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1)
        throws AlbumNotFoundException_Exception, IOException_Exception, PictureNotfoundException_Exception
    ;

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @throws AlbumNotFoundException_Exception
     * @throws PictureAlreadyExistsException_Exception
     * @throws IOException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "uploadPicture", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.UploadPicture")
    @ResponseWrapper(localName = "uploadPictureResponse", targetNamespace = "http://srv.tp1.sd/", className = "sd.tp1.clt.ws.UploadPictureResponse")
    @Action(input = "http://srv.tp1.sd/GalleryServerImplWS/uploadPictureRequest", output = "http://srv.tp1.sd/GalleryServerImplWS/uploadPictureResponse", fault = {
        @FaultAction(className = AlbumNotFoundException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/uploadPicture/Fault/AlbumNotFoundException"),
        @FaultAction(className = IOException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/uploadPicture/Fault/IOException"),
        @FaultAction(className = PictureAlreadyExistsException_Exception.class, value = "http://srv.tp1.sd/GalleryServerImplWS/uploadPicture/Fault/PictureAlreadyExistsException")
    })
    public void uploadPicture(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        byte[] arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2)
        throws AlbumNotFoundException_Exception, IOException_Exception, PictureAlreadyExistsException_Exception
    ;

}
