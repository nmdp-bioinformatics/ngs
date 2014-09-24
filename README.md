ngs
===

Next generation sequencing (NGS/HTS) tools.

[![Build Status](https://travis-ci.org/nmdp-bioinformatics/ngs.svg?branch=master)](https://travis-ci.org/nmdp-bioinformatics/ngs)


###Using ngs

Artifacts from this project are available from the Maven Central repository.

E.g.
```xml
<dependency>
  <groupId>org.nmdp.ngs</groupId>
  <artifactId>ngs-align</artifactId>
  <version>${version}</version>
</dependency>
```

 * [Maven Central artifact search](http://search.maven.org/#search|ga|1|g%3A%22org.nmdp.ngs%22)
 * [Javadoc documentation for latest release](http://nmdp-bioinformatics.github.io/ngs/apidocs/1.4)


###Using ngs-tools

The ngs-tools module builds several command line tools.

For example
```bash
$ ngs-downsample-fastq -h
usage:
java DownsampleFastq -p 0.5 -z 42 [args]

arguments:
  -h, --help  display help message [optional]
  -i, --input-fastq-file [class java.io.File]  input FASTQ file, default stdin [optional]
  -o, --output-fastq-file [class java.io.File]  output FASTQ file, default stdout [optional]
  -p, --probability [class java.lang.Double]  probability a FASTQ record will be removed, [0.0-1.0] [required]
  -z, --seed [class java.lang.Integer]  random number seed, default relates to current time [optional]
```


###Hacking ngs

Install

 * JDK 1.7 or later, http://openjdk.java.net
 * Apache Maven 3.2.3 or later, http://maven.apache.org

To build

    $ mvn install