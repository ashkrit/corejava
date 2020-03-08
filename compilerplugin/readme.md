# Maven incremental compiler has two issues

1. Unable to identify deleted files
2. Executes unit tests even when no source code is changed

This maven plugin addreses both the issues by
 1. Cleaning target location when code is changed and trigger full build.
 2. Disable unit test when no code is changed.

Both of the these features can help in reducing compilation time.