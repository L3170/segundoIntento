/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ar.facturacion.bean.util;


import com.ar.facturacion.bean.ProvinciaLocalidadBean;
import com.ar.facturacion.model.Provincia;
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import javax.faces.convert.FacesConverter;


@FacesConverter("myConverter")
public class MyConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//ASI SE USA

//    <p:selectOneMenu id="utiSelector" converter="selectOneMenuConverter" value="#{miBean.miObjeto}">
//    <f:selectItem itemValue="#{null}" noSelectionOption="true" />
//    <f:selectItems value="#{miBean.listaObjetos}" var="objeto" itemValue="#{objeto}" itemLabel="#{objeto.etiqueta}" />
//    </p:selectOneMenu>

  
  
   
}
