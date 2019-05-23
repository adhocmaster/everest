/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package functions;

import com.futurewei.everest.datatypes.KafkaEvent;
import com.futurewei.everest.functions.ValueSplitter;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/** 
* ValueSplitter Tester.
* 
* @author Indra G Harijono
* @since <pre>Nov 09, 2018</pre>
* @version 1.0 
*/ 
public class ValueSplitterTest {

    /**
     *
     * Method: select(KafkaEvent) for low -> diff > 20
     *
     */
    @Test
    public void testSelectLow() throws Exception {
        Iterable<String> expectedList = new ArrayList<String>();
        ((ArrayList<String>) expectedList).add("low");

        KafkaEvent testKafkaEvent = new KafkaEvent("ID0", 10.0, 130.0, 1541907744041835L, "low description");

        // instantiate ValueSplitter
        ValueSplitter valueSplitter = new ValueSplitter();

        Iterable<String> actualList = valueSplitter.select(testKafkaEvent);

        assertEquals(expectedList, actualList);
    }

    /**
     *
     * Method: select(KafkaEvent) for high -> diff < 20 and diff > 10
     *
     */
    @Test
    public void testSelectHigh() throws Exception {
        Iterable<String> expectedList = new ArrayList<String>();
        ((ArrayList<String>) expectedList).add("high");

        KafkaEvent testKafkaEvent = new KafkaEvent("ID0", 30.0, 45.0, 1541907744041835L, "high description");

        // instantiate ValueSplitter
        ValueSplitter valueSplitter = new ValueSplitter();

        Iterable<String> actualList = valueSplitter.select(testKafkaEvent);

        assertEquals(expectedList, actualList);
    }

    /**
     *
     * Method: select(KafkaEvent) for high -> diff < 10
     *
     */
    @Test
    public void testSelectCritical() throws Exception {
        Iterable<String> expectedList = new ArrayList<String>();
        ((ArrayList<String>) expectedList).add("critical");

        KafkaEvent testKafkaEvent = new KafkaEvent("ID0", 100.0, 105.0, 1541907744041835L, "critical description");

        // instantiate ValueSplitter
        ValueSplitter valueSplitter = new ValueSplitter();

        Iterable<String> actualList = valueSplitter.select(testKafkaEvent);

        assertEquals(expectedList, actualList);
    }


} 
