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
import com.futurewei.everest.functions.ValidValueFilter;
import org.apache.flink.api.common.functions.FilterFunction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * ValidValueFilter Tester.
 *
 * @author Indra G Harijono
 * @since <pre>Nov 09, 2018</pre>
 * @version 1.0
 */
public class ValidValueFilterTest {

    /**
     *
     * Method: filter(KafkaEvent kafkaEvent)
     *
     */
    @Test
    public void testFilterValid() throws Exception {

        // instantiate ValidValueFilter
        FilterFunction validValueFilter = new ValidValueFilter();
        // test positive
        KafkaEvent testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041835L, "description");
        assertEquals(true, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041835L, "description");
        assertEquals(true, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041835L, "description");
        assertEquals(true, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041835L, "description");
        assertEquals(true, validValueFilter.filter(testKafkaEvent));
    }

    @Test
    public void testFilterValidBoundaryValues() throws Exception {

        // instantiate ValidValueFilter
        ValidValueFilter validValueFilter = new ValidValueFilter();
        // test positive
        KafkaEvent testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND, 1541907744041835L, "description");
        assertEquals(true, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND, 1541907744041835L, "description");
        assertEquals(true, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND, 1541907744041835L, "description");
        assertEquals(true, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND, 1541907744041835L, "description");
        assertEquals(true, validValueFilter.filter(testKafkaEvent));
    }

    @Test
    public void testFilterInvalidValue() throws Exception {
        // test invalid value and valid value0
        // instantiate ValidValueFilter
        ValidValueFilter validValueFilter = new ValidValueFilter();
        KafkaEvent testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND-20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND+20, 1541907744041835L, "description");
        assertEquals(false, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND+20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20, 1541907744041835L, "description");
        assertEquals(false, validValueFilter.filter(testKafkaEvent));
    }

    @Test
    public void testFilterInvalidValue0() throws Exception {
        // instantiate ValidValueFilter
        ValidValueFilter validValueFilter = new ValidValueFilter();
        // test valid value and invalid value0
        KafkaEvent testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND+20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND-20, 1541907744041835L, "description");
        assertEquals(false, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND-20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND+20, 1541907744041835L, "description");
        assertEquals(false, validValueFilter.filter(testKafkaEvent));

    }

    @Test
    public void testFilterInvalidValueAndValue0() throws Exception {
        // instantiate ValidValueFilter
        ValidValueFilter validValueFilter = new ValidValueFilter();
        // test invalid value and valid value0
        KafkaEvent testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_LOW_BOUND-20,
                EverestDefaultValues.VALID_VALUE_LOW_BOUND-20, 1541907744041835L, "description");
        assertEquals(false, validValueFilter.filter(testKafkaEvent));
        testKafkaEvent = new KafkaEvent(0, EverestDefaultValues.VALID_VALUE_HIGH_BOUND+20,
                EverestDefaultValues.VALID_VALUE_HIGH_BOUND+20, 1541907744041835L, "description");
        assertEquals(false, validValueFilter.filter(testKafkaEvent));

    }



} 