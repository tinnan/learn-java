## References
Spock + Groovy learning materials \
https://www.baeldung.com/groovy-spock
https://github.com/spockframework/spock-example


### Setting up the project
1. According to [spock-example](https://github.com/spockframework/spock-example), the following dependencies are 
   required to use Spock.
   ```Groovy
   dependencies {
       implementation platform('org.apache.groovy:groovy-bom:4.0.20')
       implementation 'org.apache.groovy:groovy'
       testImplementation platform("org.spockframework:spock-bom:2.3-groovy-4.0")
       testImplementation "org.spockframework:spock-core"
       // you can remove this if your code does not rely on old JUnit 4 rules
       testImplementation "org.spockframework:spock-junit4"
   }
   ```
2. Since we will be writing tests with Groovy. Add Groovy plugin to `build.gradle`.
3. All tests written in Groovy must be under `test/groovy` directory.