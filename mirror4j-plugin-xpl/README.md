Additionnal plugin to use with the mirror4j project, in order to send event on the XPL bus

# Plugin installation

You just have to put the whole needed libs in the same lib/ directory than mirror4j, e.g., "mvn dependency:copy-dependencies" on this project and copy every jar under target/dependency/ + the main mirror4j-plugin-* jar into mirror4j/libs/ (the mirror4j program will automatically loads additionnal jar plugins and will detect the plugin once loaded).

# About xPL

You must have a running xPL installation (mainly, a running xPL hub, check [xpl-perl](https://github.com/beanz/xpl-perl) for example).

