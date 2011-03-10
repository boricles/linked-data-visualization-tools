/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Inform�tica, Universidad 
 * Polit�cnica de Madrid, Spain
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
package es.upm.fi.dia.oeg.map4rdf.server.inject;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterNames;

/**
 * @author Alexander De Leon
 */
public class BrowserParametersModule extends AbstractModule {

	private final Configuration config;

	public BrowserParametersModule(Configuration config) {
		this.config = config;
	}

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named(ParameterNames.ENDPOINT_URI)).to(
				config.getConfigurationParamValue(ParameterNames.ENDPOINT_URI));

		bindConstant().annotatedWith(Names.named(ParameterNames.GEOMETRY_MODEL)).to(
				Constants.GeometryModel.valueOf(config.getConfigurationParamValue(ParameterNames.GEOMETRY_MODEL)));

	}
}