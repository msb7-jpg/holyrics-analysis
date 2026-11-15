package com.limagiran.holyrics.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(
   style = Style.RPC
)
interface IHolyricsWS {
   @WebMethod
   String getSharedFiles(String var1);

   @WebMethod
   String sendSharedFiles(String var1);

   @WebMethod
   String initRemoteControl(String var1);

   @WebMethod
   String changeRemoteControlIndex(String var1);

   @WebMethod
   String getStreaming(String var1);

   @WebMethod
   String getPlaylist(String var1);

   @WebMethod
   String webService(String var1);
}
