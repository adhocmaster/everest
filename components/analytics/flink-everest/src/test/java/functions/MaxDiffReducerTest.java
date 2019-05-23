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

import com.futurewei.everest.EverestDefaultValues;
import com.futurewei.everest.datatypes.KafkaEvent;
import com.futurewei.everest.functions.MaxDiffReducer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * MaxDiffReducer Tester.
 *
 * @author Indra G Harijono
 * @since <pre>Nov 09, 2018</pre>
 * @version 1.0
 */
public class MaxDiffReducerTest {

    /**
     *
     * Method: reducer(KafkaEvent kafkaEvent)
     *
     */
    @Test
    public void testReducerValid() throws Exception {

        // instantiate MaxDiffReducer
        MaxDiffReducer maxDiffReducer = new MaxDiffReducer();
        // test positive
        KafkaEvent testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+50, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent0 = new KafkaEvent(1, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041839L, "description1");
        KafkaEvent resultedKafkaEvent = maxDiffReducer.reduce(testKafkaEvent, testKafkaEvent0);
        assertEquals(testKafkaEvent.getId(), resultedKafkaEvent.getId());
        assertEquals(testKafkaEvent.getValue(), resultedKafkaEvent.getValue());
        assertEquals(testKafkaEvent.getValue0(), resultedKafkaEvent.getValue0());
        assertEquals(testKafkaEvent.getTimestamp(), resultedKafkaEvent.getTimestamp());
        assertEquals(testKafkaEvent.getDescription(), resultedKafkaEvent.getDescription());

        KafkaEvent testKafkaEvent1 = new KafkaEvent(2, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041835L, "description 1");
        KafkaEvent testKafkaEvent2 = new KafkaEvent(3, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041839L, "description 2");
        resultedKafkaEvent = maxDiffReducer.reduce(testKafkaEvent1, testKafkaEvent2);
        assertEquals(testKafkaEvent1.getId(), resultedKafkaEvent.getId());
        assertEquals(testKafkaEvent1.getValue(), resultedKafkaEvent.getValue());
        assertEquals(testKafkaEvent1.getValue0(), resultedKafkaEvent.getValue0());
        assertEquals(testKafkaEvent1.getTimestamp(), resultedKafkaEvent.getTimestamp());
        assertEquals(testKafkaEvent1.getDescription(), resultedKafkaEvent.getDescription());

    }


    /**
     *
     * Method: reducer(KafkaEvent kafkaEvent)
     *
     */
    @Test
    public void testReducerCompounded() throws Exception {

        // instantiate MaxDiffReducer
        MaxDiffReducer maxDiffReducer = new MaxDiffReducer();
        // test positive
        KafkaEvent testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+50, 1541907744041835L, "description");
        KafkaEvent testKafkaEvent0 = new KafkaEvent(1, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041839L, "description1");
        KafkaEvent resultedKafkaEvent = maxDiffReducer.reduce(testKafkaEvent, testKafkaEvent0);
        assertEquals(testKafkaEvent.getId(), resultedKafkaEvent.getId());
        assertEquals(testKafkaEvent.getValue(), resultedKafkaEvent.getValue());
        assertEquals(testKafkaEvent.getValue0(), resultedKafkaEvent.getValue0());
        assertEquals(testKafkaEvent.getTimestamp(), resultedKafkaEvent.getTimestamp());
        assertEquals(testKafkaEvent.getDescription(), resultedKafkaEvent.getDescription());

        KafkaEvent testKafkaEvent1 = new KafkaEvent(2, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041835L, "description 1");
        KafkaEvent testKafkaEvent2 = new KafkaEvent(3, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041839L, "description 2");
        KafkaEvent resultedKafkaEvent0 = maxDiffReducer.reduce(testKafkaEvent1, testKafkaEvent2);
        assertEquals(testKafkaEvent1.getId(), resultedKafkaEvent0.getId());
        assertEquals(testKafkaEvent1.getValue(), resultedKafkaEvent0.getValue());
        assertEquals(testKafkaEvent1.getValue0(), resultedKafkaEvent0.getValue0());
        assertEquals(testKafkaEvent1.getTimestamp(), resultedKafkaEvent0.getTimestamp());
        assertEquals(testKafkaEvent1.getDescription(), resultedKafkaEvent0.getDescription());

        KafkaEvent resultedKafkaEvent1 = maxDiffReducer.reduce(resultedKafkaEvent, resultedKafkaEvent0);
        assertEquals(resultedKafkaEvent0.getId(), resultedKafkaEvent1.getId());
        assertEquals(resultedKafkaEvent0.getValue(), resultedKafkaEvent1.getValue());
        assertEquals(resultedKafkaEvent0.getValue0(), resultedKafkaEvent1.getValue0());
        assertEquals(resultedKafkaEvent0.getTimestamp(), resultedKafkaEvent1.getTimestamp());
        assertEquals(resultedKafkaEvent0.getDescription(), resultedKafkaEvent1.getDescription());





    }




} 