
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

package org.monarchinitiative.exomiser.core.analysis;

import com.google.common.collect.Lists;
import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import org.junit.Test;
import org.monarchinitiative.exomiser.core.filters.*;
import org.monarchinitiative.exomiser.core.genome.GenomeAssembly;
import org.monarchinitiative.exomiser.core.model.frequency.FrequencySource;
import org.monarchinitiative.exomiser.core.model.pathogenicity.PathogenicitySource;
import org.monarchinitiative.exomiser.core.prioritisers.NoneTypePrioritiser;
import org.monarchinitiative.exomiser.core.prioritisers.Prioritiser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class AnalysisTest {

    private static final Analysis DEFAULT_ANALYSIS = Analysis.builder().build();

    private Analysis.Builder newBuilder() {
        return Analysis.builder();
    }

    private List<AnalysisStep> getAnalysisSteps() {
        VariantFilter geneIdFilter = new GeneSymbolFilter(new HashSet<>());
        Prioritiser noneTypePrioritiser = new NoneTypePrioritiser();
        GeneFilter inheritanceFilter = new InheritanceFilter(ModeOfInheritance.ANY);
        VariantFilter targetFilter = new PassAllVariantEffectsFilter();

        return Lists.newArrayList(geneIdFilter, noneTypePrioritiser, inheritanceFilter, targetFilter);
    }

    @Test
    public void testCanSetAndGetVcfFilePath() {
        Path vcfPath = Paths.get("vcf");
        Analysis instance = newBuilder()
                .vcfPath(vcfPath)
                .build();
        assertThat(instance.getVcfPath(), equalTo(vcfPath));
    }

    @Test
    public void testCanSetAndGetPedFilePath() {
        Path pedPath = Paths.get("ped");
        Analysis instance = newBuilder()
                .pedPath(pedPath)
                .build();
        assertThat(instance.getPedPath(), equalTo(pedPath));
    }

    @Test
    public void canSetProbandSampleName() {
        String probandSampleName = "Slartibartfast";
        Analysis instance = Analysis.builder()
                .probandSampleName(probandSampleName)
                .build();
        assertThat(instance.getProbandSampleName(), equalTo(probandSampleName));
    }

    @Test
    public void canSetGenomeAssembly() {
        Analysis instance = Analysis.builder()
                .genomeAssembly(GenomeAssembly.HG19)
                .build();
        assertThat(instance.getGenomeAssembly(), equalTo(GenomeAssembly.HG19));
    }

    @Test
    public void modeOfInheritanceDefaultsToUnspecified() {
        assertThat(DEFAULT_ANALYSIS.getModeOfInheritance(), equalTo(ModeOfInheritance.ANY));
    }

    @Test
    public void testCanMakeAnalysis_specifyModeOfInheritance() {
        ModeOfInheritance modeOfInheritance = ModeOfInheritance.AUTOSOMAL_DOMINANT;
        Analysis instance = newBuilder()
                .modeOfInheritance(modeOfInheritance)
                .build();
        assertThat(instance.getModeOfInheritance(), equalTo(modeOfInheritance));
    }

    @Test
    public void analysisModeDefaultsToPassOnly() {
        assertThat(DEFAULT_ANALYSIS.getAnalysisMode(), equalTo(AnalysisMode.PASS_ONLY));
    }

    @Test
    public void analysisCanSpecifyAlternativeAnalysisMode() {
        Analysis instance = newBuilder()
                .analysisMode(AnalysisMode.FULL)
                .build();
        assertThat(instance.getAnalysisMode(), equalTo(AnalysisMode.FULL));
    }

    @Test
    public void testFrequencySourcesAreEmptyByDefault() {
        assertThat(DEFAULT_ANALYSIS.getFrequencySources().isEmpty(), is(true));
    }

    @Test
    public void canSpecifyFrequencySources() {
        Set<FrequencySource> sources = EnumSet.allOf(FrequencySource.class);
        Analysis instance = newBuilder()
                .frequencySources(sources)
                .build();
        assertThat(instance.getFrequencySources(), equalTo(sources));
    }

    @Test
    public void testPathogenicitySourcesAreEmptyByDefault() {
        assertThat(DEFAULT_ANALYSIS.getPathogenicitySources().isEmpty(), is(true));
    }

    @Test
    public void canSpecifyPathogenicitySources() {
        Set<PathogenicitySource> sources = EnumSet.allOf(PathogenicitySource.class);
        Analysis instance = newBuilder()
                .pathogenicitySources(sources)
                .build();
        assertThat(instance.getPathogenicitySources(), equalTo(sources));
    }

    @Test
    public void testGetAnalysisSteps_ReturnsEmptyListWhenNoStepsHaveBeedAdded() {
        List<AnalysisStep> steps = Collections.emptyList();
        assertThat(DEFAULT_ANALYSIS.getAnalysisSteps(), equalTo(steps));
    }

    @Test
    public void testCanAddVariantFilterAsAnAnalysisStep() {
        VariantFilter variantFilter = new PassAllVariantEffectsFilter();
        Analysis instance = newBuilder()
                .addStep(variantFilter)
                .build();
        assertThat(instance.getAnalysisSteps(), hasItem(variantFilter));
    }

    @Test
    public void testCanAddGeneFilterAsAnAnalysisStep() {
        GeneFilter geneFilter = new InheritanceFilter(ModeOfInheritance.ANY);
        Analysis instance = newBuilder()
                .addStep(geneFilter)
                .build();
        assertThat(instance.getAnalysisSteps(), hasItem(geneFilter));
    }

    @Test
    public void testCanAddPrioritiserAsAnAnalysisStep() {
        Prioritiser prioritiser = new NoneTypePrioritiser();
        Analysis instance = newBuilder()
                .addStep(prioritiser)
                .build();
        assertThat(instance.getAnalysisSteps(), hasItem(prioritiser));
    }

    @Test
    public void testGetAnalysisSteps_ReturnsListOfStepsAdded() {
        List<AnalysisStep> steps = getAnalysisSteps();

        Analysis.Builder builder = newBuilder();
        steps.forEach(builder::addStep);

        Analysis instance = builder.build();

        assertThat(instance.getAnalysisSteps(), equalTo(steps));
    }

    @Test
    public void testBuilderCanSetAllSteps() {
        List<AnalysisStep> steps = getAnalysisSteps();

        Analysis instance = newBuilder()
                .steps(steps)
                .build();

        assertThat(instance.getAnalysisSteps(), equalTo(steps));
    }

    @Test
    public void testCopyReturnsBuilder() {
        Analysis.Builder analysisBuilder = DEFAULT_ANALYSIS.copy();
        Analysis copy = analysisBuilder.build();
        assertThat(copy, equalTo(DEFAULT_ANALYSIS));
    }

    @Test
    public void testCopyBuilderCanChangeFrequencySources() {
        Analysis.Builder analysisBuilder = DEFAULT_ANALYSIS.copy();
        Analysis copy = analysisBuilder
                .frequencySources(EnumSet.of(FrequencySource.ESP_ALL))
                .build();
        assertThat(copy.getFrequencySources(), equalTo(EnumSet.of(FrequencySource.ESP_ALL)));
    }

    @Test
    public void testCopyBuilderCanAddNewAnalysisStep() {
        // this is likely a nonsense thing to do - the order of the steps is important so copying and adding a new
        // one is probably a silly thing to do.
        Analysis copy = DEFAULT_ANALYSIS.copy()
                .addStep(new NoneTypePrioritiser())
                .build();
    }

    @Test
    public void testCopyBuilderCanSetAnalysisSteps() {
        List<AnalysisStep> newSteps = getAnalysisSteps();
        Analysis copy = DEFAULT_ANALYSIS.copy()
                .steps(newSteps)
                .build();
        assertThat(copy.getAnalysisSteps(), equalTo(newSteps));
    }
}
