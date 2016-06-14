function disponibilidadFecha(date)
{
	if(document.getElementById("form:fechasdisp")!=null)
	{
		var date_array = document.getElementById("form:fechasdisp").value;
		var day   = date.getDate();
		var month = date.getMonth()+1;
		var year  = date.getFullYear();
		var value =  day+ "/" +month+"/"+year;
		var split1 = date_array.split("[");
		var split2 = split1[1].split("]");
		var split3 = split2[0].split(",");
		var result = false;
		for (var i = 0; i < split3.length; i++) {
			var element = split3[i];
			if (element.indexOf(value)==1)
			{
				result = true;
				break;
			}
				
		}
	}
	var today = new Date();
	var hoy = today.getDate();
	var mesActual = today.getMonth()+1; //January is 0!
	var anioActual = today.getFullYear();
	
	if ( year < anioActual)
	{
		return [result, "fechaPasada"];
	}else if ( year > anioActual)
	{
		return [result, "fechaFutura"];
	}else
	{
		if(month < mesActual)
		{
			return [result, "fechaPasada"];
		}else if(month > mesActual)
		{
			return [result, "fechaFutura"];
		}else	
		{
			if(day<hoy)
			{
				return [result, "fechaPasada"];
			}else if(day>hoy)
			{
				return [result, "fechaFutura"];
			}else
			{
				return [result, "fechaHoy"];
			} 
		}
	}
	
	
};

$(document).on('focus', '.datepicker',  function() {
	var formatoFecha = document.getElementById('formatoFecha').value;
	 $(this).datepicker({
	        dateFormat: formatoFecha
	    });
});


$(document).on('keydown', '.datepicker',    function() {
    $.datepicker.customKeyPress(event);
});

$.extend($.datepicker, { // Extends datepicker with shortcuts.
    customKeyPress: function (event) {
        var inst = $.datepicker._getInst(event.target);
        var isRTL = inst.dpDiv.is(".ui-datepicker-rtl");
        switch (event.keyCode) {
            case 37:    // LEFT --> -1 day
                $('body').css('overflow','hidden');
            $.datepicker._adjustDate(event.target, (isRTL ? +1 : -1), "D");
            break;
        case 38:    // UPP --> -7 day
            $('body').css('overflow','hidden');
            $.datepicker._adjustDate(event.target, -7, "D");
            break;
        case 39:    // RIGHT --> +1 day
            $('body').css('overflow','hidden');
            $.datepicker._adjustDate(event.target, (isRTL ? -1 : +1), "D");
            break;
        case 40:    // DOWN --> +7 day
            $('body').css('overflow','hidden');
            $.datepicker._adjustDate(event.target, +7, "D");
            break;
        }
        $('body').css('overflow','visible');
    }
});
$.datepicker.regional['es'] = {
		 closeText: 'Cerrar',
		 prevText: 'Anterior',
		 nextText: 'Siguiente',
		 currentText: 'Hoy',
		 monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
		 monthNamesShort: ['Ene','Feb','Mar','Abr', 'May','Jun','Jul','Ago','Sep', 'Oct','Nov','Dic'],
		 dayNames: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
		 dayNamesShort: ['Dom','Lun','Mar','Mié','Juv','Vie','Sáb'],
		 dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sá'],
		 weekHeader: 'Sm',
		 dateFormat: 'dd/mm/yy',
		 firstDay: 1,
		 isRTL: false,
		 showMonthAfterYear: false,
		 yearSuffix: ''
		 };
$.datepicker.setDefaults($.datepicker.regional['es']);
$(function() {
	var formatoFecha = document.getElementById('formatoFecha').value;
    $( "#datepickerinline" ).datepicker({
    	dateFormat: formatoFecha,
    	onSelect: function(date) { 
           document.getElementById("form:diaSelect").value=date;
           document.getElementById("form:callBean").click();
        },
        beforeShowDay: disponibilidadFecha
       
    });
    
    
  });

$(document).on('keydown', '#datepickerinline',    function() {
    $.datepicker.customKeyPress(event);
});