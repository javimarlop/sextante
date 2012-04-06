package es.unex.sextante.gvsig.extensions;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import es.unex.sextante.core.Sextante;
import es.unex.sextante.gui.core.NamedPoint;
import es.unex.sextante.gui.core.SextanteGUI;

public class PointSelectorListener
         implements
            PointListener {

   private final Image  img      = new ImageIcon(MapControl.class.getResource("images/PointSelectCursor.gif")).getImage();
   private final Cursor m_Cursor = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(16, 16), "");


   public PointSelectorListener() {

      super();

   }


   public void point(final PointEvent event) throws BehaviorException {

      View view;

      try {
         view = (View) PluginServices.getMDIManager().getActiveWindow();
      }
      catch (final ClassCastException exc) {
         return;
      }

      final ViewPort viewPort = view.getMapControl().getMapContext().getViewPort();
      final Point2D wcPoint = viewPort.toMapPoint(event.getPoint());
      final String sPointName = JOptionPane.showInputDialog(null, "X: " + Double.toString(wcPoint.getX()) + "\n" + "Y: "
                                                                  + Double.toString(wcPoint.getY()),
               Sextante.getText("New point"));
      if (sPointName != null) {
         final NamedPoint namedPoint = new NamedPoint(sPointName, wcPoint);
         SextanteGUI.getGUIFactory().getCoordinatesList().add(namedPoint);
      }

   }


   public Cursor getCursor() {
      return m_Cursor;
   }


   public boolean cancelDrawing() {
      return false;
   }


   public void pointDoubleClick(final PointEvent event) throws BehaviorException {

   }
}
