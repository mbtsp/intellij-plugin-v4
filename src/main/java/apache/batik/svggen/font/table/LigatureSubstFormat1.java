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
package apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 * @version $Id$
 */
public class LigatureSubstFormat1 extends LigatureSubst {

    private int coverageOffset;
    private int ligSetCount;
    private int[] ligatureSetOffsets;
    private Coverage coverage;
    private LigatureSet[] ligatureSets;

    /** Creates new LigatureSubstFormat1 */
    protected LigatureSubstFormat1(RandomAccessFile raf,int offset) throws IOException {
        coverageOffset = raf.readUnsignedShort();
        ligSetCount = raf.readUnsignedShort();
        ligatureSetOffsets = new int[ligSetCount];
        ligatureSets = new LigatureSet[ligSetCount];
        for (int i = 0; i < ligSetCount; i++) {
            ligatureSetOffsets[i] = raf.readUnsignedShort();
        }
        raf.seek(offset + coverageOffset);
        coverage = Coverage.read(raf);
        for (int i = 0; i < ligSetCount; i++) {
            ligatureSets[i] = new LigatureSet(raf, offset + ligatureSetOffsets[i]);
        }
    }

    public int getFormat() {
        return 1;
    }

}
