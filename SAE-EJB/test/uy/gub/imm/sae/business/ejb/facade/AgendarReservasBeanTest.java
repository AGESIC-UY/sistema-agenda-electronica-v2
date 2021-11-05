package uy.gub.imm.sae.business.ejb.facade;

import org.junit.Test;
import org.mockito.Matchers;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AgendarReservasBeanTest {
    @Test
    public void givenValidRecursoWhenIsRecursoVisibleInternetThenReturnFalse() throws BusinessException, ApplicationException {
        AgendarReservasBean reservasBean = mock(AgendarReservasBean.class);
        Recurso recurso = new Recurso();
        recurso.setVisibleInternet(true);
        when(reservasBean.consultarRecursoPorId(Matchers.<Agenda>anyObject(), anyInt())).thenReturn(recurso);

        boolean result = reservasBean.isRecursoVisibleInternet(recurso);

        assertFalse(result);
    }

    @Test
    public void givenInvalidRecursoWhenIsRecursoVisibleInternetThenReturnFalse() throws BusinessException, ApplicationException {
        AgendarReservasBean reservasBean = mock(AgendarReservasBean.class);
        Recurso recurso = new Recurso();
        recurso.setVisibleInternet(false);
        when(reservasBean.consultarRecursoPorId(Matchers.<Agenda>anyObject(), anyInt())).thenReturn(recurso);

        boolean result = reservasBean.isRecursoVisibleInternet(recurso);

        assertFalse(result);
    }
}