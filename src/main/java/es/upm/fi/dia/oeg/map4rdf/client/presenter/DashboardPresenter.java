/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Inform‡tica, Universidad 
 * PolitŽcnica de Madrid, Spain
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
package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import java.util.Collections;
import java.util.Set;

import name.alexdeleon.lib.gwtblocks.client.PagePresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResources;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetStatisticDatasets;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceCloseEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceCloseEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.LoadResourceEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.LoadResourceEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.GeoUtils;
import es.upm.fi.dia.oeg.map4rdf.client.widget.DataToolBar;
import es.upm.fi.dia.oeg.map4rdf.client.widget.EditResourceWidget;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;

/**
 * @author Alexander De Leon
 */
@Singleton
public class DashboardPresenter extends PagePresenter<DashboardPresenter.Display> implements
        FacetConstraintsChangedHandler, LoadResourceEventHandler, AreaFilterChangedHandler, EditResourceEventHandler, EditResourceCloseEventHandler {

    public interface Display extends WidgetDisplay {
        HasWidgets getMapPanel();
        void addWestWidget(Widget widget, String header);
        void clear();
        void setMainPopup(Integer width, Integer height, Widget widget);
        void closeMainPopup();
    }
    private final ResultsPresenter resultsPresenter;
    private final MapPresenter mapPresenter;
    private final FacetPresenter facetPresenter;
    private final FiltersPresenter filtersPresenter;
    private final DispatchAsync dispatchAsync;
    private final DataToolBar dataToolBar;
    private final BrowserMessages messages;
    private final BrowserResources resources;
    
    
    @Inject
    public DashboardPresenter(Display display, EventBus eventBus, FacetPresenter facetPresenter,
            MapPresenter mapPresenter, FiltersPresenter filtersPresenter, ResultsPresenter resultsPresenter, DispatchAsync dispatchAsync,
            DataToolBar dataToolBar, BrowserMessages messages, BrowserResources resources) {
        super(display, eventBus);
        this.messages = messages;
        this.resources = resources;
        this.mapPresenter = mapPresenter;
        this.facetPresenter = facetPresenter;
        this.resultsPresenter = resultsPresenter;
        this.dispatchAsync = dispatchAsync;
        this.dataToolBar = dataToolBar;
        this.filtersPresenter = filtersPresenter;
        
        addControl(mapPresenter);
        addControl(facetPresenter);

        // registered for app-level events
        eventBus.addHandler(FacetConstraintsChangedEvent.getType(), this);
        eventBus.addHandler(LoadResourceEvent.getType(), this);
        eventBus.addHandler(AreaFilterChangedEvent.getType(), this);
        eventBus.addHandler(EditResourceEvent.getType(), this);
        eventBus.addHandler(EditResourceCloseEvent.getType(), this);
    }

    @Override
    public void onFacetConstraintsChanged(FacetConstraintsChangedEvent event) {
        // TODO: add constraints to the Place
        mapPresenter.clear();
        resultsPresenter.clear();
        loadResources(mapPresenter.getVisibleBox(), event.getConstraints());
    }

    @Override
    public void onLoadResource(LoadResourceEvent event) {
        mapPresenter.clear();
        resultsPresenter.clear();
        loadResource(event.getResourceUri());
    }

    /* -------------- Presenter callbacks -- */
    @Override
    public Place getPlace() {
        return Places.DASHBOARD;
    }

    @Override
    protected void onBind() {
        // attach children
        
    	GetStatisticDatasets action = new GetStatisticDatasets();
    	dispatchAsync.execute(action,new AsyncCallback<ListResult<Resource>>() {

			@Override
			public void onFailure(Throwable caught) {
				getDisplay().addWestWidget(facetPresenter.getDisplay().asWidget(), "Facets");
		        getDisplay().addWestWidget(filtersPresenter.getDisplay().asWidget(), messages.filtres());
		        getDisplay().addWestWidget(resultsPresenter.getDisplay().asWidget(), messages.results());
		        getDisplay().getMapPanel().add(mapPresenter.getDisplay().asWidget());				
			}

			@Override
			public void onSuccess(ListResult<Resource> result) {
				getDisplay().addWestWidget(facetPresenter.getDisplay().asWidget(), "Facets");
				if(result != null && result.asList().size()>0) {
					getDisplay().addWestWidget(dataToolBar, messages.overlays());					
				}
		        getDisplay().addWestWidget(filtersPresenter.getDisplay().asWidget(), messages.filtres());
		        getDisplay().addWestWidget(resultsPresenter.getDisplay().asWidget(), messages.results());
		        getDisplay().getMapPanel().add(mapPresenter.getDisplay().asWidget());

			}
		});
    }
    
    @Override
    protected void onPlaceRequest(PlaceRequest request) {
    }

    @Override
    protected void onUnbind() {
        // empty
    }

    @Override
    protected void onRefreshDisplay() {
    }

    @Override
    protected void onRevealDisplay() {
        //mapPresenter.clear();
        //resultsPresenter.clear();
        //facetPresenter.clear();
        //filtersPresenter.clear();
        //mapPresenter.clearDrawing();
        //loadResources(mapPresenter.getVisibleBox(), null);
        //getDisplay().clear();
    }

    /* --------------- helper methods --- */
    void loadResources(BoundingBox boundingBox, Set<FacetConstraint> constraints) {
        GetGeoResources action = new GetGeoResources(boundingBox);
        if (constraints != null) {
            action.setFacetConstraints(constraints);
        }
        mapPresenter.getDisplay().startProcessing();
        dispatchAsync.execute(action, new AsyncCallback<ListResult<GeoResource>>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                mapPresenter.getDisplay().stopProcessing();
            }

            @Override
            public void onSuccess(ListResult<GeoResource> result) {
                mapPresenter.drawGeoResouces(result.asList());
                resultsPresenter.setResults(result.asList());
                mapPresenter.getDisplay().stopProcessing();
            }
        });
    }

    void loadResource(String uri) {
        GetGeoResource action = new GetGeoResource(uri);
        mapPresenter.getDisplay().startProcessing();
        dispatchAsync.execute(action, new AsyncCallback<SingletonResult<GeoResource>>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                mapPresenter.getDisplay().stopProcessing();
            }

            @Override
            public void onSuccess(SingletonResult<GeoResource> result) {
                mapPresenter.drawGeoResouces(Collections.singletonList(result.getValue()));
                mapPresenter.setVisibleBox(GeoUtils.computeBoundingBoxFromGeometries(result.getValue().getGeometries()));
                mapPresenter.getDisplay().stopProcessing();
            }
        });
    }

	@Override
	public void onAreaFilterChanged(
			AreaFilterChangedEvent areaFilterChangedEvent) {
			FacetConstraintsChangedEvent event = new FacetConstraintsChangedEvent(facetPresenter.getConstraints());
			eventBus.fireEvent(event);
			
	}

	@Override
	public void onEditResource(EditResourceEvent editResourceEvent) {
		int height = mapPresenter.getDisplay().asWidget().getOffsetHeight();
		int width = mapPresenter.getDisplay().asWidget().getOffsetWidth();
		es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter.Display d = mapPresenter.getDisplay();
		getDisplay().setMainPopup(width, height, new EditResourceWidget(editResourceEvent.getUrl(),dispatchAsync,d,resources,messages,eventBus));
	}

	@Override
	public void onEditResourceClose(
			EditResourceCloseEvent editResourceCloseEvent) {
		getDisplay().closeMainPopup();
	}
}