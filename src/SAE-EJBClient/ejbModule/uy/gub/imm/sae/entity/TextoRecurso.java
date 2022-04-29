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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ae_textos_recurso")
public class TextoRecurso implements Serializable {

    private static final long serialVersionUID = 6580112377644111328L;

    private Integer id;
    private String textoPaso2;
    private String textoPaso3;
    private String tituloCiudadanoEnLlamador;
    private String tituloPuestoEnLlamador;
    private String ticketEtiquetaUno;
    private String ticketEtiquetaDos;
    private String valorEtiquetaUno;
    private String valorEtiquetaDos;

    private Recurso recurso;

    //No puede haber ningun registro en null porque da "HibernateException: null index column for collection"
    private String idioma;

    public TextoRecurso() {
    }

    TextoRecurso(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_textoRecurso")
    @SequenceGenerator(name = "seq_textoRecurso", initialValue = 1, sequenceName = "s_ae_textorecurso", allocationSize = 1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(length = 1000, name = "texto_paso2")
    public String getTextoPaso2() {
        return textoPaso2;
    }

    public void setTextoPaso2(String textoPaso2) {
        this.textoPaso2 = textoPaso2;
    }

    @Column(length = 1000, name = "texto_paso3")
    public String getTextoPaso3() {
        return textoPaso3;
    }

    public void setTextoPaso3(String textoPaso3) {
        this.textoPaso3 = textoPaso3;
    }

    @Column(name = "titulo_ciudadano_en_llamador")
    public String getTituloCiudadanoEnLlamador() {
        return tituloCiudadanoEnLlamador;
    }

    public void setTituloCiudadanoEnLlamador(String tituloCiudadanoEnLlamador) {
        this.tituloCiudadanoEnLlamador = tituloCiudadanoEnLlamador;
    }

    @Column(name = "titulo_puesto_en_llamador")
    public String getTituloPuestoEnLlamador() {
        return tituloPuestoEnLlamador;
    }

    public void setTituloPuestoEnLlamador(String tituloPuestoEnLlamador) {
        this.tituloPuestoEnLlamador = tituloPuestoEnLlamador;
    }

    @XmlTransient
    @OneToOne(optional = false)
    @JoinColumn(name = "aere_id", nullable = false)
    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    @Column(length = 15, name = "ticket_etiqueta_uno")
    public String getTicketEtiquetaUno() {
        return ticketEtiquetaUno;
    }

    public void setTicketEtiquetaUno(String ticketEtiquetaUno) {
        this.ticketEtiquetaUno = ticketEtiquetaUno;
    }

    @Column(length = 15, name = "ticket_etiqueta_dos")
    public String getTicketEtiquetaDos() {
        return ticketEtiquetaDos;
    }

    public void setTicketEtiquetaDos(String ticketEtiquetaDos) {
        this.ticketEtiquetaDos = ticketEtiquetaDos;
    }

    @Column(length = 30, name = "valor_etiqueta_uno")
    public String getValorEtiquetaUno() {
        return valorEtiquetaUno;
    }

    public void setValorEtiquetaUno(String valorEtiquetaUno) {
        this.valorEtiquetaUno = valorEtiquetaUno;
    }

    @Column(length = 30, name = "valor_etiqueta_dos")
    public String getValorEtiquetaDos() {
        return valorEtiquetaDos;
    }

    public void setValorEtiquetaDos(String valorEtiquetaDos) {
        this.valorEtiquetaDos = valorEtiquetaDos;
    }

    @Column(name = "idioma", nullable = false)
    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextoRecurso that = (TextoRecurso) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
