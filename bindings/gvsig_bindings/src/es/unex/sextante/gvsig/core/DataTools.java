package es.unex.sextante.gvsig.core;

import java.sql.Types;
import java.util.Date;

import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.DateValue;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.FloatValue;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.LongValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

public class DataTools {

   public static int[] getgvSIGTypes(final Class[] types) {

      final int iTypes[] = new int[types.length];
      for (int i = 0; i < types.length; i++) {
         if (types[i].equals(Integer.class)) {
            iTypes[i] = Types.INTEGER;
         }
         else if (types[i].equals(Double.class)) {
            iTypes[i] = Types.DOUBLE;
         }
         else if (types[i].equals(Long.class)) {
            iTypes[i] = Types.BIGINT;
         }
         else if (types[i].equals(Float.class)) {
            iTypes[i] = Types.FLOAT;
         }
         else if (types[i].equals(String.class)) {
            iTypes[i] = Types.CHAR;
         }
         else if (types[i].equals(Date.class)) {
            iTypes[i] = Types.DATE;
         }
         else if (types[i].equals(Boolean.class)) {
            iTypes[i] = Types.BOOLEAN;
         }
         else if (types[i].equals(Object.class)) {
            iTypes[i] = Types.JAVA_OBJECT;
         }
      }

      return iTypes;

   }


   public static Class getTypeClass(final int dataType) {

      switch (dataType) {
         case Types.DOUBLE:
            return Double.class;
         case Types.FLOAT:
            return Float.class;
         case Types.BIGINT:
            return Long.class;
         case Types.INTEGER:
            return Integer.class;
         case Types.CHAR:
            return String.class;
         case Types.DATE:
            return Date.class;
         case Types.BOOLEAN:
            return Boolean.class;
      }

      return String.class;

   }


   public static Object[] getSextanteValues(final Value[] record) {

      final Object[] values = new Object[record.length];

      for (int i = 0; i < record.length; i++) {
         if (record[i] instanceof IntValue) {
            values[i] = new Integer(((IntValue) record[i]).getValue());
         }
         else if (record[i] instanceof DoubleValue) {
            values[i] = new Double(((DoubleValue) record[i]).getValue());
         }
         else if (record[i] instanceof FloatValue) {
            values[i] = new Float(((FloatValue) record[i]).getValue());
         }
         else if (record[i] instanceof LongValue) {
            values[i] = new Long(((LongValue) record[i]).getValue());
         }
         else if (record[i] instanceof DateValue) {
            values[i] = ((DateValue) record[i]).getValue();
         }
         else if (record[i] instanceof StringValue) {
            values[i] = ((StringValue) record[i]).getValue();
         }
         else if (record[i] instanceof BooleanValue) {
            values[i] = new Boolean(((BooleanValue) record[i]).getValue());
         }
      }

      return values;

   }


   public static Value[] getGVSIGValues(final Object[] record) {

      final Value[] values = new Value[record.length];

      for (int i = 0; i < record.length; i++) {
         if (record[i] instanceof Integer) {
            values[i] = ValueFactory.createValue(((Integer) record[i]).intValue());
         }
         else if (record[i] instanceof Double) {
            values[i] = ValueFactory.createValue(((Double) record[i]).doubleValue());
         }
         else if (record[i] instanceof Float) {
            values[i] = ValueFactory.createValue(((Float) record[i]).longValue());
         }
         else if (record[i] instanceof Long) {
            values[i] = ValueFactory.createValue(((Long) record[i]).longValue());
         }
         else if (record[i] instanceof Date) {
            values[i] = ValueFactory.createValue(((Date) record[i]));
         }
         else if (record[i] instanceof String) {
            values[i] = ValueFactory.createValue(((String) record[i]));
         }
         else if (record[i] instanceof Boolean) {
            values[i] = ValueFactory.createValue(((Boolean) record[i]).booleanValue());
         }
         else if (record[i] == null) {
            values[i] = ValueFactory.createNullValue();
         }
      }

      return values;

   }

}
