/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package apache.batik.dom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.Document;

import org.xml.sax.XMLReader;

/**
 * This interface represents an object which can build a Document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface DocumentFactory {

    /**
     * Sets whether or not the XML stream has to be validate, depending on the
     * specified parameter.
     *
     * @param isValidating true implies the XML stream will be validated
     */
    void setValidating(boolean isValidating);

    /**
     * Returns true if the XML stream has to be validated, false otherwise.
     */
    boolean isValidating();

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    Document createDocument(String ns, String root, String uri) throws IOException;

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    Document createDocument(String ns, String root, String uri, InputStream is)
        throws IOException;

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param r An XMLReader instance
     * @exception IOException if an error occured while reading the document.
     */
    Document createDocument(String ns, String root, String uri, XMLReader r)
        throws IOException;

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    Document createDocument(String ns, String root, String uri, Reader r)
        throws IOException;

    /**
     * Returns the document descriptor associated with the latest created
     * document.
     * @return null if no document or descriptor was previously generated.
     */
    DocumentDescriptor getDocumentDescriptor();
}
