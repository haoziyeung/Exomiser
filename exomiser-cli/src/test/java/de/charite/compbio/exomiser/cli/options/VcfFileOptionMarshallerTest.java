/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.charite.compbio.exomiser.cli.options;

import de.charite.compbio.exomiser.core.ExomiserSettings;
import java.nio.file.Paths;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class VcfFileOptionMarshallerTest {
    
    VcfFileOptionMarshaller instance;
    
    public VcfFileOptionMarshallerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new VcfFileOptionMarshaller();
    }

    @Test
    public void testGetCommandLineParameter() {
        
        assertThat(instance.getCommandLineParameter(), equalTo("vcf"));
    }

    @Test
    public void testHasAnOption() {

        assertThat(instance.getOption(), is(notNullValue()));
    }

    @Test
    public void testApplyValuesToSettingsBuilderSetsVcfValue() {
        System.out.println("applyValuesToSettingsBuilder");
        String vcfFileName = "123.vcf";
        String[] values = {vcfFileName};
        
        ExomiserSettings.SettingsBuilder settingsBuilder = new ExomiserSettings.SettingsBuilder();
        instance.applyValuesToSettingsBuilder(values, settingsBuilder);
        ExomiserSettings settings = settingsBuilder.build();
        
        assertThat(settings.getVcfPath(), equalTo(Paths.get(vcfFileName)));
    }
    
}
