/*
 */

package DBViewer.objects.model;

import java.io.Serializable;

/**
 *
 * @author Derrick Bowen <derrickbowen@gmail.com>
 */
public interface Column extends Serializable{

   /**
    *
    * @return
    */
   String getAutoIncrement();

   /**
    *
    * @param autoIncrement
    */
   void setAutoIncrement(String autoIncrement);
   /**
    *
    * @return
    */
   String getDataType();

   /**
    *
    * @return
    */
   String getComment();

   /**
    *
    * @return
    */
   String getName();

   /**
    *
    * @return
    */
   int getOrdinalValue();

   /**
    *
    * @return
    */
   String getNullable();

   /**
    *
    * @param dataType
    */
   void setDataType(String dataType);

   /**
    *
    * @param defaultValue
    */
   void setComment(String comment);

   /**
    *
    * @param name
    */
   void setName(String name);

   /**
    *
    * @param notNull
    */
   void setNullable(String nullable);

   /**
    *
    * @param ordinalValue
    */
   void setOrdinalValue(int ordinalValue);

   /**
    *
    * @return
    */
   ForeignKey transformToFK();

   /**
    *
    * @return
    */
   PrimaryKey transformToPK();

   Table getTable();
   
   void setTable(Table table);

}
