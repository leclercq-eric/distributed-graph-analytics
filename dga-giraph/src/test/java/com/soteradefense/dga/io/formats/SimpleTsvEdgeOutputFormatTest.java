/*
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
 */
package com.soteradefense.dga.io.formats;

import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.conf.ImmutableClassesGiraphConfiguration;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SimpleTsvEdgeOutputFormatTest extends SimpleTsvEdgeOutputFormat {

    private ImmutableClassesGiraphConfiguration conf;
    private TaskAttemptContext tac;

    private Vertex<Text, VIntWritable, VIntWritable> vertex;
    Edge<Text, VIntWritable> edge1;
    Edge<Text, VIntWritable> edge2;

    private RecordWriter<Text, Text> rw;

    @Before
    public void setUp() throws Exception {
        GiraphConfiguration giraphConfiguration = new GiraphConfiguration();
        conf = new ImmutableClassesGiraphConfiguration<Text, Text, VIntWritable>(giraphConfiguration);
        tac = mock(TaskAttemptContext.class);
        when(tac.getConfiguration()).thenReturn(conf);

        vertex = mock(Vertex.class);
        when(vertex.getId()).thenReturn(new Text("34"));
        when(vertex.getValue()).thenReturn(new VIntWritable(10));

        Iterable<Edge<Text, VIntWritable>> iterable = mock(Iterable.class);
        Iterator<Edge<Text, VIntWritable>> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);

        edge1 = mock(Edge.class);
        when(edge1.getTargetVertexId()).thenReturn(new Text("12"));
        when(edge1.getValue()).thenReturn(new VIntWritable(1));

        edge2 = mock(Edge.class);
        when(edge2.getTargetVertexId()).thenReturn(new Text("6"));
        when(edge2.getValue()).thenReturn(new VIntWritable(4));

        rw = mock(RecordWriter.class);

        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(edge1, edge2);

    }

    public TextEdgeWriter<Text, VIntWritable, VIntWritable> createEdgeWriter(final RecordWriter<Text, Text> rw) {
        return new SimpleTsvEdgeWriter() {
            @Override
            protected RecordWriter<Text, Text> createLineRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
                return rw;
            }
        };
    }

    @Test
    public void testWriteGraphAsEdges() throws Exception {
        TextEdgeWriter<Text, VIntWritable, VIntWritable> writer = createEdgeWriter(rw);
        writer.setConf(conf);
        writer.initialize(tac);
        writer.writeEdge(vertex.getId(), vertex.getValue(), edge1);
        verify(rw).write(new Text("34\t12\t1"), null);
        writer.writeEdge(vertex.getId(), vertex.getValue(), edge2);
        verify(rw).write(new Text("34\t6\t4"), null);

    }

    @Test
    public void testWriteGraphWithOverriddenSeparator() throws Exception {
        TextEdgeWriter<Text, VIntWritable, VIntWritable> writer = createEdgeWriter(rw);

        GiraphConfiguration giraphConfiguration = new GiraphConfiguration();
        giraphConfiguration.set(SimpleTsvEdgeOutputFormat.LINE_TOKENIZE_VALUE, ":");

        writer.setConf(new ImmutableClassesGiraphConfiguration(giraphConfiguration));
        writer.initialize(tac);
        writer.writeEdge(vertex.getId(), vertex.getValue(), edge1);
        verify(rw).write(new Text("34:12:1"), null);
        writer.writeEdge(vertex.getId(), vertex.getValue(), edge2);
        verify(rw).write(new Text("34:6:4"), null);

    }

    @Test
    public void testGraphWriteWithEmptyEdgeWeight() throws Exception {
        TextEdgeWriter<Text, VIntWritable, VIntWritable> writer = createEdgeWriter(rw);

        writer.setConf(conf);
        writer.initialize(tac);

        Edge<Text, VIntWritable> edge = mock(Edge.class);
        when(edge.getTargetVertexId()).thenReturn(new Text("12"));
        when(edge.getValue()).thenReturn(new VIntWritable());

        writer.writeEdge(vertex.getId(), vertex.getValue(), edge);
        verify(rw).write(new Text("34\t12\t0"), null);

    }
}