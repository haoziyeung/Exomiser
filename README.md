The Exomiser - A Tool to Annotate and Prioritize Exome Variants
===============================================================

[![GitHub release](https://img.shields.io/github/release/exomiser/Exomiser.svg)](https://github.com/exomiser/Exomiser/releases)
[![CircleCI](https://circleci.com/gh/exomiser/Exomiser/tree/development.svg?style=shield)](https://circleci.com/gh/exomiser/Exomiser/tree/development)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/041e1de03278443c9eb64900b839e7ac)](https://www.codacy.com/app/jules-jacobsen/Exomiser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=exomiser/Exomiser&amp;utm_campaign=Badge_Grade)

#### Overview:

The Exomiser is a Java program that finds potential disease-causing variants from whole-exome or whole-genome sequencing data.

Starting from a VCF file and a set of phenotypes encoded using the [Human Phenotype Ontology](http://www.human-phenotype-ontology.org) (HPO) it will annotate, filter and prioritise likely causative variants. The program does this based on user-defined criteria such as a variant's predicted pathogenicity, frequency of occurrence in a population and also how closely the given phenotype matches the known phenotype of diseased genes from human and model organism data.

The functional annotation of variants is handled by [Jannovar](https://github.com/charite/jannovar) and uses [UCSC](http://genome.ucsc.edu) KnownGene transcript definitions and hg19 genomic coordinates.

Variants are prioritised according to user-defined criteria on variant frequency, pathogenicity, quality, inheritance pattern, and model organism phenotype data. Predicted pathogenicity data is extracted from the [dbNSFP](http://www.ncbi.nlm.nih.gov/pubmed/21520341) resource. Variant frequency data is taken from the [1000 Genomes](http://www.1000genomes.org/), [ESP](http://evs.gs.washington.edu/EVS) and [ExAC](http://exac.broadinstitute.org) datasets. Subsets of these frequency and pathogenicity data can be defined to further tune the analysis. Cross-species phenotype comparisons come from our PhenoDigm tool powered by the OWLTools [OWLSim](https://github.com/owlcollab/owltools) algorithm.

The Exomiser was developed by the Computational Biology and Bioinformatics group at the Institute for Medical Genetics and Human Genetics of the Charité - Universitätsmedizin Berlin, the Mouse Informatics Group at the Sanger Institute and other members of the [Monarch initiative](https://monarchinitiative.org).

#### Download and Installation

The prebuilt Exomiser binaries can be obtained from the [releases](https://github.com/exomiser/Exomiser/releases) page and supporting data files can be downloaded from the [Exomiser FTP site](http://data.monarchinitiative.org/exomiser).

It is possible to use the same data sources for each major version, in order to avoid having to download the data files for each software point release. To do this, edit the ```exomiser.data-directory``` field in the ```application.properties``` file to point to the data directory of the other installation.
    
For example you have an exomiser installation located at ```/opt/exomiser-cli-7.0.0``` which contains the data files in the directory ```/opt/exomiser-cli-7.0.0/data```. You can use the release 7.2.3 (same major version) by unzipping the release to ```/opt/exomiser-cli-7.2.3``` and changing the line in the file ```/opt/exomiser-cli-7.2.3/application.properties``` from
```properties
#root path where data is to be downloaded and worked on
#it is assumed that all the files required by exomiser listed in this properties file
#will be found in the data directory unless specifically overridden here.
exomiser.data-directory=data
```
to
```properties
exomiser.data-directory=/opt/exomiser-cli-7.0.0/data
```

For further instructions on installing and running please refer to the [README.md](http://data.monarchinitiative.org/exomiser/README.md) file.

#### Running it

Please refer to the [manual](http://exomiser.github.io/Exomiser/) for details on how to configure and run the Exomiser.

#### Demo site

There is a limited [demo version](http://exomiser.monarchinitiative.org/exomiser/) of the exomiser hosted by the [Monarch Initiative](https://monarchinitiative.org/). This instance is for teaching purposes only and is limited to small exome analysis.

#### Using The Exomiser in your code

The exomiser can also be used as a library in Spring Java applications. Add the ```exomiser-spring-boot-starter``` library to your pom/gradle build script.

In your configuration class add the ```@EnableExomiser``` annotation
 
 ```java
@EnableExomiser
public class MainConfig {
    
}
```

Or if using Spring boot for your application, the exomiser will be autoconfigured if it is on your classpath.

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

In your application use the AnalysisBuilder obtained from the Exomiser instance to configure your analysis. Then run the Analysis using the Exomiser class.
Creation of the Exomiser is a complicated process so defer this to Spring and the exomiser-spring-boot-starter. Calling the ```add``` prefixed methods 
will add that analysis step to the analysis in the order that they have been defined in your code.

Example usage:
```
@Autowired
private final Exomiser exomiser;

...
           
    Analysis analysis = exomiser.getAnalysisBuilder()
                .genomeAssembly(GenomeAssembly.HG19)
                .vcfPath(vcfPath)
                .pedPath(pedPath)
                .probandSampleName(probandSampleId)
                .hpoIds(phenotypes)
                .analysisMode(AnalysisMode.PASS_ONLY)
                .modeOfInheritance(ModeOfInheritance.AUTOSOMAL_DOMINANT)
                .frequencySources(FrequencySource.ALL_EXTERNAL_FREQ_SOURCES)
                .pathogenicitySources(EnumSet.of(PathogenicitySource.POLYPHEN, PathogenicitySource.MUTATION_TASTER, PathogenicitySource.SIFT))
                .addPhivePrioritiser()
                .addPriorityScoreFilter(PriorityType.PHIVE_PRIORITY, 0.501f)
                .addQualityFilter(500.0)
                .addRegulatoryFeatureFilter()
                .addFrequencyFilter(0.01f)
                .addPathogenicityFilter(true)
                .addInheritanceFilter()
                .addOmimPrioritiser()
                .build();
                
    AnalysisResults analysisResults = exomiser.run(analysis);
```
 
#### Memory usage

Analysing whole genomes using the ``AnalysisMode.FULL`` or ``AnalysisMode.SPARSE`` will use a lot of RAM (~16GB for 4.5 million variants without any extra variant data being loaded) the standard Java GC will fail to cope well with these.
Using the G1GC should solve this issue. e.g. add ``-XX:+UseG1GC`` to your ``java -jar -Xmx...`` incantation. 

#### Caching

Since 9.0.0 caching uses the standard Spring mechanisms.
 
To enable and configure caching in your Spring application, use the ```@EnableCaching``` annotation on a ```@Configuration``` class, include the required cache implementation jar and add the specific properties to the ```application.properties```.

For example, to use [Caffeine](https://github.com/ben-manes/caffeine) just add the dependency to your pom:

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```
and these lines to the ```application.properties```:
```properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=300000
```