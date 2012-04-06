

package es.unex.sextante.arcgis.gptool;

import com.esri.arcgis.geodatabase.IGPMessages;
import com.esri.arcgis.system.ITrackCancel;

import es.unex.sextante.core.ITaskMonitor;


public class ArcTaskController
         implements
            ITaskMonitor {

   private final ITrackCancel m_TrackCancel;
   private final IGPMessages  m_Msg;


   public ArcTaskController(final ITrackCancel trackCancel,
                            final IGPMessages msg) {

      m_TrackCancel = trackCancel;
      m_Msg = msg;

   }


   @Override
   public void close() {
      // TODO Auto-generated method stub

   }


   @Override
   public boolean isCanceled() {

      try {
         return !m_TrackCancel.esri_continue();
      }
      catch (final Exception e) {
         return true;
      }

   }


   @Override
   public void setDescriptionPrefix(final String prefix) {
   }


   @Override
   public void setDeterminate(final boolean determinate) {
   }


   @Override
   public void setProcessDescription(final String description) {

   }


   @Override
   public void setProgress(final int step) {

      setProgress(step, 100);

   }


   @Override
   public void setProgress(final int step,
                           final int totalNumberOfSteps) {

      //System.out.println(step + "/" + totalNumberOfSteps);

   }


   @Override
   public void setProgressText(final String sText) {

      try {
         m_Msg.addMessage(sText);
      }
      catch (final Exception e) {
         // just ignore the message
      }

   }

}
