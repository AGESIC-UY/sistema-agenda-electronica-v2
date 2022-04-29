/*
 * SAE - Sistema de Agenda Electronica
 * Copyright (C) 2009  IMM - Intendencia Municipal de Montevideo
 *
 * This file is part of SAE.

 * SAE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table (name = "ae_acciones_miperfil_recurso")
public class AccionMiPerfil implements Serializable {
	private static final long serialVersionUID = -5197426783029830293L;
	

	 
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_acciones_miperfil_gen")
    @SequenceGenerator(name = "seq_acciones_miperfil_gen", sequenceName = "s_ae_acciones_miperfil", allocationSize = 1)
    @Column(name = "id")
    private Integer id;
	
	@OneToOne
    @JoinColumn(name = "recurso_id")
    private Recurso recurso;
	
	
	
	   
    @Column(name = "titulo_con_1")
    private String titulo_con_1;
    
    @Column(name = "url_con_1")
    private String url_con_1;
    
    @Column(name = "destacada_con_1")
    private Boolean destacada_con_1;
    
    @Column(name = "titulo_con_2")
    private String titulo_con_2;
    
    @Column(name = "url_con_2")
    private String url_con_2;
    
    @Column(name = "destacada_con_2")
    private Boolean destacada_con_2;
    
    @Column(name = "titulo_con_3")
    private String titulo_con_3;
    
    @Column(name = "url_con_3")
    private String url_con_3;
    
    @Column(name = "destacada_con_3")
    private Boolean destacada_con_3;
    
    @Column(name = "titulo_con_4")
    private String titulo_con_4;
    
    @Column(name = "url_con_4")
    private String url_con_4;
    
    @Column(name = "destacada_con_4")
    private Boolean destacada_con_4;
    
    @Column(name = "titulo_con_5")
    private String titulo_con_5;
    
    @Column(name = "url_con_5")
    private String url_con_5;
    
    @Column(name = "destacada_con_5")
    private Boolean destacada_con_5;
    
    
    
	   
    @Column(name = "titulo_can_1")
    private String titulo_can_1;
    
    @Column(name = "url_can_1")
    private String url_can_1;
    
    @Column(name = "destacada_can_1")
    private Boolean destacada_can_1;
    
    @Column(name = "titulo_can_2")
    private String titulo_can_2;
    
    @Column(name = "url_can_2")
    private String url_can_2;
    
    @Column(name = "destacada_can_2")
    private Boolean destacada_can_2;
    
    @Column(name = "titulo_can_3")
    private String titulo_can_3;
    
    @Column(name = "url_can_3")
    private String url_can_3;
    
    @Column(name = "destacada_can_3")
    private Boolean destacada_can_3;
    
    @Column(name = "titulo_can_4")
    private String titulo_can_4;
    
    @Column(name = "url_can_4")
    private String url_can_4;
    
    @Column(name = "destacada_can_4")
    private Boolean destacada_can_4;
    
    @Column(name = "titulo_can_5")
    private String titulo_can_5;
    
    @Column(name = "url_can_5")
    private String url_can_5;
    
    @Column(name = "destacada_can_5")
    private Boolean destacada_can_5;

    
    
	//Atributos de acciones de recordatorios
	   
    @Column(name = "titulo_rec_1")
    private String titulo_rec_1;
    
    @Column(name = "url_rec_1")
    private String url_rec_1;
    
    @Column(name = "destacada_rec_1")
    private Boolean destacada_rec_1;
    
    @Column(name = "titulo_rec_2")
    private String titulo_rec_2;
    
    @Column(name = "url_rec_2")
    private String url_rec_2;
    
    @Column(name = "destacada_rec_2")
    private Boolean destacada_rec_2;
    
    @Column(name = "titulo_rec_3")
    private String titulo_rec_3;
    
    @Column(name = "url_rec_3")
    private String url_rec_3;
    
    @Column(name = "destacada_rec_3")
    private Boolean destacada_rec_3;
    
    @Column(name = "titulo_rec_4")
    private String titulo_rec_4;
    
    @Column(name = "url_rec_4")
    private String url_rec_4;
    
    @Column(name = "destacada_rec_4")
    private Boolean destacada_rec_4;
    
    @Column(name = "titulo_rec_5")
    private String titulo_rec_5;
    
    @Column(name = "url_rec_5")
    private String url_rec_5;
    
    @Column(name = "destacada_rec_5")
    private Boolean destacada_rec_5;

	
    
    // Constructor basico
 	public AccionMiPerfil () {
 		
 		recurso = null;
 		
 		destacada_con_1 = false;
 		destacada_con_2 = false;
 		destacada_con_3 = false;
 		destacada_con_4 = false;
 		destacada_con_5 = false;
 		destacada_can_1 = false;
 		destacada_can_2 = false;
 		destacada_can_3 = false;
 		destacada_can_4 = false;
 		destacada_can_5 = false;
 		destacada_rec_1 = false;
 		destacada_rec_2 = false;
 		destacada_rec_3 = false;
 		destacada_rec_4 = false;
 		destacada_rec_5 = false;
 		
 		titulo_con_1 = "";
 		titulo_con_2 = "";
 		titulo_con_3 = "";
 		titulo_con_4 = "";
 		titulo_con_5 = "";
 		titulo_can_1 = "";
 		titulo_can_2 = "";
 		titulo_can_3 = "";
 		titulo_can_4 = "";
 		titulo_can_5 = "";
 		titulo_rec_1 = "";
 		titulo_rec_2 = "";
 		titulo_rec_3 = "";
 		titulo_rec_4 = "";
 		titulo_rec_5 = "";
 		
 		url_con_1 = "";
 		url_con_2 = "";
 		url_con_3 = "";
 		url_con_4 = "";
 		url_con_5 = "";
 		url_can_1 = "";
 		url_can_2 = "";
 		url_can_3 = "";
 		url_can_4 = "";
 		url_can_5 = "";
 		url_rec_1 = "";
 		url_rec_2 = "";
 		url_rec_3 = "";
 		url_rec_4 = "";
 		url_rec_5 = "";
 		    
 	}
    
	
    // Constructor por copia 
	public AccionMiPerfil (AccionMiPerfil a) {
		
		id = a.getId();
		recurso = a.getRecurso();
		
		destacada_con_1 = a.getDestacada_con_1();
		destacada_con_2 = a.getDestacada_con_2();
		destacada_con_3 = a.getDestacada_con_3();
		destacada_con_4 = a.getDestacada_con_4();
		destacada_con_5 = a.getDestacada_con_5();
		destacada_can_1 = a.getDestacada_can_1();
		destacada_can_2 = a.getDestacada_can_2();
		destacada_can_3 = a.getDestacada_can_3();
		destacada_can_4 = a.getDestacada_can_4();
		destacada_can_5 = a.getDestacada_can_5();
		destacada_rec_1 = a.getDestacada_rec_1();
		destacada_rec_2 = a.getDestacada_rec_2();
		destacada_rec_3 = a.getDestacada_rec_3();
		destacada_rec_4 = a.getDestacada_rec_4();
		destacada_rec_5 = a.getDestacada_rec_5();
		
		titulo_con_1 = a.getTitulo_con_1();
		titulo_con_2 = a.getTitulo_con_2();
		titulo_con_3 = a.getTitulo_con_3();
		titulo_con_4 = a.getTitulo_con_4();
		titulo_con_5 = a.getTitulo_con_5();
		titulo_can_1 = a.getTitulo_can_1();
		titulo_can_2 = a.getTitulo_can_2();
		titulo_can_3 = a.getTitulo_can_3();
		titulo_can_4 = a.getTitulo_can_4();
		titulo_can_5 = a.getTitulo_can_5();
		titulo_rec_1 = a.getTitulo_rec_1();
		titulo_rec_2 = a.getTitulo_rec_2();
		titulo_rec_3 = a.getTitulo_rec_3();
		titulo_rec_4 = a.getTitulo_rec_4();
		titulo_rec_5 = a.getTitulo_rec_5();
		
		url_con_1 = a.getUrl_con_1();
		url_con_2 = a.getUrl_con_2();
		url_con_3 = a.getUrl_con_3();
		url_con_4 = a.getUrl_con_4();
		url_con_5 = a.getUrl_con_5();
		url_can_1 = a.getUrl_can_1();
		url_can_2 = a.getUrl_can_2();
		url_can_3 = a.getUrl_can_3();
		url_can_4 = a.getUrl_can_4();
		url_can_5 = a.getUrl_can_5();
		url_rec_1 = a.getUrl_rec_1();
		url_rec_2 = a.getUrl_rec_2();
		url_rec_3 = a.getUrl_rec_3();
		url_rec_4 = a.getUrl_rec_4();
		url_rec_5 = a.getUrl_rec_5();
		    
	}

	AccionMiPerfil(Integer id) {
		this.id = id;
	}

//Getters y Setters

	public Integer getId() {
		return id;
	}




	public void setId(Integer id) {
		this.id = id;
	}




	public Recurso getRecurso() {
		return recurso;
	}




	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}




	public String getTitulo_con_1() {
		return titulo_con_1;
	}




	public void setTitulo_con_1(String titulo_con_1) {
		this.titulo_con_1 = titulo_con_1;
	}




	public String getUrl_con_1() {
		return url_con_1;
	}




	public void setUrl_con_1(String url_con_1) {
		this.url_con_1 = url_con_1;
	}




	public Boolean getDestacada_con_1() {
		return destacada_con_1;
	}




	public void setDestacada_con_1(Boolean destacada_con_1) {
		this.destacada_con_1 = destacada_con_1;
	}




	public String getTitulo_con_2() {
		return titulo_con_2;
	}




	public void setTitulo_con_2(String titulo_con_2) {
		this.titulo_con_2 = titulo_con_2;
	}




	public String getUrl_con_2() {
		return url_con_2;
	}




	public void setUrl_con_2(String url_con_2) {
		this.url_con_2 = url_con_2;
	}




	public Boolean getDestacada_con_2() {
		return destacada_con_2;
	}




	public void setDestacada_con_2(Boolean destacada_con_2) {
		this.destacada_con_2 = destacada_con_2;
	}




	public String getTitulo_con_3() {
		return titulo_con_3;
	}




	public void setTitulo_con_3(String titulo_con_3) {
		this.titulo_con_3 = titulo_con_3;
	}




	public String getUrl_con_3() {
		return url_con_3;
	}




	public void setUrl_con_3(String url_con_3) {
		this.url_con_3 = url_con_3;
	}




	public Boolean getDestacada_con_3() {
		return destacada_con_3;
	}




	public void setDestacada_con_3(Boolean destacada_con_3) {
		this.destacada_con_3 = destacada_con_3;
	}




	public String getTitulo_con_4() {
		return titulo_con_4;
	}




	public void setTitulo_con_4(String titulo_con_4) {
		this.titulo_con_4 = titulo_con_4;
	}




	public String getUrl_con_4() {
		return url_con_4;
	}




	public void setUrl_con_4(String url_con_4) {
		this.url_con_4 = url_con_4;
	}




	public Boolean getDestacada_con_4() {
		return destacada_con_4;
	}




	public void setDestacada_con_4(Boolean destacada_con_4) {
		this.destacada_con_4 = destacada_con_4;
	}




	public String getTitulo_con_5() {
		return titulo_con_5;
	}




	public void setTitulo_con_5(String titulo_con_5) {
		this.titulo_con_5 = titulo_con_5;
	}




	public String getUrl_con_5() {
		return url_con_5;
	}




	public void setUrl_con_5(String url_con_5) {
		this.url_con_5 = url_con_5;
	}




	public Boolean getDestacada_con_5() {
		return destacada_con_5;
	}




	public void setDestacada_con_5(Boolean destacada_con_5) {
		this.destacada_con_5 = destacada_con_5;
	}




	public String getTitulo_can_1() {
		return titulo_can_1;
	}




	public void setTitulo_can_1(String titulo_can_1) {
		this.titulo_can_1 = titulo_can_1;
	}




	public String getUrl_can_1() {
		return url_can_1;
	}




	public void setUrl_can_1(String url_can_1) {
		this.url_can_1 = url_can_1;
	}




	public Boolean getDestacada_can_1() {
		return destacada_can_1;
	}




	public void setDestacada_can_1(Boolean destacada_can_1) {
		this.destacada_can_1 = destacada_can_1;
	}




	public String getTitulo_can_2() {
		return titulo_can_2;
	}




	public void setTitulo_can_2(String titulo_can_2) {
		this.titulo_can_2 = titulo_can_2;
	}




	public String getUrl_can_2() {
		return url_can_2;
	}




	public void setUrl_can_2(String url_can_2) {
		this.url_can_2 = url_can_2;
	}




	public Boolean getDestacada_can_2() {
		return destacada_can_2;
	}




	public void setDestacada_can_2(Boolean destacada_can_2) {
		this.destacada_can_2 = destacada_can_2;
	}




	public String getTitulo_can_3() {
		return titulo_can_3;
	}




	public void setTitulo_can_3(String titulo_can_3) {
		this.titulo_can_3 = titulo_can_3;
	}




	public String getUrl_can_3() {
		return url_can_3;
	}




	public void setUrl_can_3(String url_can_3) {
		this.url_can_3 = url_can_3;
	}




	public Boolean getDestacada_can_3() {
		return destacada_can_3;
	}




	public void setDestacada_can_3(Boolean destacada_can_3) {
		this.destacada_can_3 = destacada_can_3;
	}




	public String getTitulo_can_4() {
		return titulo_can_4;
	}




	public void setTitulo_can_4(String titulo_can_4) {
		this.titulo_can_4 = titulo_can_4;
	}




	public String getUrl_can_4() {
		return url_can_4;
	}




	public void setUrl_can_4(String url_can_4) {
		this.url_can_4 = url_can_4;
	}




	public Boolean getDestacada_can_4() {
		return destacada_can_4;
	}




	public void setDestacada_can_4(Boolean destacada_can_4) {
		this.destacada_can_4 = destacada_can_4;
	}




	public String getTitulo_can_5() {
		return titulo_can_5;
	}




	public void setTitulo_can_5(String titulo_can_5) {
		this.titulo_can_5 = titulo_can_5;
	}




	public String getUrl_can_5() {
		return url_can_5;
	}




	public void setUrl_can_5(String url_can_5) {
		this.url_can_5 = url_can_5;
	}




	public Boolean getDestacada_can_5() {
		return destacada_can_5;
	}




	public void setDestacada_can_5(Boolean destacada_can_5) {
		this.destacada_can_5 = destacada_can_5;
	}




	public String getTitulo_rec_1() {
		return titulo_rec_1;
	}




	public void setTitulo_rec_1(String titulo_rec_1) {
		this.titulo_rec_1 = titulo_rec_1;
	}




	public String getUrl_rec_1() {
		return url_rec_1;
	}




	public void setUrl_rec_1(String url_rec_1) {
		this.url_rec_1 = url_rec_1;
	}




	public Boolean getDestacada_rec_1() {
		return destacada_rec_1;
	}




	public void setDestacada_rec_1(Boolean destacada_rec_1) {
		this.destacada_rec_1 = destacada_rec_1;
	}




	public String getTitulo_rec_2() {
		return titulo_rec_2;
	}




	public void setTitulo_rec_2(String titulo_rec_2) {
		this.titulo_rec_2 = titulo_rec_2;
	}




	public String getUrl_rec_2() {
		return url_rec_2;
	}




	public void setUrl_rec_2(String url_rec_2) {
		this.url_rec_2 = url_rec_2;
	}




	public Boolean getDestacada_rec_2() {
		return destacada_rec_2;
	}




	public void setDestacada_rec_2(Boolean destacada_rec_2) {
		this.destacada_rec_2 = destacada_rec_2;
	}




	public String getTitulo_rec_3() {
		return titulo_rec_3;
	}




	public void setTitulo_rec_3(String titulo_rec_3) {
		this.titulo_rec_3 = titulo_rec_3;
	}




	public String getUrl_rec_3() {
		return url_rec_3;
	}




	public void setUrl_rec_3(String url_rec_3) {
		this.url_rec_3 = url_rec_3;
	}




	public Boolean getDestacada_rec_3() {
		return destacada_rec_3;
	}




	public void setDestacada_rec_3(Boolean destacada_rec_3) {
		this.destacada_rec_3 = destacada_rec_3;
	}




	public String getTitulo_rec_4() {
		return titulo_rec_4;
	}




	public void setTitulo_rec_4(String titulo_rec_4) {
		this.titulo_rec_4 = titulo_rec_4;
	}




	public String getUrl_rec_4() {
		return url_rec_4;
	}




	public void setUrl_rec_4(String url_rec_4) {
		this.url_rec_4 = url_rec_4;
	}




	public Boolean getDestacada_rec_4() {
		return destacada_rec_4;
	}




	public void setDestacada_rec_4(Boolean destacada_rec_4) {
		this.destacada_rec_4 = destacada_rec_4;
	}




	public String getTitulo_rec_5() {
		return titulo_rec_5;
	}




	public void setTitulo_rec_5(String titulo_rec_5) {
		this.titulo_rec_5 = titulo_rec_5;
	}




	public String getUrl_rec_5() {
		return url_rec_5;
	}




	public void setUrl_rec_5(String url_rec_5) {
		this.url_rec_5 = url_rec_5;
	}




	public Boolean getDestacada_rec_5() {
		return destacada_rec_5;
	}




	public void setDestacada_rec_5(Boolean destacada_rec_5) {
		this.destacada_rec_5 = destacada_rec_5;
	}




	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AccionMiPerfil that = (AccionMiPerfil) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}


