/*
 * Copyright 2017 Goldman Sachs.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.gs.tablasco;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class HideMatchedTablesTest
{
    @Rule
    public final TableVerifier tableVerifier = new TableVerifier()
            .withFilePerMethod()
            .withMavenDirectoryStrategy()
            .withHideMatchedTables(true);

    @Test
    public void matchedTablesAreHidden() throws IOException
    {
        final VerifiableTable matchTable = new TestTable("Col").withRow("A");
        final VerifiableTable outOfOrderTableExpected = new TestTable("Col 1", "Col 2").withRow("A", "B");
        final VerifiableTable outOfOrderTableActual = new TestTable("Col 2", "Col 1").withRow("B", "A");
        final VerifiableTable breakTableExpected = new TestTable("Col").withRow("A");
        final VerifiableTable breakTableActual = new TestTable("Col").withRow("B");
        TableTestUtils.assertAssertionError(() -> tableVerifier.verify(
                TableTestUtils.toNamedTables("match", matchTable, "break", breakTableExpected, "outOfOrder", outOfOrderTableExpected),
                TableTestUtils.toNamedTables("match", matchTable, "break", breakTableActual, "outOfOrder", outOfOrderTableActual)));
        Assert.assertEquals(
                "<body>\n" +
                "<div class=\"metadata\"/>\n" +
                "<h1>matchedTablesAreHidden</h1>\n" +
                "<div id=\"matchedTablesAreHidden.break\">\n" +
                "<h2>break</h2>\n" +
                "<table border=\"1\" cellspacing=\"0\">\n" +
                "<tr>\n" +
                "<th class=\"pass\">Col</th>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class=\"surplus\">B<p>Surplus</p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class=\"missing\">A<p>Missing</p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</div>\n" +
                "<div id=\"matchedTablesAreHidden.outOfOrder\">\n" +
                "<h2>outOfOrder</h2>\n" +
                "<table border=\"1\" cellspacing=\"0\">\n" +
                "<tr>\n" +
                "<th class=\"outoforder\">Col 2<p>Out of order</p>\n" +
                "</th>\n" +
                "<th class=\"pass\">Col 1</th>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class=\"outoforder\">B<p>Out of order</p>\n" +
                "</td>\n" +
                "<td class=\"pass\">A</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</div>\n" +
                "</body>"
        , TableTestUtils.getHtml(this.tableVerifier, "body"));
    }
}
