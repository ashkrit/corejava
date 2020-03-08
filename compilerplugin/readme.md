# Maven incremental compiler has two issues

1. Unable to identify deleted files
2. Executes unit tests even when no source code is changed

This maven plugin addreses both the issues by
 1. Cleaning target location when code is changed and trigger full build.
 2. Disable unit test when no code is changed.

Both of the these features can help in reducing compilation time.


# How to use

### Add entry in pom file
```
<plugin>
                <groupId>mavenplugin</groupId>
                <artifactId>compilerplugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <id>pre-clean</id>
                        <phase>pre-clean</phase>
                        <goals>
                            <goal>inc</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

## Use pre-clean instead of clean
mvn pre-clean install

# Run log

## When code is changed
```
[INFO] --- compilerplugin:1.0-SNAPSHOT:inc (pre-clean) @ compilerplugintest ---
[INFO] Checking ...\compilerplugintest\target\buildcheck.timestamp
[INFO] Last changed file/folder is ...\compilerplugintest\target\buildcheck.timestamp
[INFO] Checking ...\compilerplugintest\src\main\java
[INFO] Checking ...\compilerplugintest\src\main\scala
[INFO] Checking ...\compilerplugintest\src\main\resources
[INFO] Checking ...\compilerplugintest\src\test\java
[INFO] Checking ...\compilerplugintest\src\test\scala
[INFO] Checking ...\compilerplugintest\src\test\resources
[INFO] Checking ...\compilerplugintest\pom.xml
[INFO] Last changed file/folder is ...\compilerplugintest\src\main\scala\runner
[INFO] Code compiled at 2020-03-08T12:35:37.790
[INFO] Code changed at 2020-03-08T12:37:50.202
[INFO] Code was changed 2 MINUTES 12 SECONDS  after compilation
[INFO] Changed detected - cleaning ...\compilerplugintest\target
[INFO] Total time 467 ms
```

## When code is not changed
```
[INFO] --- compilerplugin:1.0-SNAPSHOT:inc (pre-clean) @ compilerplugintest ---
[INFO] Checking ...\compilerplugintest\target\buildcheck.timestamp
[INFO] Last changed file/folder is ...\compilerplugintest\target\buildcheck.timestamp
[INFO] Checking ...\compilerplugintest\src\main\java
[INFO] Checking ...\compilerplugintest\src\main\scala
[INFO] Checking ...\compilerplugintest\src\main\resources
[INFO] Checking ...\compilerplugintest\src\test\java
[INFO] Checking ...\compilerplugintest\src\test\scala
[INFO] Checking ...\compilerplugintest\src\test\resources
[INFO] Checking ...\compilerplugintest\pom.xml
[INFO] Last changed file/folder is ...\compilerplugintest\src\main\scala\runner
[INFO] Code compiled at 2020-03-08T12:37:56.458
[INFO] Code changed at 2020-03-08T12:37:50.202
[INFO] Nothing to clean - Source and target are up to date. Not updated from 50 MINUTES 25 SECONDS
[INFO] Total time 396 ms
```