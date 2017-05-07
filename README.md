# Talend Demos
Talend Open Studio 6.x demo jobs with emphasis on: generic tJava|Row|Flex components, 
usage of external libraries and workflows that include flow of Java Objects (not only talend types). Example problems: Log parsing, HTML parsing and scraping, JSON API parsing, Java hash table join implementation. 

For more details and screenshots please visit [github pages](https://dabac76.github.io/talend_demos/).

## Contents & Instructions
* `TOS_workspace_20170428.zip` is a talend open studio project. It contains 6 demo jobs, [talend framework](http://talendframework.com/) and a few useful code routines. Two jobs demonstrate HTML parsing using [JSoup](https://jsoup.org/) library. One is about JSON parsing using Jayway [JsonPath](https://github.com/json-path/JsonPath) library. The rest are showing some data transformation and join possibilities with generic tJava\* components. You can import it with talend's import project dialog. 
* `tFileSystem.7z` is a talend framework compliant folder tree containing all input data and external libraries you will need in order to run the jobs. Unpack it in your home folder. Before running jobs edit paths in context variables to reflect your local path. Context variables whose values need to be changed are: `HttpJsonAPi/searchList`, `JoinHashInOut/sqlitedb`, `JoinJavaFlex/groceryList`, `JoinJavaFlex/tagsMap` and `ParseComplexLog/logDirPath`.
* `RoutinesGithubExamples.java` is a code routine script. Contains all the custom methods used throughout jobs. It's here just in case you want to take a peek without going through project import.
* `TOS_documentation_20170428.zip` is talend generated job documentation. You can download and inspect all code and flows without importing project.     

