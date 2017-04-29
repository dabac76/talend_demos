package routines;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils; 


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;


public class GithubExamples {

    /**
     * Finds if all words from shorter text are contained in longer text. 
     * No stemming, just simple comparison. Space is considered as word 
     * separator. It doesn't matter which argument contains longer text.
     * @param source1 text or pattern
     * @param source2 text or pattern
     * <p>
     * 
     * {talendTypes} Boolean
     * 
     * {Category} GithubExamples
     * 
     * {param} String("some , string,  other source, blabla") input: source1
     * 
     * {param} String("other source") input: source2
     * 
     * {example} containsAllWords("first part / second part", "first second") # true
     */
    public static Boolean containsAllWords(String source1, String source2) {
        String [] source1Split = ApacheStringUtils.split(source1);
        String [] source2Split = ApacheStringUtils.split(source2);        
        if (source1Split.length >= source2Split.length) {
            // source1 is text, source2 is pattern to find
            for (String elem : source2Split)
            {
                if (!ApacheStringUtils.containsIgnoreCase(source1, elem)) 
                    {return false;} 
            }
        } else {
            // source1 is pattern to find, source2 is text
            for (String elem : source1Split)
            {
                if (!ApacheStringUtils.containsIgnoreCase(source2, elem)) 
                    {return false;} 
            }       
        }
        return true;
    } //containsAllWords
    
    /**
     * Returns tag names. To apply tags, pass a <code>HashMap&lt;key,value&gt;</code>  
     * where key is a text pattern to be found in a text to be tagged and 
     * value is a tag to be applied on that text. All words inside text pattern
     * need to be found in text in order for tag to be applied.
     * {@link #containsAllWords(String, String)} 
     * @param desc text in which a pattern (key in map) is searched for. 
     * If found then tag is applied.
     * @param tagmap key=text pattern, value=tag
     * @return string formatted java set output of applied tags  
     * <p>
     * {talendTypes} String
     * 
     * {Category} GithubExamples
     * 
     * {param} String("Some text with keywords to be tagged") input:desc
     *      
     * {param} HashMap<String, String>("keywords-> tag1 ", "tagged-> tag2 ") input:tagmap
     * 
     * {example} applyTags(...) # "tag1,tag2"
     */
    public static String applyTags(String desc, java.util.HashMap<String, String> tagmap) {        
        //String tagsCommaSep = null;
        Set<String> tags = new HashSet<String>();
        for (String key : tagmap.keySet()) {
            if (GithubExamples.containsAllWords(desc, key)) {
                //tagsCommaSep = tagsCommaSep == null ? key : tagsCommaSep + "," + key;
                tags.add(tagmap.get(key));
            }
        }
        //return tagsCommaSep;
        return tags.toString();
    } //applyTags
    
    /**
     * Convert Double to Scientific format: take Double return number in 
     * scientific format.
     * @param dnum Double to parse
     * @return number in scientific notation 
     * <p>
     * 
     * {talendTypes} String
     * 
     * {Category} GithubExamples
     * 
     * {param} Double(0.123) input:dnum
     * 
     * {example} Double2Scientific(0.123) # 1.230000000000000e-001 !.
     */
    public static String Double2Scientific(Double dnum) {        
        NumberFormat formatter = new DecimalFormat("0.000000000000000E000");
        String result;
        if (dnum == null) {
            return null;
        } else {
            result = formatter.format(dnum);
            if (!result.contains("E-")) { //don't blast a negative sign
                result = result.replace("E", "E+");
            }
            return result;
        }
    } //Double2Scientific
    
    /**
     * Returns UUID.
     * @return UUID string (e.g. 682e8f79-b463-4655-8f7d-bda4e0b22f5f)
     * <p>
     * 
     * {talendTypes} String
     * 
     * {Category} GithubExamples
     * 
     * {example} getUUID() # 682e8f79-b463-4655-8f7d-bda4e0b22f5f
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    } //getUUID
    
    /**
     * Parses a String and returns a Date. 
     * Can handle more than one possible date formatting at once.
     * Just a wrap around this handy Apache DateUtils method.
     * @param str2parse string to parse.
     * @param formats   accepted date formats.
     * @return java date
     * @throws ParseException
     * <p>
     * 
     * {talendTypes} Date
     * 
     * {Category} GitHubExamples
     * 
     * {param} String("2017-04-02") input: str2parse
     *
     * {param} String[] {"yyyy-MM-dd","yyyy-MM-dd'T'HH:mm:ss.SSS"} input: formats
     *  
     * {example} parseDateMultiFormats(...) # 02/04/2017
     */
    public static Date parseDateMultiFormats(String str2parse, String[] formats) {        
        //String[] formats = {"yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd'T'HH:mm:ss.SSS"};
        try {
         return DateUtils.parseDate(str2parse, formats);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }        
    } //parseDateMultiFormats
    
