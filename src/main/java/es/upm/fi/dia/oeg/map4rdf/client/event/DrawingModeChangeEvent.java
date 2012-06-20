/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.client.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

import es.upm.fi.dia.oeg.map4rdf.client.drawing.DrawingType;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;

/**
 * @author Filip
 */
public class DrawingModeChangeEvent extends GwtEvent<DrawingModeChangeHandler> {

	private static GwtEvent.Type<DrawingModeChangeHandler> TYPE;

	private DrawingType type;

	public DrawingModeChangeEvent(DrawingType type) {
		super();
		this.setDrawingType(type);	
	}

	public static GwtEvent.Type<DrawingModeChangeHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<DrawingModeChangeHandler>();
		}
		return TYPE;
	}


	@Override
	protected void dispatch(DrawingModeChangeHandler handler) {
		handler.onDrawingTypeChange(this);
	}

	@Override
	public GwtEvent.Type<DrawingModeChangeHandler> getAssociatedType() {
		return getType();
	}

	public DrawingType getDrawingMode() {
		return type;
	}

	public void setDrawingType(DrawingType type) {
		this.type=type;;
	}

}
