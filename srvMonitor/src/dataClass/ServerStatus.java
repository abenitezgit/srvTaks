/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataClass;

import java.sql.Connection;

/**
 *
 * @author andresbenitez
 */
public class ServerStatus {
    boolean isSocketServerActive;
    boolean isKeepAliveActive;
    boolean isGetAgendaActive;
    boolean isMetadataConnect;
    boolean srvLoadParam;
    boolean srvActive;
    Connection metadataConnection;
    int numProcMax;
    String srvStartTime;

    public boolean isIsGetAgendaActive() {
        return isGetAgendaActive;
    }

    public void setIsGetAgendaActive(boolean isGetAgendaActive) {
        this.isGetAgendaActive = isGetAgendaActive;
    }
    
    public String getSrvStartTime() {
        return srvStartTime;
    }

    public void setSrvStartTime(String srvStartTime) {
        this.srvStartTime = srvStartTime;
    }

    public boolean isSrvActive() {
        return srvActive;
    }

    public void setSrvActive(boolean srvActive) {
        this.srvActive = srvActive;
    }
    
    public int getNumProcMax() {
        return numProcMax;
    }

    public void setNumProcMax(int numProcMax) {
        this.numProcMax = numProcMax;
    }

    public boolean isSrvLoadParam() {
        return srvLoadParam;
    }

    public void setSrvLoadParam(boolean srvLoadParam) {
        this.srvLoadParam = srvLoadParam;
    }
    
    public Connection getMetadataConnection() {
        return metadataConnection;
    }

    public void setMetadataConnection(Connection metadataConnection) {
        this.metadataConnection = metadataConnection;
    }
    
    

    public boolean isIsMetadataConnect() {
        return isMetadataConnect;
    }

    public void setIsMetadataConnect(boolean isMetadataConnect) {
        this.isMetadataConnect = isMetadataConnect;
    }
    
    

    public boolean isIsSocketServerActive() {
        return isSocketServerActive;
    }

    public void setIsSocketServerActive(boolean isSocketServerActive) {
        this.isSocketServerActive = isSocketServerActive;
    }

    public boolean isIsKeepAliveActive() {
        return isKeepAliveActive;
    }

    public void setIsKeepAliveActive(boolean isKeepAliveActive) {
        this.isKeepAliveActive = isKeepAliveActive;
    }

    
    
}
