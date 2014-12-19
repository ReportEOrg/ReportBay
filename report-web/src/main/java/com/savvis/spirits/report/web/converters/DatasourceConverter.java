package com.savvis.spirits.report.web.converters;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;

import com.savvis.spirits.report.model.domain.Datasource;
import com.savvis.spirits.report.model.services.DatasourceHandlerException;
import com.savvis.spirits.report.model.services.ModelService;

@Named
@ApplicationScoped
public class DatasourceConverter implements Converter {
	@Inject
	private ModelService modelService;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1,
			String submittedValue) {
		if (submittedValue == null || submittedValue.isEmpty()) {
			return null;
		}

		List<Datasource> datasources;
		try {
			datasources = modelService.getDatasourceHandler().findAll();
		} catch (DatasourceHandlerException e) {
			throw new ConverterException("Unable to retrieve all datasources. Thus failed to convert " + submittedValue
					+ " into Datasource.", e);
		}
		
		if (CollectionUtils.isEmpty(datasources)) {
			throw new ConverterException(
					"Empty collection returned. Thus failed to convert "
							+ submittedValue + " into Datasource.");
		} else {
			for (Datasource datasource : datasources) {
				if (datasource.getId() == Integer.valueOf(submittedValue)) {
					return datasource;
				}
			}
			throw new ConverterException(new FacesMessage(String.format(
					"%s is not a valid Datasource ID.", submittedValue)));
		}

	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1,
			Object modelValue) {
		if (modelValue == null) {
			return "";
		}

		if (modelValue instanceof Datasource) {
			return String.valueOf(((Datasource) modelValue).getId());
		} else {
			throw new ConverterException(new FacesMessage(String.format(
					"%s is not a valid Datasource.", modelValue)));
		}
	}

}
