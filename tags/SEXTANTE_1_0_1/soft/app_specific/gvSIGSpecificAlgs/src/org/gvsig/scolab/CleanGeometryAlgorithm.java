/*******************************************************************************
 LinkPointsToLinesAlgorithm.java
 Autor: Fco. José Peñarrubia (fjp@scolab.es)
 Copyright (C) SCOLAB Software Colaborativo S.L.
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *******************************************************************************/

package org.gvsig.scolab;

import java.util.ArrayList;

import com.iver.cit.gvsig.util.SnappingCoordinateMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import es.unex.sextante.additionalInfo.AdditionalInfoNumericalValue;
import es.unex.sextante.additionalInfo.AdditionalInfoVectorLayer;
import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.IteratorException;
import es.unex.sextante.exceptions.RepeatedParameterNameException;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 * Algorithm to clean a geometry, using a snap tolerance for its coordinates.
 * I've made some tests wiht JTS Douglas Peucker algorithm and it seems to me that
 * coordinates remain duplicated. So, I make this algorithm.
 * Nota: Solo lo hace con las coordenadas internas de una geometría.
 *  
 */
public class CleanGeometryAlgorithm extends GeoAlgorithm {
	GeometryFactory geomFact = new GeometryFactory();
	private double snapTolerance;

	/* (non-Javadoc)
	 * @see es.unex.sextante.core.GeoAlgorithm#defineCharacteristics()
	 */
	public void defineCharacteristics() {

		setGroup(Sextante.getText("Tools_for_vector_layers")); //$NON-NLS-1$
		this.setName(Sextante.getText("Clean_geometry")); //$NON-NLS-1$
		try {
			m_Parameters.addInputVectorLayer("LAYER", Sextante.getText("original_layer"), //$NON-NLS-1$ //$NON-NLS-2$
					AdditionalInfoVectorLayer.SHAPE_TYPE_ANY, true);
			
			m_Parameters.addNumericalValue("TOLERANCE", Sextante.getText("tolerance_to_search"), 1.0, //$NON-NLS-1$ //$NON-NLS-2$
					AdditionalInfoNumericalValue.NUMERICAL_VALUE_DOUBLE);


			addOutputVectorLayer("RESULT", Sextante.getText("new_layer"), AdditionalInfoVectorLayer.SHAPE_TYPE_ANY); //$NON-NLS-1$
			
		} catch (RepeatedParameterNameException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.unex.sextante.StandardExtension.core.StandardExtensionGeoAlgorithm
	 * #processAlgorithm()
	 */
	public boolean processAlgorithm() {

		int i = 0;

		try {
			IVectorLayer layer = m_Parameters
					.getParameterValueAsVectorLayer("LAYER"); //$NON-NLS-1$
			snapTolerance = m_Parameters.getParameterValueAsDouble("TOLERANCE"); //$NON-NLS-1$

			IVectorLayer result = getNewVectorLayer("RESULT", Sextante.getText("new_layer"), layer.getShapeType(), //$NON-NLS-1$
					layer.getFieldTypes(), layer.getFieldNames());
			
			int iShapeCount = layer.getShapesCount();
			m_Task.setProgressText(Sextante.getText("exporting")); //$NON-NLS-1$
			IFeatureIterator iter = layer.iterator();
			CoordinateFilter filterClean = new CoordinateFilter() {
				
				public void filter(Coordinate c) {
					c.setCoordinate(new Coordinate(c.x, c.y));
				}
			};
			while (iter.hasNext() && setProgress(i, iShapeCount)) {
				try {
					IFeature feat = iter.next();
					Geometry geom = feat.getGeometry();
					Geometry geom2 = processGeometry(geom);
					result.addFeature(geom2, feat.getRecord().getValues());
				}
				catch (IteratorException ex) {
					ex.printStackTrace();
				}
				i++;
					
			}
			
			
		} catch (GeoAlgorithmExecutionException e) {
			e.printStackTrace();
			return false;
		}

		return !m_Task.isCanceled();
	}

	private Geometry processGeometry(Geometry jtsGeom) {
		SnappingCoordinateMap coordMap = new SnappingCoordinateMap(snapTolerance);
		Coordinate[] coords = jtsGeom.getCoordinates();
		ArrayList<Coordinate> newCoords = new ArrayList<Coordinate>();
		Coordinate lastValidCoord = null;
		for (int i=0; i < coords.length; i++) {
			Coordinate c = coords[i];
			if (coordMap.containsKey(c)) {
				// Self intersection?
				c = (Coordinate) coordMap.get(c);
				if (lastValidCoord != c) {
					// Yes, self intersection
					newCoords.add(c);
					lastValidCoord = c;
				}
			}
			else
			{
				if (lastValidCoord == null) {
					coordMap.put(c, c);
					lastValidCoord = c;
					newCoords.add(c);					
				}
				else if (c.distance(lastValidCoord) > snapTolerance) {
					coordMap.put(c, c);
					lastValidCoord = c;
					newCoords.add(c);
				}
			}
		}
		// Si solo hay una coordenada, pongamos la última aunque la línea sea demasiado pequeña.
		if (newCoords.size() == 1)
			newCoords.add(coords[coords.length-1]);
		Geometry newJtsG = jtsGeom.getFactory().createLineString(newCoords.toArray(new Coordinate[0]));
		return newJtsG;
		
	}

}