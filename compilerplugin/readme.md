# Maven incremental compiler has two issues

1. Unable to identify deleted files
2. Executes unit tests even when no source code is changed

This maven plugin addreses both the issues by
 1. Cleaning target location when code is changed and trigger full build.
 2. Disable unit test when no code is changed.

Both of the these features can help in reducing compilation time.


#How to use

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