     /**
      * Returns true if an element is found in a list. Accepts any Talend types.
      * @param elem     element to search for
      * @param itemList search list
      * @return boolean.
      * <p>
      * 
      * {talendTypes} Boolean
      *
      * {Category} GitHubExamples
      * 
      * {param}
      *
      * {param}
      *
      * {example} IN("a", {"a", "b"}) #true
     */
    public static <T> Boolean IN(T elem, T[] itemList) {        
        if (elem != null) {
            for (T item : itemList) {
                if (elem.equals(item)) {return true;}
            }
        } else {return false;}
        return false;
     } //IN
    
    /**
     * Returns either a single element from a json string, or a json sub-block 
     * in a form of <code>List&lt;Object&gt; or Map&lt;String, Object&gt;</code>. 
     * If single element is returned, expected type is automatically cast by 
     * JsonPath lib, so it is important to know in advance what to expect.  
     * @param json Json formatted string 
     * @param path query string in json-path syntax
     * @return generic type, implicitely cast by json-path lib
     * @see <a href="https://github.com/json-path/JsonPath">json-path</a>
     * @see <a href="http://www.javadoc.io/doc/com.jayway.jsonpath/json-path/2.2.0">javadoc</a>
     * <p>
     * 
     * {talendTypes} Object|String|Integer|Float
     * 
     * {Category} GitHubExamples
     * 
     * {param} String("{\"date_as_long\" : 1411455611975}") input: json
     * 
     * {param} String("$['date_as_long']") input: path
     * 
     * {example}
     * */
    public static <T> T readJson(String json, String path) {
        T result = JsonPath.parse(json).read(path);
        return result;
        
    } //readJson
    
    /**
     * Returns a single element from a json string. User explicitly requests
     * casting of returned result to a known java class.  
     * @param json Json formatted string 
     * @param path query string in json-path syntax
     * @param type java.lang.class reference
     * @see <a href="https://github.com/json-path/JsonPath">json-path</a>
     * @see <a href="http://www.javadoc.io/doc/com.jayway.jsonpath/json-path/2.2.0">javadoc</a>
     * <p>
     * 
     * {talendTypes} Object|String|Integer|Float|Date
     * 
     * {Category} GitHubExamples
     * 
     * {param} String("{\"date_as_long\" : 1411455611975}") input: json
     * 
     * {param} String("$['date_as_long']") input: path
     * 
     * {example} &lt;Date&gt;readJsonExplCast(json, path, Date.class) #2016-05-04
     * */
    public static <T> T readJsonExplCast(String json, String path, java.lang.Class<T> type) {
        T result = JsonPath.parse(json).read(path, type);
        return result;
        
    } //readJsonExplCast
    
    /**
     * Returns a single element or a json sub-block from a json string. 
     * User explicitly requests casting of returned result to a generic type.  
     * @param json Json formatted string 
     * @param path query string in json-path syntax
     * @param type class reference
     * @see <a href="https://github.com/json-path/JsonPath">json-path</a>
     * @see <a href="http://www.javadoc.io/doc/com.jayway.jsonpath/json-path/2.2.0">javadoc</a>
     * <p>
     * 
     * {talendTypes} Object|String|Integer|Float|Date
     * 
     * {Category} GitHubExamples
     * 
     * {param} String("{\"date_as_long\" : [1411455611975, 1411475622985]}") input: json
     * 
     * {param} String("$['date_as_long']") input: path
     * 
     * {example} 
     * &lt;List&lt;Date&gt;&gt;readJsonWithCast(json, "$['date_as_long'][*]", 
     *                              new TypeRef&lt;List&lt;Date&gt;&gt;(){});
     * */
    public static <T> T readJsonGenCast(String json, String path, TypeRef<T> type) {
        Configuration conf = Configuration
                .builder()
                .mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider())
                .build();
        
