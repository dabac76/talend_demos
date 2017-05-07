---
layout: default
---

### Motivation

Flow-based programming fits nicely to data-centric problems and talend
leverages that through Eclipse GEF framework. Whenever possible, user should follow
it's design and utilize native components to accomplish data transformation task.
However, from time to time you will encounter a problem that requires you to get
your hands dirty with some Java coding, instead of just declaring native component 
properties or occasional SQL. These problems usually fall into categories like:

* More demanding transformations going beyond aggregation & filtering.
* Parsing weakly structured or unstructured data, like logs or text.  
* Working with data sources having weak talend native components support (or none at all) 
  like JSON or HTML.

In cases like these your first stop should be talend [exchange](https://exchange.talend.com/). 
Maybe somebody already solved your problem and made a nice custom component. 
But, if not, then tJavaRow, tJavaFlex tJava\* components are the only way forward. 
Moreover, their usage often comes together with the change of flow of logic in 
diagram when you have to switch between flow to iterate and back. 
Examples for them given in talend documentation are too rudimentary. 
These demo jobs are trying to extend on that, show you some of their quirks and apply 
them on often seen etl scenarios, no matter the underlying problem to be solved. 
Most of them exploit the same design pattern: a blend of two or more serially 
connected tJava\* components, commanding either iteration or data-flow, passing 
between each other some Java collection object like array or hashMap and with some 
external library in play that applies to the particular problem like 
JSON or HTML parsing or similar...    

So, inside this TOS project, among else, you will find 6 jobs under GithubExamples folder :

* **HTMLParsing**     - Works with Jsoup Java library. Demonstrates cascade of tJavaFlex 
  components and iterate connections between them. 

* **HTMLScraping**    - Similar problem like former, just a bit more complex. Employs 
  SQLite as destination storage. 

* **HTTPJsonAPI**     - Talend lacks support for serious work on json format. 
  Best option is to work directly in tJava\* components using Jayway JsonPath library.
  Job demonstrates common setup for it by querying [libraries.io](https://libraries.io/) API.  

* **ParseComplexLog** - One possible template when digging through semi-structured 
  data. You make two passes on data. First pass to get the line numbers for some hooks, 
  using regular expressions. In second pass you use discovered hooks to process the data.    

* **JoinHashInOut**   - Demonstrates a pattern to implement something similar to 
  talend's tMap, using technical components tHashOutput/tHashInput. This way you 
  can relax join condition. Instead of strict equality between two values it can be
  anything. In this particular case, join condition is if all the words in a shorter
  string exist in a longer string. 

* **JoinJavaFlex**    - Same like previous example, just with tJavaFlex components.
  Example deals with the case of tagging a string. A set of all tags is loaded in lookup 
  table.

All jobs are heavily commented. Follow the links in header to find more details. 

### Using External Java Libraries in Talend Jobs

Since talend is derived from Eclipse IDE, it pulls a bunch of dead functionalities.
You can see and access them but they are either turned off or simply don't
work which is rather annoying. For example a standard place to handle external 
libraries in Eclipse: `Preferences/Java/User Libraries` doesn't work. 
Even more, talend documentation on this topic is not clear, as you can see just 
by the sheer number of comments 
[there](https://help.talend.com/display/KB/How+to+install+external+modules+in+the+Talend+products). 

So here is my take on how to insert external Java dependencies: 

* You have 2 possible places where you'd want to use external dependencies: 
  in your routine (`Repository/Code/Routines`) or in your job inside tJava\* components.

* You have 3 options how to inject external dependencies (.jar) into talend: 
  (1) `Repository/Code/Routines/your_routine/Edit routine libraries/new`. (2) Module view
  (`Window/Show view/Modules/Import external jars button`) or (3) tLibraryLoad component.
  If you want to use the external lib inside your code routine you will go with option (1) 
  and immediately after you will see imported lib in the Modules view.  
  Option (3) makes your dependencies available only for that particular job at runtime 
  and is convenient if you plan to build-export the job (`Job/Build Job`) and deploy it on some server.
  But what if you want to use external lib inside your job in tJava\* components 
  **without** declaring your job dependent on any routines (`Job/Setup routine dependencies`) ?
  There is a catch:

* Let's say that you've pushed the `Modules/import external jars` button, and installed a new jar.
  This library is now copied to `.m2/repository/org/talend/libraries`. So talend really did 
  import this .jar. But, it's still unaware of it. You're still not able to use it anywhere 
  through Java `import` statement and it still doesn't show up in your Modules view.
  
  Now the strange catch is this: No matter how you'd want to use this freshly imported
  .jar (in routine or in job's tJava\*), for talend to be aware of it, you **have** to declare this
  dependency in some routine using `Edit routine libraries`. Then, **and only then** 
  (as of talend v6.5) will this new .jar appear in Modules view! And only after that will
  you able to use Java `import` statement inside a job's tJava\* component, even without declaring
  your job as dependent on a routine where you declared dependency on the particular .jar. Strange.   

  To recap: you can import external .jar in two ways:
  1. Go to `Windows/Show view/Modules/import external jars`. Browse and import.
     Talend is still not aware of this library and it will **not** show it in modules. 
     Go to `Repository/Code/Routines/any_routine/Edit routine libraries/new/input library's 
     name` and input exact .jar's filename. Now it will be listed in Modules and you're 
     able to use it inside that routine and inside any job's tJava\* component by 
     declaring this dependency in component's import tab. You don't have to make your job 
     dependent on the routine where you referenced that .jar.
  2. Go directly to `Repository/Code/Routines/your_routine/Edit routine libraries/new/Browse 
     a library file` and import the library. You will see it listed in Modules and you're 
     able to use it inside that routine and inside any job's tJava\* component by 
     declaring this dependency in component's import tab. You don't have to make your job 
     dependent on the routine where you referenced that .jar. 

* (Off remark) Never use `Window/Show view/Modules/Download External modules`, 
  especially on Linux. This window is a disaster and will completely freeze the application.

* (Off remark) If your subjob(!) contains more than one tJava\* component it's sufficient
  to import all necessary dependencies in only first one. 

### Talend Framework

For anything more important than homework it's advisable to wrap your jobs in this
useful and very well thought of talend [framework](http://talendframework.com/):
* It creates a folder tree and follows a clear convention where to keep all important 
  resources for project/job like: input/output files, archive, reports, logs, statistics.
  This also makes your work easy to port and backup. 
* Puts all your context variables in files, and allows to change the context by 
  editing files in .ini like syntax. 
* Allows optional hierarchy of context files. You can have one context file per 
  environment (dev|prod|uat) on project level that is inherited for all its jobs. 
  And another context file for each job separately.
* It helps you to easily setup monitoring, statistics and error handling in your jobs.

Inside the tos demo project you have the framework already embedded and initialized for 
hierarchy of context files. **JoinHashInOut** demo job is wrapped in it ("frameworked") 
as an example of how to embed your jobs in it and use it. When you extract tfFileSystem.7z 
in your home directory and import tos project, you will be able to execute job inside 
`GithubExamples/HashJoin/HashJoin` immediately without any context variable modification.

For more info visit the [website](http://talendframework.com/). What follows is a 
digest of the most important info from that website if you would like to set your 
repository under the framework from scratch:

#### Setup of tFramework

* Configure on a project level. This is done only once per project. 
  `File/Edit Project properties/Job Settings/Stats & Logs`, check: logs/statistics/volumetrics/on file, input:
  
  ```Java
  File Path:  Framework.getDefaultStatsDir(context.statsDir, context.baseDir, contextStr, projectName, jobName)  
  Stats file: String.format("stats_file.%s.txt", TalendDate.formatDate("yyyyMMdd", TalendDate.getCurrentDate())) 
  Log file:   String.format("logs_file.%s.txt", TalendDate.formatDate("yyyyMMdd", TalendDate.getCurrentDate()))  
  Meter file: String.format("meter_file.%s.txt", TalendDate.formatDate("yyyyMMdd", TalendDate.getCurrentDate())) 
  ```

* [Configure](http://talendframework.com/organising-your-projects) hierarchy of context files. 
  This is done only once for all future projects so that Framework/FileSystem/SendmMail context 
  groups are shared between all projects, all other contexts will be on job level.
  
  Run once Framework's test job `Framework/Template/Template` to initialize folder tree.
  Create context folder in `#HOME/project/talend/#CONTEXTNAME/context` (same level as project folders). <br>
  Copy `(Dev|Default).(FileSystem|Framework|SendMail).context files` from `Repo/Documentation/Framework/ContextFiles` 
  to new folder.

#### Tips on Using tFramework

* Context group 'Default' (meaning **by name** default, not by property to be default used) is reserved 
  for purpose of Framework test only. Stick only to provided Dev|Prod|Uat avoid defining more.
* You can develop your job outside the framework and connect it later. Remember to decouple 
  your job properties from the project properties during that time: `Job view/Extra` and
  `Job view/Stats&Logs`, de-tick `Use project settings`.
* You create all your context groups/variables like always through repository. At the 
  moment when you want to wrap your job in framework you need to create context file for your job 
  like this: 
  * Copy `Repo/Documentation/Framework/ContextFiles/Default.Template.context` to a new
  folder at `#HOME/project/talend/#CONTEXTNAME/#PROJECTNAME/context`.
  * Rename that file to have the same `#CONTEXTNAME` under which path it is placed and 
    same name as your job has (e.g. Dev.MyNewJob.context)  
  * Open the file and edit `autoload` path entries. Replace with the right `#CONTEXTNAME`.
  * If you previously defined some new context variables, just amend this file with them
    , by placing additional lines with `variable=value` statements. This way your context 
    variables will be loaded at runtime from a file. 
* Finally how to wrap the job inside a framework? Like this:
  * Each job has its own folder. In the root of that folder you copy the following
    framework jobs: `Template/Template`, `Template/TemplateProcess`, `Template/TemplateOrchestrate`.
    Last one is optional and is used if you want to call more than one task.
    In template wording "task" denotes your job that actually performs something.
    Create additional subfolder and copy there `Template/TemplateTask`.
  * Rename all copied jobs by replacing the `Template` prefix with your job name.
  * You embed your job by copy-pasting all components of the flow inside a `TemplateTask` job.
  * Components in Framework's jobs are color coded: 
    * RED -> don't touch. 
    * YELLOW -> to be modified. Placeholders for your own logic. When changed mark them ORANGE. 
    * GRAY -> to be replaced with your own.
  You will see inside a TemplateTask job a gray component that needs to be replaced with yours and 
  this is where you hook your job to the framework.
* The executable job is the `Template/Template` job (which you, by now, have renamed appropriately). 

### Places to Find Useful Tips on Talend

* [Open Analytics](https://gabrielebaldassarre.com/category/talend/)
* [Bekwam's blog](http://bekwam.blogspot.rs/)
* [PowerUp's tutorial on component creation](http://powerupbi.com/talend/componentCreation_index.html)
* [Talend by Example](https://www.talendbyexample.com/)
* [Talend hunter blog](http://talendhunter.blogspot.rs/)
* [Talend framework](http://talendframework.com/)