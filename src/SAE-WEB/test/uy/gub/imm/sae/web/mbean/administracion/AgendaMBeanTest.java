package uy.gub.imm.sae.web.mbean.administracion;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.model.StreamedContent;
import uy.gub.imm.sae.business.ejb.facade.AgendaGeneralBean;
import uy.gub.imm.sae.business.ejb.facade.RecursosBean;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FacesContext.class)
public class AgendaMBeanTest {
    @Before
    public void setup() {
        mockStatic(FacesContext.class);
        FacesContext instance = mock(FacesContext.class, RETURNS_DEEP_STUBS);
        when(FacesContext.getCurrentInstance()).thenReturn(instance);
    }

    @Test
    public void whenMoverSeleccionados_thenRecursosMarcadosActualizados() {
        AgendaMBean bean = new AgendaMBean();
        RowList<Recurso> recursos = new RowList<>();
        Recurso recurso = new Recurso();
        recurso.setSeleccionado(true);
        recurso.setId(0);
        Row<Recurso> row = new Row<>(recurso, recursos);
        recursos.add(row);
        bean.setRecursosAgenda(recursos);

        bean.moverSeleccionados(null);

        assertThat(bean.getRecursosMarcados()).isNotNull().hasSize(1).contains(recurso);
    }

    @Test
    public void whenActualizarRecursos_thenModificarRecurso() throws IOException, BusinessException, ApplicationException, UserException {
        AgendaMBean bean = new AgendaMBean();
        RecursoSessionMBean recursoMBean = new RecursoSessionMBean();
        recursoMBean.setDiasInicioVentanaIntranet(10);
        recursoMBean.setDiasVentanaIntranet(10);
        recursoMBean.setDiasInicioVentanaInternet(10);
        recursoMBean.setDiasVentanaInternet(10);
        Recurso recurso = new Recurso();
        recurso.setId(1);
        Set<Recurso> recursosMarcados = new TreeSet<>();
        recursosMarcados.add(recurso);
        RecursosBean recursosEjb = mock(RecursosBean.class);
        bean.setRecursosMarcados(recursosMarcados);
        bean.setRecursoSessionMBean(recursoMBean);
        SessionMBean sessionMBean = mock(SessionMBean.class, RETURNS_DEEP_STUBS);
        when(sessionMBean.getTextos().get(anyString())).thenReturn("");
        bean.setSessionMBean(sessionMBean);
        bean.setGeneralEJB(mock(AgendaGeneralBean.class));
        bean.setRecursosEJB(recursosEjb);

        bean.actualizarRecursos();
        StreamedContent reporte = bean.getReporte();

        verify(recursosEjb, times(1)).modificarRecurso(Matchers.<Recurso>anyObject(), anyString());
        assertThat(reporte).isNull();
    }

    @Test
    public void givenRecursoWithError_whenActualizarRecursos_thenGetReporte() throws IOException, BusinessException, ApplicationException, UserException {
        AgendaMBean bean = new AgendaMBean();
        RecursoSessionMBean recursoMBean = new RecursoSessionMBean();
        recursoMBean.setDiasInicioVentanaIntranet(10);
        recursoMBean.setDiasVentanaIntranet(10);
        recursoMBean.setDiasInicioVentanaInternet(10);
        recursoMBean.setDiasVentanaInternet(10);
        Recurso recurso = new Recurso();
        recurso.setId(1);
        recurso.setNombre("Recurso de prueba");
        Agenda agenda = new Agenda();
        agenda.setId(1);
        recurso.setAgenda(agenda);
        Set<Recurso> recursosMarcados = new TreeSet<>();
        recursosMarcados.add(recurso);
        RecursosBean recursosEjb = mock(RecursosBean.class);
        doThrow(new BusinessException("Error al actualizar")).when(recursosEjb).modificarRecurso(Matchers.<Recurso>anyObject(), anyString());
        bean.setRecursosMarcados(recursosMarcados);
        bean.setRecursoSessionMBean(recursoMBean);
        SessionMBean sessionMBean = mock(SessionMBean.class, RETURNS_DEEP_STUBS);
        when(sessionMBean.getTextos().get(anyString())).thenReturn("");
        bean.setSessionMBean(sessionMBean);
        bean.setGeneralEJB(mock(AgendaGeneralBean.class));
        bean.setRecursosEJB(recursosEjb);

        bean.actualizarRecursos();
        StreamedContent reporte = bean.getReporte();

        verify(recursosEjb, times(1)).modificarRecurso(Matchers.<Recurso>anyObject(), anyString());
        assertThat(reporte).isNotNull();
        assertThat(reporte.getName()).isEqualTo("reporte.csv");
        assertThat(reporte.getContentType()).isEqualTo("text/csv");
        String content = IOUtils.toString(reporte.getStream(), StandardCharsets.UTF_8.toString());
        assertThat(content).contains("1,El recurso 'Recurso de prueba' de la agenda 1 no ha sido actualizado. Mensaje de error:");
    }

