/*
 * The Exomiser - A tool to annotate and prioritize genomic variants
 *
 * Copyright (c) 2016-2017 Queen Mary University of London.
 * Copyright (c) 2012-2016 Charité Universitätsmedizin Berlin and Genome Research Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.monarchinitiative.exomiser.autoconfigure;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanCreationException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class DataDirectoryAutoConfigurationTest extends AbstractAutoConfigurationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testUndefinedDataPath() {
        thrown.expect(BeanCreationException.class);
        thrown.expectMessage("Exomiser data directory not defined. Please provide a valid path.");
        load(DataDirectoryAutoConfiguration.class);
        Path exomiserDataDirectory = (Path) this.context.getBean("exomiserDataDirectory");
        assertThat(exomiserDataDirectory, nullValue());
    }

    @Test
    public void testEmptyDataPath() {
        thrown.expect(BeanCreationException.class);
//        thrown.expectMessage("Invalid data directory ''");
        thrown.expectMessage("Exomiser data directory not defined. Please provide a valid path.");
        load(DataDirectoryAutoConfiguration.class, "exomiser.data-directory=");
        Path exomiserDataDirectory = (Path) this.context.getBean("exomiserDataDirectory");
        assertThat(exomiserDataDirectory, nullValue());
    }

    @Test
    public void testDataPath() {
        load(DataDirectoryAutoConfiguration.class, "exomiser.data-directory=" + TEST_DATA);
        Path exomiserDataDirectory = (Path) this.context.getBean("exomiserDataDirectory");
        assertThat(exomiserDataDirectory.getFileName(), equalTo(Paths.get("data")));
    }

    @Test
    public void testWorkingDirectoryPathDefaultIsTempDir() {
        load(DataDirectoryAutoConfiguration.class, TEST_DATA_ENV);
        Path workingDirectory = (Path) this.context.getBean("exomiserWorkingDirectory");
        assertThat(workingDirectory.getFileName(), equalTo(Paths.get("exomiser")));
        assertThat(workingDirectory.getParent(), equalTo(Paths.get(System.getProperty("java.io.tmpdir"))));
    }

    @Test
    public void testCanSpecifyWorkingDirectory() {
        load(DataDirectoryAutoConfiguration.class, TEST_DATA_ENV, "exomiser.working-directory=" + TEST_DATA + "/wibble");
        Path workingDirectory = (Path) this.context.getBean("exomiserWorkingDirectory");
        assertThat(workingDirectory.getFileName(), equalTo(Paths.get("wibble")));
        assertThat(workingDirectory.getParent(), equalTo(TEST_DATA));
    }

}