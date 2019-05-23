/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package sources;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* CassandraSource Tester. 
* 
* @author Indra G. Harijono
* @since <pre>Nov 22, 2018</pre> 
* @version 1.0 
*/ 
public class CassandraSourceTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: run(SourceContext<KafkaEvent<String, Double, Double>> ctx) 
* 
*/ 
@Test
public void testRun() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: cancel() 
* 
*/ 
@Test
public void testCancel() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: collect(T t) 
* 
*/ 
@Test
public void testCollect() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: collectWithTimestamp(T t, long l) 
* 
*/ 
@Test
public void testCollectWithTimestamp() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: emitWatermark(org.apache.flink.streaming.api.watermark.Watermark watermark) 
* 
*/ 
@Test
public void testEmitWatermark() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: markAsTemporarilyIdle() 
* 
*/ 
@Test
public void testMarkAsTemporarilyIdle() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getCheckpointLock() 
* 
*/ 
@Test
public void testGetCheckpointLock() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: close() 
* 
*/ 
@Test
public void testClose() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: connect() 
* 
*/ 
@Test
public void testConnect() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CassandraSource.getClass().getMethod("connect"); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: retrieve() 
* 
*/ 
@Test
public void testRetrieve() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CassandraSource.getClass().getMethod("retrieve"); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
