package com.limagiran.holyrics.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(
   style = Style.RPC
)
interface IRemoteControlBibleWS {
   @WebMethod
   boolean connect(String var1);

   @WebMethod
   boolean isConnected();

   @WebMethod
   String getMapChaptersAndVerses();

   @WebMethod
   String goTo(int var1, int var2, int var3);

   @WebMethod
   String getCurrentID_VersePreviousAndNextVerse();

   @WebMethod
   String show(int var1, int var2, int var3);

   @WebMethod
   void exit();

   @WebMethod
   String next(int var1, int var2, int var3, boolean var4);

   @WebMethod
   String previous(int var1, int var2, int var3, boolean var4);

   @WebMethod
   String webService(String var1);

   @WebMethod
   String ws(String var1);
}