        T result = JsonPath
                .using(conf)
                .parse(json)
                .read(path, type);
        return result;
        
    } //readJsonGenCast

    /**
     * Wrapper around org.jsoup.Jsoup.parse(String). Used to expose Jsoup lib
     * so that it can be imported in java components of any job/joblet. 
     * @param content HTML formatted string
     * @return DOM object as jsoup Document type
     * @see <a href="https://jsoup.org/apidocs/">jsoup-javadocs</a>
     * <p>
     * 
     * {talendTypes} Object
     * 
     * {Category} GitHubExamples
     * 
     * */
    public static org.jsoup.nodes.Document parseHTML(String content) {
            
            // Wrapper
            return Jsoup.parse(content);
            
    } //parseHTML
    
    /**
     * Wrapper around org.jsoup.Jsoup.parse(String). Used to expose Jsoup lib
     * so that it can be imported in java components of any job/joblet. 
     * @param content URL
     * @param timeoutMillis timeout in milliseconds
     * @return DOM object as jsoup Document type
     * @see <a href="https://jsoup.org/apidocs/">jsoup-javadocs</a>
     * <p>
     * 
     * {talendTypes} Object
     * 
     * {Category} GitHubExamples
     * 
     * */
    public static org.jsoup.nodes.Document parseHTML(URL content, int timeoutMillis) {
            // Wrapper
            try {
                return Jsoup.parse(content, timeoutMillis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            
    } //parseHTML
    
    /**
     * List all links from a given HTML page.
     * @param content HTML formatted string
     * @return array list of all links
     * <p>
     * 
     * {talendTypes} Object
     * 
     * {Category} GitHubExamples
     * */
    public static ArrayList<String> getAllHrefs(String content){
        org.jsoup.nodes.Document doc = Jsoup.parse(content);
        Elements links = doc.select("a[href]");
        ArrayList<String> hrefs = new ArrayList<String>();
        for (Element link : links){
            hrefs.add(link.attr("abs:href"));
        }
        return hrefs;
    }  //getAllHrefs
    
    /**
     * Execute statement in the database. Connection is passed and not closed
     * upon exiting. Main purpose is for DDL statements like: 
     * create, delete from, drop table.
     * @param conn jdbc connection - <code>(Connection) globalMap.get("conn"+ unique_component_name)</code>
     * @param sql sql ddl statement (no result set is expected)
     * <p>
     * 
     * {talendTypes}  
     * 
     * {Category} GitHubExamples
     * 
     * {example} jdbcUpdate((Connection)globalMap.get("conn_tSQLiteConnection_1"), 
     *                      "DELETE FROM TAB1;")
     * */
    public static void jdbcUpdate(Connection conn, String sql) {
        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    } //jdbcUpdate
    
    /**
     * Execute select query in the database. Connection is passed and not closed
     * upon exiting. Used in conjunction with {@link #jdbcUpdate(Connection, String)}, to
     * check the current state of the database prior to some create, delete, drop
     * action. Since intended purpose is to gather small amounts of data, mainly
     * from database's metadata tables (e.g. sqlite_master, etc.) all data in 
     * result set is returned as string type. 
     * @param conn jdbc connection - <code>(Connection) globalMap.get("conn"+ unique_component_name)</code>
     * @param sql sql statement
     * @return array of string arrays of dimension rows x columns in result set.
     * <p>
     * 
     * {talendTypes} Object
     * 
     * {Category} GitHubExamples
     * 
     * {example} jdbcUpdate((Connection)globalMap.get("conn_tSQLiteConnection_1"), 
     *                      "SELECT * FROM SQLITE_MASTER;")
     * */
    public static ArrayList<String[]> jdbcQuery(Connection conn, String sql) {
        ArrayList<String[]> result = new ArrayList<String[]>();
        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int cols = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String[] row = new String[cols];
                for (int i = 0; i < cols; i++) {
                    // jdbc column index starts from 1 
                    row[i] = rs.getString(i+1);
                }
                result.add(row);
            }
            stmt.close();
            
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return result;
    } //jdbcQuery
    
}

