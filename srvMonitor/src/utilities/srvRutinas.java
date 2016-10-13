/*
 * To change this license header, choose License Headers in Project Properties.nnn
 */
package utilities;

import dataClass.AssignedTypeProc;
import dataClass.ServiceStatus;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author andresbenitez
 */
public class srvRutinas {
    globalAreaData gDatos;
    
    //Constructor de la clase
    //
    public srvRutinas(globalAreaData m) {
        gDatos = m;
    }
    
    public void sysOutln(Object obj) {
        System.out.println(obj);
    }
    
    public String getDateNow(String xformat) {
        try {
            //Extrae Fecha de Hoy
            //
            Date today;
            SimpleDateFormat formatter;
            formatter = new SimpleDateFormat(xformat);
            System.out.println(formatter.getTimeZone());
            today = new Date();
            return formatter.format(today);  
        } catch (Exception e) {
            return null;
        }
    }
        
    public String appendJSonParam(String cadena, String param, String valor) {
        String output="{}";
        
        try {
            if (cadena==null) {cadena="{}";}
            
            JSONObject obj = new JSONObject(cadena);
            obj.append(param, valor);
            output = obj.toString();
        
            return output;
        } catch (Exception e) {
            return output;
        }
    }
    
    public String sendError(int errCode, String errMesg) {
        JSONObject jData = new JSONObject();
        JSONObject jHeader = new JSONObject();
    
        jData.put("errMesg", errMesg);
        jData.put("errCode", errCode);
        
        jHeader.put("data",jData);
        jHeader.put("result", "error");
            
        return jHeader.toString();
    }
    
    public String sendError(int errCode) {
        String errMesg;
        
        switch (errCode) {
            case 90: 
                    errMesg = "error de entrada";
                    break;
            case 80: 
                    errMesg = "servicio offlne";
                    break;
            case 60:
                    errMesg = "TX no autorizada";
                    break;
            default: 
                    errMesg = "error desconocido";
                    break;
        }
        
        JSONObject jData = new JSONObject();
        JSONObject jHeader = new JSONObject();
    
        jData.put("errMesg", errMesg);
        jData.put("errCode", errCode);
        
        jHeader.put("data",jData);
        jHeader.put("result", "error");
            
        return jHeader.toString();
    }

    public String sendOkTX() {
        
        JSONObject jData = new JSONObject();
        JSONObject jHeader = new JSONObject();
        
        jHeader.put("data", jData);
        jHeader.put("result", "OK");
            
        return jHeader.toString();
    }
    
    public void putExecOSP(String inputData) {
        try {
            JSONObject ds = new JSONObject(inputData);
            JSONArray rows = ds.getJSONArray("params");
            JSONObject row = rows.getJSONObject(0); //Recupera el primero registro de la lista
        } catch (Exception e) {
            sysOutln(e);
        }    
    }

    public String sendStatusServices() {
        try {
            JSONObject jData = new JSONObject();
            JSONObject jHeader = new JSONObject();
            JSONArray jArray;
            ObjectMapper mapper = new ObjectMapper();
            
            jArray = new JSONArray(mapper.writeValueAsString(gDatos.getLstServiceStatus()));
            
            jData.put("servicios", jArray);
            jHeader.put("data",jData);
            jHeader.put("result", "OK");

            return jHeader.toString();
        } catch (IOException | JSONException e) {
            return sendError(0,e.getMessage());
        }
    }
        
    public int updateStatusService(JSONObject jData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ServiceStatus serviceStatus;
            List<AssignedTypeProc> lstAssignedTypeProc;
            
            serviceStatus = mapper.readValue(jData.toString(), ServiceStatus.class);
            int numItems = gDatos.getLstServiceStatus().size();
            boolean itemFound = false;
            
            for (int i=0; i<numItems; i++) {
                if (gDatos.getLstServiceStatus().get(i).getSrvID().equals(serviceStatus.getSrvID())) {
                    lstAssignedTypeProc = gDatos.getLstServiceStatus().get(i).getLstAssignedTypeProc();
                    serviceStatus.setLstAssignedTypeProc(lstAssignedTypeProc);
                    gDatos.getLstServiceStatus().set(i, serviceStatus);
                    itemFound = true;
                }
            }
            
            if (!itemFound) {
                gDatos.getLstServiceStatus().add(serviceStatus);
            }
            
            return 0;
        } catch (JSONException | IOException e) {
            return 1;
        }
    }
    
    public String sendAssignedProc(String srvID) {
        try {
            List<AssignedTypeProc> lstAssignedTypeProc = null;
            
            JSONObject jData = new JSONObject();
            JSONObject jHeader = new JSONObject();
            
            ObjectMapper mapper = new ObjectMapper();
            
            int numItems = gDatos.getLstServiceStatus().size();
            
            for (int i=0; i<numItems; i++) {
                if (gDatos.getLstServiceStatus().get(i).getSrvID().equals(srvID)) {
                    lstAssignedTypeProc = gDatos.getLstServiceStatus().get(i).getLstAssignedTypeProc();
                }
            
            }
            
            JSONArray assignedTypeProc = new JSONArray(mapper.writeValueAsString(lstAssignedTypeProc));

            jData.put("AssignedTypeProc", assignedTypeProc);
            jHeader.put("data",jData);
            jHeader.put("result", "OK");
            return jHeader.toString();
        } catch (IOException | JSONException e) {
            return sendError(1,e.getMessage());
        }
    }
    
    public String sendDate() {
        try {
            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray();
            JSONObject mainjo = new JSONObject();

            jo.put("fecha", getDateNow("yyyy-MM-dd HH:mm:ss"));
            ja.put(jo);
            mainjo.put("params", ja);
            mainjo.put("result", "getDate");
            return mainjo.toString();
        } catch (Exception e) {
            return sendError(99, e.getMessage());
        }
    }
                  
    public int getMDprocAssigned() throws SQLException, IOException {
        JSONArray ja;
        JSONObject jo = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        ServiceStatus serviceStatus;
        
        try {
            String vSQL = "select srvID, srvDesc, srvEnable, srvTypeProc "
                    + "     from process.tb_services"
                    + "     order by srvID";
            Statement stm;
            stm = gDatos.getServerStatus().getMetadataConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stm.executeQuery(vSQL);
            if (rs!=null) {
                while (rs.next()) {
                          
                    ja = new JSONArray(rs.getString("srvTypeProc"));
                    jo.put("lstAssignedTypeProc", ja);
                    jo.put("srvID", rs.getString("srvID"));
                    jo.put("srvEnable", rs.getInt("srvEnable"));
                    
                    serviceStatus = mapper.readValue(jo.toString(), ServiceStatus.class);
                    
                    gDatos.updateLstServiceStatus(serviceStatus);
                    //gDatos.getLstServiceStatus().add(serviceStatus);
                }
            }
            mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
            sysOutln(mapper.writeValueAsString(gDatos.getLstServiceStatus()));
        
            return 0;
        } catch (SQLException | JSONException e) {
            sysOutln(e.getMessage());
            return 1;
        }
    }
}
