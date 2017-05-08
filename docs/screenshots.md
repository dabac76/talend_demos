---
layout: default
---
[back](./)

### 1. HTMLParsing
![]({{ site.baseurl }}/assets/images/HTMLParsing_3.2_20170428.png)

A simple website target is chosen to demonstrate scraping tJava pattern. Multiple serially connected tJavaFlexes (here two) using `iterate` connection, creating multiple inner data loops until you converge to the exact parsing target and parse it with the last tJavaFlex or tJavaRow. Each tJavaFlex connected with `iterate` connection passes a collection object which is iterated within a subsequent tJavaFlex. Eventually, tJavaRow receives a single object (HashMap) per iteration and unpacks its values for the final destination.

### 2. HTMLScraping
![]({{ site.baseurl }}/assets/images/HTMLScraping_1.0_20170428.png)

A slightly complicated website target is chosen for this case. You can see the same cascading pattern like in previous case. One side trick is introduced here. When you have some database preparation work, like table creation/deletion or content deletion prior to job start, you can create multiple stored procedures and call it from talend with tLoop -> t(someDatabase)Row - check out the 4th example. But you can also type a few useful methods that leverage jdbc and keep all logic in talend. In this case, after successful connection to SQLite a wrapper method around jdbc's 'executeUpdate' is called in order to: create table if it doesn't exist and delete all records inside if it exists. All this is possible because talend database connection components contain a hidden(!?) property:
```Java
(Connection)globalMap.get("conn_tSQLiteConnection_1");
```
In this case it's SQLite , but all other have it as well with similar naming. This you pass to the custom method. 

Remember, even if you have a big fat schema you don't have to type sql create statement at all. Instead use this trick:
* Finish your data flow. In database output component pick as 'Action on table': 'Create table' or 'Create table if does not exist' as you need.
* Press 'Code' tab. Talend just generated the whole create statement for you inside Java code :) Just find it and copy it. 
* Don't forget now to switch 'Action on table' back to whatever else action you need like default/insert/update.
  
### 3. HttpJsonAPI
![]({{ site.baseurl }}/assets/images/HttpJsonAPI_0.2_20170428.png)

Here we're talking to [libraries.io](https://libraries.io/) API. Although not necessary here, 'Init session' subjob is kept just to show where you'd initiate a session if API demands one (not shown in screenshot). 
Data is first cleansed before injecting into query string. tJavaFlex components who actually perform JSON parsing can be cascaded, likewise those in HTML parsing. There is only one here, because of the simple scenario. Finally, tJavaRow component receives HashMap object and flattens the data for the output. There are quite a few external dependencies to satisfy when setting up JSON parsing job. They are all listed inside a GithubExamples code routine. You may find that info helpful. 

### 4. JoinHashInOut
![]({{ site.baseurl }}/assets/images/JoinHashInOut_0.2_20170428.png)

Talend's tMap is a central workhorse node for integrating data. It's powerful but, maybe in some edge cases you might want some strange join logic that it's not capable to support. Here is an example of that. The pattern is always the same. You have at least 2 subjobs. One or more branch to build a lookup table, and the other with a main table flow. Though, if you plan to use talend's tHashIn\|Out technical components you're constrained to have only one lookup table. In the next example, with tJavaFlex implementation you're not. You then enclose all your join logic in a code routine's static method. In this case tJavaRow calls a method that takes 2 strings, one from lookup table and one from the main table. It returns True (i.e. performs a join - propagates a value from a lookup table) in case when a longer of those two strings contains all the words from the shorter one. Yes, weird, but it's a demo :) With a bit of a stretch you might imagine a scenario in natural language processing (NLP) where you might encounter a need for this kind of 'fuzzy joins'.

Note: You first have to unlock technical component to see them in the palette. Press palette filter button and drop them into show panel.

### 5. JoinJavaFlex
![]({{ site.baseurl }}/assets/images/JoinJavaFlex_0.3_20170428.png)

This example is analogous to the previous one, just using plain tJavaFlexlexes and HashTables instead of talend's technical components which gives you even more freedom. You can have more than one lookup tables and use all in a join node. Scenario in question is about tagging a certain string. Lookup table is a set of all tags, which is scanned for each incoming string in main table.

### 6. ParseComplexLog
![]({{ site.baseurl }}/assets/images/ParseComplexLog_0.3_20170428.png)

This job shows a potential usage of tJava components in parsing semi structured data. If you have some logs partly structured and partly not, one approach to find data inside is to make two passes through log file. During first pass you digest the file with some regular expressions and build a HashMap or a Set or a similar collection object containing 'hooks' (markers) found inside log file. In second pass you use those hooks to further process that log in parts, each part is between two neighboring hooks. This job also shows an example of unit testing and assertion in talend.  
