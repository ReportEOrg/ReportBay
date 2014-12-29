package org.reporte.web.component;

import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.primefaces.event.ReorderEvent;

@FacesComponent(value="designModel")
public class DesignModel extends UINamingContainer {
	/**
	 * 
	 * @param event
	 */
	public void ajaxEventListener(ReorderEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
        MethodExpression ajaxEventListener = (MethodExpression) getAttributes().get("onReorderListener");
        ajaxEventListener.invoke(context.getELContext(), new Object[] { event });
	}
}
