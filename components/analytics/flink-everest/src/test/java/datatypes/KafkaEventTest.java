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

package datatypes;

import com.futurewei.everest.datatypes.KafkaEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** 
* KafkaEvent Tester. 
* 
* @author Indra G Harijono
* @since <pre>11/08/2018</pre>
* @version 1.0 
*/ 
public class KafkaEventTest {

    private KafkaEvent<String, Double, Double> testKafkaEvent;
    private String id = "ID0";
    private Double value = 10.0;
    private Double value0 = 90.01;
    private long timestamp = 1541907744042130L;
    private String description = "test description";

    /**
    *
    * Method: getId()
    *
    */
    @Test
    public void testGetId() {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        assertEquals(id, testKafkaEvent.getId());
    }

    /**
    *
    * Method: setId(int id)
    *
    */
    @Test
    public void testSetId() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        testKafkaEvent.setId("ID2");
        assertEquals("ID2", testKafkaEvent.getId());
    }

    /**
     *
     * Method: getValue()
     *
     */
    @Test
    public void testGetValue() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        assertEquals(value, testKafkaEvent.getValue());
    }

    /**
     *
     * Method: setValue(String path)
     *
     */
    @Test
    public void testsetValue() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        testKafkaEvent.setValue(new Double(10.0));
        assertEquals(new Double(10.0), testKafkaEvent.getValue());
    }

    /**
     *
     * Method: getValue()
     *
     */
    @Test
    public void testGetValue0() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        assertEquals(value0, testKafkaEvent.getValue0());
    }

    /**
     *
     * Method: setValue(String path)
     *
     */
    @Test
    public void testsetValue0() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        testKafkaEvent.setValue0(new Double(10.0));
        assertEquals(new Double(10.0), testKafkaEvent.getValue0());
    }

    /**
    *
    * Method: getTimestamp()
    *
    */
    @Test
    public void testGetTimestamp() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        assertEquals(timestamp, testKafkaEvent.getTimestamp());
    }

    /**
    *
    * Method: setTimestamp(long timestamp)
    *
    */
    @Test
    public void testSetTimestamp() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        testKafkaEvent.setTimestamp(1541907744042135L);
        assertEquals(1541907744042135L, testKafkaEvent.getTimestamp());
    }

    /**
    *
    * Method: getDescription()
    *
    */
    @Test
    public void testGetDescription() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        assertEquals(description, testKafkaEvent.getDescription());
    }

    /**
    *
    * Method: setDescription(String description)
    *
    */
    @Test
    public void testSetDescription() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        testKafkaEvent.setDescription("new_description");
        assertEquals("new_description", testKafkaEvent.getDescription());

    }

    /**
    *
    * Method: fromString(String eventStr)
    *
    */
    @Test
    public void testFromString() throws Exception {
        testKafkaEvent = KafkaEvent.fromString(id + "," + value + "," + value0 + "," + String.valueOf(timestamp) + "," + description);
        assertEquals(id, testKafkaEvent.getId());
        assertEquals(value, testKafkaEvent.getValue());
        assertEquals(value0, testKafkaEvent.getValue0());
        assertEquals(timestamp, testKafkaEvent.getTimestamp());
        assertEquals(description, testKafkaEvent.getDescription());
    }

    /**
    *
    * Method: toString()
    *
    */
    @Test
    public void testToString() throws Exception {
        testKafkaEvent = new KafkaEvent(id, value, value0, timestamp, description);
        assertEquals(id + "," + value + "," + value0 + "," + timestamp + "," + "," + description,
                testKafkaEvent.toString());
    }

} 
