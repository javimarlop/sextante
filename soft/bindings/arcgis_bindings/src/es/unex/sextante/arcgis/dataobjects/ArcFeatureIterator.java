

package es.unex.sextante.arcgis.dataobjects;

import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IFeatureSelection;
import com.esri.arcgis.geodatabase.IEnumIDs;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geodatabase.ISelectionSet;

import es.unex.sextante.arcgis.gptool.SextanteGlobalParams;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.exceptions.IteratorException;


public class ArcFeatureIterator
         implements
            IFeatureIterator {


   private IFeatureCursor m_FeatureCursor;
   private int            m_iTotal;
   private int            m_iCurrent;
   private IEnumIDs       m_SelectedIDs;
   private IFeatureClass  m_FeatureClass;


   public ArcFeatureIterator(final FeatureLayer layer) {

      m_iCurrent = 0;
      try {
         m_FeatureClass = layer.getFeatureClass();
         if (((Boolean) SextanteGlobalParams.getParameter(SextanteGlobalParams.USE_ONLY_SELECTED)).booleanValue()) {
            final ISelectionSet selection = ((IFeatureSelection) layer).getSelectionSet();
            if (selection.getCount() != 0) {
               m_SelectedIDs = selection.getIDs();
               m_iTotal = selection.getCount();
            }
            else {
               m_FeatureCursor = layer.search(null, true);
               m_iTotal = layer.getFeatureClass().featureCount(null);
            }
         }
         else {
            m_FeatureCursor = layer.search(null, true);
            m_iTotal = layer.getFeatureClass().featureCount(null);
         }

      }
      catch (final Exception e) {
         m_iTotal = 0;
      }

   }


   public ArcFeatureIterator() {
      //creates a dummy iterator
      m_iTotal = 0;
   }


   @Override
   public void close() {
   }


   @Override
   public boolean hasNext() {

      return m_iCurrent < m_iTotal;

   }


   @Override
   public IFeature next() throws IteratorException {

      try {
         m_iCurrent++;
         if (m_SelectedIDs == null) {
            return new ArcFeature(m_FeatureCursor.nextFeature());
         }
         else {
            return new ArcFeature(m_FeatureClass.getFeature(m_SelectedIDs.next()));
         }
      }
      catch (final Exception e) {
         return null;
         //TODO improve this
      }

   }

}
