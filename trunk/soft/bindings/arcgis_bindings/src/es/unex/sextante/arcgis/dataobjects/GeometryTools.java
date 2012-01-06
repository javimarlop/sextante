

package es.unex.sextante.arcgis.dataobjects;

import java.util.ArrayList;

import com.esri.arcgis.geometry.GeometryBag;
import com.esri.arcgis.geometry.GeometryEnvironment;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IGeometryBridge2;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Line;
import com.esri.arcgis.geometry.Multipoint;
import com.esri.arcgis.geometry.Path;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.geometry.Polyline;
import com.esri.arcgis.geometry.Ring;
import com.esri.arcgis.system._WKSPoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

import es.unex.sextante.core.Sextante;


public class GeometryTools {

   public static Geometry toJTS(final IGeometry shape) {

      try {
         if (shape instanceof Point) {
            final GeometryFactory gf = new GeometryFactory();
            final Point pt = (Point) shape;
            final Coordinate coord = new Coordinate(pt.getX(), pt.getY());
            final com.vividsolutions.jts.geom.Point point = gf.createPoint(coord);
            return point;
         }
         else if (shape instanceof Multipoint) {
            final Multipoint pts = (Multipoint) shape;
            final GeometryFactory gf = new GeometryFactory();
            final Coordinate[] coords = new Coordinate[pts.getPointCount()];
            for (int i = 0; i < coords.length; i++) {
               final Point pt = (Point) pts.getGeometry(i);
               coords[i] = new Coordinate(pt.getX(), pt.getY());
            }
            return gf.createMultiPoint(coords);
         }
         else if (shape instanceof Polyline) {
            final Polyline polyline = (Polyline) shape;
            final GeometryFactory gf = new GeometryFactory();
            final ArrayList<LineString> list = new ArrayList<LineString>();
            for (int i = 0; i < polyline.getGeometryCount(); i++) {
               final Polyline line = (Polyline) polyline.getGeometry(i);
               final Coordinate[] coords = new Coordinate[line.getPointCount()];
               for (int j = 0; j < coords.length; j++) {
                  final IPoint pt = line.getPoint(j);
                  coords[j] = new Coordinate(pt.getX(), pt.getY());
               }
               list.add(gf.createLineString(coords));
            }
            return gf.createMultiLineString(list.toArray(new LineString[0]));
         }
         else if (shape instanceof Polygon) {
            final Polygon polygon = (Polygon) shape;
            final GeometryFactory gf = new GeometryFactory();
            final ArrayList<com.vividsolutions.jts.geom.Polygon> list = new ArrayList<com.vividsolutions.jts.geom.Polygon>();
            for (int i = 0; i < polygon.getExteriorRingCount(); i++) {
               final GeometryBag bag = (GeometryBag) polygon.getExteriorRingBag();
               final Ring exteriorRing = (Ring) bag.getGeometry(i);
               final Coordinate[] exteriorCoords = new Coordinate[exteriorRing.getPointCount()];
               for (int k = 0; k < exteriorCoords.length; k++) {
                  final IPoint pt = exteriorRing.getPoint(k);
                  exteriorCoords[k] = new Coordinate(pt.getX(), pt.getY());
               }
               final LinearRing externalLinearRing = gf.createLinearRing(exteriorCoords);
               final ArrayList<LinearRing> listInternal = new ArrayList<LinearRing>();
               final GeometryBag interiorBag = (GeometryBag) polygon.getInteriorRingBag(exteriorRing);
               for (int j = 0; j < interiorBag.getGeometryCount(); j++) {
                  final Ring interiorRing = (Ring) interiorBag.getGeometry(j);
                  final Coordinate[] coords = new Coordinate[interiorRing.getPointCount()];
                  for (int k = 0; k < coords.length; k++) {
                     final IPoint pt = interiorRing.getPoint(k);
                     coords[k] = new Coordinate(pt.getX(), pt.getY());
                     listInternal.add(gf.createLinearRing(coords));
                  }
               }
               list.add(gf.createPolygon(externalLinearRing, listInternal.toArray(new LinearRing[0])));
            }
            return gf.createMultiPolygon(list.toArray(new com.vividsolutions.jts.geom.Polygon[0]));
         }
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         return null;
      }
      return null;

   }


   public static IGeometry toArc(final Geometry shape) {

      try {
         if ((shape instanceof com.vividsolutions.jts.geom.Point) || (shape instanceof com.vividsolutions.jts.geom.MultiPoint)) {
            final IGeometryBridge2 geometryBridge = new GeometryEnvironment();
            final Multipoint pointCollection = new Multipoint();
            final Coordinate[] coords = shape.getCoordinates();
            final _WKSPoint[] points = new _WKSPoint[coords.length];
            for (int i = 0; i < points.length; i++) {
               points[i] = new _WKSPoint();
               points[i].x = coords[i].x;
               points[i].y = coords[i].y;
            }
            geometryBridge.setWKSPoints(pointCollection, points);
            return pointCollection;
         }
         else if ((shape instanceof com.vividsolutions.jts.geom.MultiLineString)
                  || (shape instanceof com.vividsolutions.jts.geom.LineString)) {
            final Polyline result = new Polyline();
            for (int i = 0; i < shape.getNumGeometries(); i++) {
               final Geometry geom = shape.getGeometryN(i);
               final Path path = new Path();
               final Coordinate[] coords = geom.getCoordinates();
               for (int j = 0; j < coords.length - 1; j++) {
                  final Line line = new Line();
                  final Point pt = new Point();
                  pt.setX(coords[j].x);
                  pt.setY(coords[j].y);
                  line.setFromPoint(pt);
                  final Point pt2 = new Point();
                  pt2.setX(coords[j + 1].x);
                  pt2.setY(coords[j + 1].y);
                  line.setToPoint(pt2);
                  path.addSegment(line, null, null);
               }
               result.addGeometry(path, null, null);
            }
            return result;
         }
         else if ((shape instanceof com.vividsolutions.jts.geom.MultiPolygon)
                  || (shape instanceof com.vividsolutions.jts.geom.Polygon)) {
            final Polygon result = new Polygon();
            for (int i = 0; i < shape.getNumGeometries(); i++) {
               final com.vividsolutions.jts.geom.Polygon geom = (com.vividsolutions.jts.geom.Polygon) shape.getGeometryN(i);
               final Ring ring = new Ring();
               final IGeometryBridge2 geometryBridge = new GeometryEnvironment();
               final Coordinate[] coords = geom.getExteriorRing().getCoordinates();
               final _WKSPoint[] points = new _WKSPoint[coords.length];
               for (int j = 0; j < points.length; j++) {
                  points[j] = new _WKSPoint();
                  points[j].x = coords[j].x;
                  points[j].y = coords[j].y;
               }
               geometryBridge.setWKSPoints(ring, points);
               result.addGeometry(ring, null, null);
               for (int j = 0; j < geom.getNumInteriorRing(); j++) {
                  final Ring ring2 = new Ring();
                  final Coordinate[] coords2 = geom.getInteriorRingN(j).getCoordinates();
                  final _WKSPoint[] points2 = new _WKSPoint[coords.length];
                  for (int k = 0; k < points2.length; k++) {
                     points2[k] = new _WKSPoint();
                     points2[k].x = coords2[k].x;
                     points2[k].y = coords2[k].y;
                  }
                  geometryBridge.setWKSPoints(ring2, points);
                  result.addGeometry(ring2, null, null);
               }
            }
            return result;
         }
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         return null;
      }
      return null;

   }
}
