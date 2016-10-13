/*
 * To change this license header, choose License Headers in Project Properties.    
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srvmonitor;
import utilities.globalAreaData;
import java.io.* ; 
import java.net.* ;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import utilities.srvRutinas;

/**
 *
 * @author andresbenitez
 */
public class thMonitorSocket extends Thread {
    static srvRutinas gSub;
    static globalAreaData gDatos;
    static boolean isSocketActive;
    static String errNum;
    static String errDesc;
    Logger logger = Logger.getLogger("thServerSocket");
    
    //Carga constructor para inicializar los datos
    public thMonitorSocket(globalAreaData m) {
        gDatos = m;
        gSub = new srvRutinas(gDatos);
        isSocketActive  = true;
    }
    
    @Override
    public void run() {
        try {
            logger.info("Starting Listener Thread Monitor Server port: " + gDatos.getServerInfo().getSrvPort());
            ServerSocket skServidor = new ServerSocket(gDatos.getServerInfo().getSrvPort());
            String inputData;
            String outputData;
            String dRequest;
            String dAuth;
            JSONObject jHeader;
            JSONObject jData;
            int result;
            
            while (isSocketActive) {
                Socket skCliente = skServidor.accept();
                InputStream inpStr = skCliente.getInputStream();
                DataInputStream dataInput = new DataInputStream( inpStr );
                
                //Espera Entrada
                //
                try {
                    inputData  = dataInput.readUTF();
                    logger.info("Recibiendo TX: "+inputData);
                    
                    jHeader = new JSONObject(inputData);
                    jData = jHeader.getJSONObject("data");
                    
                    dAuth = jHeader.getString("auth");
                    dRequest = jHeader.getString("request");

                    if (dAuth.equals(gDatos.getServerInfo().getAuthKey())) {

                        switch (dRequest) {
                            case "keepAlive":
                                result = gSub.updateStatusService(jData.getJSONObject("ServiceStatus"));
                                if (result==0) {
                                    outputData = gSub.sendAssignedProc(jData.getJSONObject("ServiceStatus").getString("srvID"));
                                } else {
                                    outputData = gSub.sendError(10);
                                }
                                break;
                            case "getDate":
                                outputData = gSub.sendDate();
                                break;
                            case "getStatus":
                                logger.info("ejecutando ... getStatusServices");
                                outputData = gSub.sendStatusServices();
                                break;
                            case "putExecOSP":
                                gSub.putExecOSP(inputData);
                                outputData = gSub.sendOkTX();
                                break;
                            case "sendPing":
                                outputData = "OK";
                                break;
                            default:
                                outputData = gSub.sendError(99, "Error Desconocido...");
                        }
                    } else {
                        outputData = gSub.sendError(60);
                    }
                } catch (IOException | JSONException e) {
                    outputData = gSub.sendError(90);
                }
                     
                //Envia Respuesta
                //
                OutputStream outStr = skCliente.getOutputStream();
                DataOutputStream dataOutput = new DataOutputStream(outStr);
                logger.debug("Enviando respuesta: "+ outputData);
                if (outputData==null) {
                    dataOutput.writeUTF("{}");
                } else {
                    dataOutput.writeUTF(outputData);
                }
                
                //Cierra Todas las conexiones
                //
                inpStr.close();
                dataInput.close();
                skCliente.close();
            }
        
        } catch (NumberFormatException | IOException e) {
            logger.error(e.getMessage());
        }
    }
}
