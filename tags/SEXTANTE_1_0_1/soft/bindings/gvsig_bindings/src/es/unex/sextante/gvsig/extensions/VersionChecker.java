package es.unex.sextante.gvsig.extensions;

import java.lang.reflect.Method;

import com.iver.cit.gvsig.Version;


public class VersionChecker {

   public static boolean isGvSIGCE() {

      try {
         final Method method = Version.class.getMethod("isGvSIGCE");
         return true;
      }
      catch (final Exception e) {
         return false;
      }


   }

}
