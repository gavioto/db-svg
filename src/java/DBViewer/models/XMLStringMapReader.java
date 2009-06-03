/*
 * DB-SVG Copyright 2009 Derrick Bowen
 *
 * This file is part of DB-SVG.
 *
 *   DB-SVG is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   DB-SVG is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with DB-SVG.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package DBViewer.models;

import java.io.*;
import org.w3c.dom.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Adapted from: http://www.developerfusion.com/code/2064/a-simple-way-to-read-an-xml-file-in-java/
 *
 * Parses a simple XML file into List of String Maps, based on
 *
 * @author horizon
 */
public class XMLStringMapReader {

    protected String ROW_ELEMENT_NAME = "entry";

/**
 * Default Constructor.  Uses default row node name of "entry"
 */
public XMLStringMapReader() {
}

/**
 * Secondary Constructor.  Set the default row node name to whatever you wish.
 * @param rowElement
 */
public XMLStringMapReader(String rowElement) {
    ROW_ELEMENT_NAME = rowElement;
}


/**
 * Parses the file at the specified path, according the array of entry Value tag Names.
 * The first node in the child elements is returned as a string for the map.
 *
 * @param path
 * @param entryValueNames
 * @return
 */
public List<Map<String,String>> ParseFile(String path, String[] entryValueNames) throws IOException{
    List<Map<String,String>> returner = new ArrayList();
    try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(path));

            // normalize text representation
            doc.getDocumentElement ().normalize ();

            NodeList entryList = doc.getElementsByTagName(ROW_ELEMENT_NAME);

            for(int s=0; s<entryList.getLength() ; s++){
                Map<String,String> entry = new HashMap();

                Node entryNode = entryList.item(s);
                if(entryNode.getNodeType() == Node.ELEMENT_NODE){
                    Element firstPersonElement = (Element)entryNode;

                    for (String col : entryValueNames) {
                        NodeList columnNode = firstPersonElement.getElementsByTagName(col);
                        Element firstElement = (Element)columnNode.item(0);
                        if (firstElement!=null) {
                            NodeList textFNList = firstElement.getChildNodes();
                            entry.put(col.toLowerCase(), ((Node)textFNList.item(0)).getNodeValue().trim());
                        }
                    }
                //only adds the map if It is an element node.
                returner.add(entry);
                }//end of if clause
            }//end of for loop with s var
        }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line "
                 + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }catch (IOException e) {
            //if the path is incorrect, it throws an error and can try again with a new path.
            System.out.println("Incorrect XML file path");
            throw e;
        }catch (Throwable t) {
            t.printStackTrace ();
        }
        return returner;
    }//end of Parse File

/**
 * A main method for testing purposes.
 * @param args
 */
    public static void main (String[] args) throws Exception{
        XMLStringMapReader xsmr = new XMLStringMapReader();
        String[] entyVals = {"title","url","driver","username","password","Title","Url","Driver","Username","Password", "URL"};
        List<Map<String,String>> dbVals = xsmr.ParseFile("/DB-SVG/dbs.xml", entyVals);

        for (Map<String,String> dbentry : dbVals) {
            System.out.println("");
            System.out.println(" ---  Table Entry --- ");
            for (Map.Entry<String,String> dbval : dbentry.entrySet()) {
                System.out.println(dbval.getKey()+" : "+dbval.getValue());
            }
        }
    }
}
