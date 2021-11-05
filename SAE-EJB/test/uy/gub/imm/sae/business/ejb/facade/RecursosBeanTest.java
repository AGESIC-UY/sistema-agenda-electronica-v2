package uy.gub.imm.sae.business.ejb.facade;

import org.junit.Test;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.UserException;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecursosBeanTest {
    @Test
    @SuppressWarnings("unchecked")
    public void givenValidAgendaIdWhenConsultarRecursoByAgendaIdThenReturnRecursos() throws UserException {
        RecursosBean recursosBean = new RecursosBean();
        EntityManager entityManager = mock(EntityManager.class, RETURNS_DEEP_STUBS);
        recursosBean.setEntityManager(entityManager);
        when(entityManager.createQuery(anyString(), any(Class.class)).setParameter(anyString(), anyInt()).getResultList()).thenReturn(Collections.singletonList(new Recurso()));

        List<Recurso> recursos = recursosBean.consultarRecursoByAgendaId(1);

        assertThat(recursos).isNotNull().isNotEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenInvalidAgendaIdWhenConsultarRecursoByAgendaIdThenThrowException() {
        RecursosBean recursosBean = new RecursosBean();
        EntityManager entityManager = mock(EntityManager.class, RETURNS_DEEP_STUBS);
        recursosBean.setEntityManager(entityManager);
        when(entityManager.createQuery(anyString(), any(Class.class)).setParameter(anyString(), anyInt()).getResultList()).thenReturn(null);
        try {
            recursosBean.consultarRecursoByAgendaId(0);
            fail("UserException thrown");
        } catch (UserException ex) {
            assertThat(ex).isInstanceOf(UserException.class);
            assertThat(ex.getCodigoError()).isEqualTo("no_se_encuentra_el_recurso_especificado");
        }
    }
}