    @Test
    public void whenActualizarRecursosWithoutRecursosMarcados_thenGetReportNull() throws IOException, BusinessException, ApplicationException, UserException {
        AgendaMBean bean = new AgendaMBean();
        RecursoSessionMBean recursoMBean = new RecursoSessionMBean();
        recursoMBean.setDiasInicioVentanaIntranet(10);
        recursoMBean.setDiasVentanaIntranet(10);
        recursoMBean.setDiasInicioVentanaInternet(10);
        recursoMBean.setDiasVentanaInternet(10);
        Set<Recurso> recursosMarcados = new TreeSet<>();
        RecursosBean recursosEjb = mock(RecursosBean.class);
        bean.setRecursosMarcados(recursosMarcados);
        bean.setRecursoSessionMBean(recursoMBean);
        SessionMBean sessionMBean = mock(SessionMBean.class, RETURNS_DEEP_STUBS);
        when(sessionMBean.getTextos().get(anyString())).thenReturn("");
        bean.setSessionMBean(sessionMBean);
        bean.setGeneralEJB(mock(AgendaGeneralBean.class));
        bean.setRecursosEJB(recursosEjb);

        bean.actualizarRecursos();
        StreamedContent reporte = bean.getReporte();

        verify(recursosEjb, times(0)).modificarRecurso(Matchers.<Recurso>anyObject(), anyString());
        assertThat(reporte).isNull();
    }

    @Test
    public void whenSeleccionarTodosRecursos_thenSeleccionadoIsChecked() {
        AgendaMBean bean = new AgendaMBean();
        AjaxBehaviorEvent event = mock(AjaxBehaviorEvent.class);
        HtmlSelectBooleanCheckbox checkBox = mock(HtmlSelectBooleanCheckbox.class);
        when(checkBox.getValue()).thenReturn(true);
        when(event.getSource()).thenReturn(checkBox);
        RowList<Recurso> recursos = new RowList<>();
        recursos.addAll(Collections.singletonList(new Row<>(new Recurso(), recursos)));
        bean.setRecursosAgenda(recursos);

        bean.seleccionarTodosRecursos(event);
        RowList<Recurso> recursosAgenda = bean.getRecursosAgenda();

        assertThat(recursosAgenda.get(0).getData().isSeleccionado()).isTrue();
    }

    @Test
    public void whenSeleccionarRecursosDisponibles_thenGetRecursosMarcadosWithData() throws UserException {
        AgendaMBean bean = new AgendaMBean();
        bean.setTodosLosRecursos(true);
        List<SelectItem> agendasDisponibles = new ArrayList<>();
        agendasDisponibles.add(new SelectItem("1", "Agenda 1"));
        bean.setAgendasDisponibles(agendasDisponibles);
        RecursosBean recursosEjb = mock(RecursosBean.class);
        Recurso recurso = new Recurso();
        recurso.setId(1);
        when(recursosEjb.consultarRecursoByAgendaId(anyInt())).thenReturn(Collections.singletonList(recurso));
        bean.setRecursosEJB(recursosEjb);

        bean.seleccionarRecursosDisponibles();
        Set<Recurso> recursosMarcados = bean.getRecursosMarcados();

        verify(recursosEjb, times(1)).consultarRecursoByAgendaId(1);
        assertThat(recursosMarcados).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    public void givenValidAgendaId_whenCambioSeleccionAgenda_thenUpdateRecursosAgenda() throws UserException {
        AgendaMBean bean = new AgendaMBean();
        bean.setAgendaActualId("1");
        List<SelectItem> agendasDisponibles = new ArrayList<>();
        agendasDisponibles.add(new SelectItem("1", "Agenda 1"));
        bean.setAgendasDisponibles(agendasDisponibles);
        RecursosBean recursosEjb = mock(RecursosBean.class);
        Recurso recurso = new Recurso();
        recurso.setId(1);
        when(recursosEjb.consultarRecursoByAgendaId(anyInt())).thenReturn(Collections.singletonList(recurso));
        bean.setRecursosEJB(recursosEjb);

        bean.cambioSeleccionAgenda(null);
        RowList<Recurso> recursosAgenda = bean.getRecursosAgenda();

        verify(recursosEjb, times(1)).consultarRecursoByAgendaId(1);
        assertThat(recursosAgenda).isNotNull().isNotEmpty().hasSize(1);
        assertThat(bean.getSeleccionarTodos()).isFalse();
    }

    @Test
    public void givenInvalidAgendaId_whenCambioSeleccionAgenda_thenGetEmptyRecursosAgenda() throws UserException {
        AgendaMBean bean = new AgendaMBean();
        bean.setAgendaActualId("X");
        RecursosBean recursosEjb = mock(RecursosBean.class);
        bean.setRecursosEJB(recursosEjb);

        bean.cambioSeleccionAgenda(null);
        RowList<Recurso> recursosAgenda = bean.getRecursosAgenda();

        verify(recursosEjb, times(0)).consultarRecursoByAgendaId(1);
        assertThat(recursosAgenda).isNotNull().isEmpty();
        assertThat(bean.getSeleccionarTodos()).isFalse();
    }
}