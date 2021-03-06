/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.soteradefense.dga.graphx.config

import scala.collection.mutable

/**
 * A case class that is used to store arguments passed from the command line.
 *
 * @param inputPath Input path to read the data from.
 * @param outputPath Output path to store the results.
 * @param sparkMasterUrl Spark master url.
 * @param sparkAppName The name of the current job.
 * @param sparkJars A list of jars to pass to the spark nodes.
 * @param sparkHome Spark home location.
 * @param edgeDelimiter The delimiter that splits up the edges.
 * @param useKryoSerializer Whether or not to use the kryo serializer.
 * @param systemProperties System properties.
 * @param customArguments Custom Properties to pass the analytics and Spark.
 */
case class Config(
                   inputPath: String = "",
                   outputPath: String = "",
                   sparkMasterUrl: String = "local",
                   sparkAppName: String = "GraphX Analytic",
                   sparkJars: String = "",
                   sparkHome: String = "",
                   edgeDelimiter: String = ",",
                   useKryoSerializer: Boolean = false,
                   systemProperties: Seq[(String, String)] = Seq.empty[(String, String)],
                   customArguments: mutable.HashMap[String, String] = mutable.HashMap.empty[String, String])
