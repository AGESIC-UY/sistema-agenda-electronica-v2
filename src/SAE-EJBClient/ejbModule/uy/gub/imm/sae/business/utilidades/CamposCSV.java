/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.imm.sae.business.utilidades;

/**
 *
 * @author tincho
 */
public class CamposCSV {
    private String nombreRecurso;
    private String direccion;
//    private String coordenadas;
    private String latitud;
    private String longitud;
    private String telefonos;
    private String horarios;
    private String fechaInicio;
    private String fechaFin;
    private String lunes;
    private String martes;
    private String miercoles;
    private String jueves;
    private String viernes;
    private String sabado;
    private String domingo;

    public CamposCSV() {

    }

    public CamposCSV(String nombreRecurso, String direccion, String latitud, String longitud, String telefonos, String horarios, String fechaInicio
    , String fechaFin, String lunes, String martes, String miercoles, String jueves, String viernes, String sabado, String domingo) {
        
        this.nombreRecurso = nombreRecurso;
        this.direccion = direccion;
//        this.coordenadas = coordenadas;
        this.latitud = latitud;
        this.longitud = longitud;
        this.telefonos = telefonos;
        this.horarios = horarios;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.lunes = lunes;
        this.martes = martes;
        this.miercoles = miercoles;
        this.jueves = jueves;
        this.viernes = viernes;
        this.sabado = sabado;
        this.domingo = domingo;
    }


    public String getNombreRecurso() {
        return nombreRecurso;
    }

    public void setNombreRecurso(String nombreRecurso) {
        this.nombreRecurso = nombreRecurso;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

//    public String getCoordenadas() {
//        return coordenadas;
//    }
//
//    public void setCoordenadas(String coordenadas) {
//        this.coordenadas = coordenadas;
//    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getLunes() {
        return lunes;
    }

    public void setLunes(String lunes) {
        this.lunes = lunes;
    }

    public String getMartes() {
        return martes;
    }

    public void setMartes(String martes) {
        this.martes = martes;
    }

    public String getMiercoles() {
        return miercoles;
    }

    public void setMiercoles(String miercoles) {
        this.miercoles = miercoles;
    }

    public String getJueves() {
        return jueves;
    }

    public void setJueves(String jueves) {
        this.jueves = jueves;
    }

    public String getViernes() {
        return viernes;
    }

    public void setViernes(String viernes) {
        this.viernes = viernes;
    }

    public String getSabado() {
        return sabado;
    }

    public void setSabado(String sabado) {
        this.sabado = sabado;
    }

    public String getDomingo() {
        return domingo;
    }

    public void setDomingo(String domingo) {
        this.domingo = domingo;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
    
    

    public String getCamposCSV() {
        return  nombreRecurso + " ; " + direccion + " ; " + latitud + "; " + longitud + " ; " + telefonos + " ; " + horarios + " ; " + fechaInicio + " ; " + fechaFin
        + " ; " + lunes + " ; " + martes + " ; " + miercoles + " ; " + jueves + " ; " + viernes + " ; " + sabado + " ; " + domingo;
    }
    
}
