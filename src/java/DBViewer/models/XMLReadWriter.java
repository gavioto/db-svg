/**
 * DB-SVG
 * XMLReadWriter.java
 * Created on November 13, 2007, 10:43 PM
 *
 * @author  Derrick Bowen
 *
 * This Class Does all of the XML work.  A lot of it is copied very closely from
 *  the tutorial at roseindia.com.  An excellent site.
 *
 */

package DBViewer.models;

 import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 
import java.util.*;
import java.io.*;

public class XMLReadWriter {
    
    /**
     * Creates a new instance of XMLTableWriter
     *
     * There are no classwide variables
     */
    public XMLReadWriter() {
    }
    
    /**
     * The first method run when saving a new document
     */
    public Document DomCreator(String docName) throws Exception{
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        
        return doc;
    }
    
    /**
     * The first method run when opening a document.  the "docBuilder.parse()" 
     *  line was the one that windows was crapping out on when I tried to open 
     *  a file in the My Documents Folder, but was okay with the exact same file
     *  in the C:\ folder.  I'm not sure what caused it.  Steve said maybe there
     *  is some sort of program permissions thing put in place? I thought it 
     *  might be because one of the folders had a period in the name, but then I 
     *  created a new user to get rid of it and it was still having the same
     *  trouble.  If I were going to do more with this program, I'd definitely
     *  have to fix this issue, but it's worked around for now.
     */
    public Document DocOpen(String fileName) throws Exception{
     
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(fileName);
    
        return doc;
    }
    
    /**
     * Does all the work of saving the XML file out.  runs the DomCreator method
     *  to get a Document, then creates a root element and starts giving it children
     *  from out of the List of Lists.  
     *
     * Makes a fileWriter and then sticks some Transformers on it and saves out 
     *  a brand new XML doc.
     *
     * Takes a fileName String, a 2 dimensional list of the data, and an array of the column headings
     */
    public void writeOut(String fileName, List<List<String>> dataSaved, String[] columns) throws Exception{
       
           //set it up
           Document doc = DomCreator(fileName);
           Element rootElement = doc.createElement("root");
           doc.appendChild(rootElement);
           Element rowElement = null;
           Element temp = null;
           List row = null;
           
           //extract the data from the list into the document
           for (int i = 0; i < dataSaved.size(); i++) {
               row = dataSaved.get(i);
               rowElement = doc.createElement("entry");
               rootElement.appendChild(rowElement);
               for (int j = 0; j < columns.length; j++) {
                   temp = doc.createElement(columns[j]);
                   temp.setTextContent((String)row.get(j));
                   rowElement.appendChild(temp);
               }
               Node newLine = doc.createTextNode("\n");
               rootElement.appendChild(newLine);
           }
           
           //file Writer
           FileWriter fout = new FileWriter(fileName);
           PrintWriter pout = new PrintWriter(fout); //could use bufferedWriter, but it's not necessary, used more in networking.
           
           //Transformers (robots in... )
           TransformerFactory tranFactory = TransformerFactory.newInstance();
           Transformer aTransformer = tranFactory.newTransformer();
           
           Source src = new DOMSource(doc);
           Result dest = new StreamResult(pout);
           aTransformer.transform(src, dest);
           
           pout.close();
        
    }
    
    /**
     * The first powerhouse for opening up an XML doc.  The DocOpen method should already
     *  have been run, and the document then just fed in here.
     *
     * This checks to make sure that there are actually children in the right 
     *  places, and then sucks out the node names like oh so many raw oysters.
     *  I used the getAttributes()==null for my boolean because True Elements are the
     *  only Node type that won't give that response.  This automatically skips 
     *  over any Text Nodes, whitespace, new lines, etc.
     *
     * The Node names are returned in a String Array so that the next method can 
     *  suck out the actual data.
     */
    public String[] readInColumnNames(Document doc) throws Exception{
        String[] colNames = null;
        List<String> tempColNames = new ArrayList<String>();
        Node rootNode = null;
        Node listNode = null;
        boolean noVal = true;
        
        if (doc.hasChildNodes()){
            rootNode = doc.getFirstChild();
            if (rootNode.hasChildNodes()){
                listNode = rootNode.getFirstChild();
                if (listNode.getAttributes()==null){
                    while (listNode.getAttributes()==null && rootNode.getNextSibling()!=null){
                        listNode = rootNode.getNextSibling();
                        noVal = false;
                    }
                    if(noVal){
                        System.out.println("1");
                        throw new Exception("no Element Nodes");
                    }
                }
            } else {
                System.out.println("2");
                throw new Exception("no Children in the Root Node");
            }
        }
        for (int i = 0; i < listNode.getChildNodes().getLength(); i++) {
            tempColNames.add(listNode.getChildNodes().item(i).getNodeName());
        }
        colNames = new String[tempColNames.size()];
        for (int i = 0; i < tempColNames.size(); i++) {
            colNames[i]=tempColNames.get(i);
        }
        return colNames;
    }
    
    /**
     * The secondpowerhouse for opening up an XML doc.  The DocOpen and 
     *  readInColumnNames methods should already have been run, and the 
     *  document and String Array fed in here.
     *
     * This checks to make sure that there are actually children in the right 
     *  places, and then sucks out the data names like oh so many pistachios.
     *  I used the getAttributes()==null for my boolean because Elements are the
     *  only Node type that won't give that response.  This automatically skips 
     *  over any Text Nodes.
     *
     * Each piece of info per column is fed into a List that is returned in the end.
     */
    public List<List<String>> readInList(Document doc, String[] column) throws Exception{
        List<List<String>> data = new ArrayList<List<String>>();
        Element rootElement = null;
        
        Node rootNode = null;
        Node itemNode = null;
        if (doc.hasChildNodes()){
            rootNode = doc.getFirstChild();
        } else {
            System.out.println("3");
            throw new Exception("No Nodes here");
        }
        if (rootNode.hasChildNodes()){
            NodeList itemNs = rootNode.getChildNodes();
            
            for (int p = 0; p < itemNs.getLength(); p++) {
                itemNode = itemNs.item(p);
                if (itemNode.getAttributes()!=null) {
                    List<String> tempAttNames = new ArrayList<String>();
                    
                    for (int i = 0; i < itemNode.getChildNodes().getLength(); i++) {
                        tempAttNames.add(itemNode.getChildNodes().item(i).getTextContent());
                    }
                    data.add(tempAttNames);
                }
            }
        } else {
            System.out.println("4");
            throw new Exception("No Content Information Nodes");
        }
        
        return data;
    }
}
