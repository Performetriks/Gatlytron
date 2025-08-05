package com.performetriks.gatlytron.utils;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.security.cert.X509Certificate;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.Strictness;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**************************************************************************************************************
 * Utility Class copied and adjusted from the CoreFramework project (Class: com.xresch.cfw.utils.json.CFWJson).
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 **************************************************************************************************************/
public class GatlytronJson {
	
	private static final Logger logger = LoggerFactory.getLogger(GatlytronJson.class);
	
	private static Gson gsonInstance;
	private static Gson gsonInstancePretty;
	
	private static Gson gsonInstanceEncrypted;
	//private static Gson prettyTabWriter;
	
	static{
		//Type cfwobjectListType = new TypeToken<LinkedHashMap<CFWObject>>() {}.getType();

		gsonInstance = createGsonBuilderBase()
				.serializeNulls()
				.setStrictness(Strictness.LENIENT)
				.create();
		
		gsonInstancePretty = createGsonBuilderBase()
				.serializeNulls()
				.setStrictness(Strictness.LENIENT)
				.setPrettyPrinting()
				.create();
		
	}
			
	
	private static Gson exposedOnlyInstance = createGsonBuilderBase()
			.excludeFieldsWithoutExposeAnnotation()
			.serializeNulls()
			.setStrictness(Strictness.LENIENT)
			.create();
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	private static GsonBuilder createGsonBuilderBase() {
		return new GsonBuilder();
				//.registerTypeHierarchyAdapter(BigDecimal.class, new SerializerBigDecimal());
	}
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	private static final String escapes[][] = new String[][]{
	        {"\\", "\\\\"},
	        {"\"", "\\\""},
	        {"\n", "\\n"},
	        {"\r", "\\r"},
	        {"\b", "\\b"},
	        {"\f", "\\f"},
	        {"\t", "\\t"}
	};
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static Gson getGsonInstance() {
		return gsonInstance;
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static String toJSON(Object object) {
		return gsonInstance.toJson(object);
	}
		
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static String toJSONPretty(Object object) {
		
		return gsonInstancePretty.toJson(object);
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static String toJSONPrettyDebugOnly(Object object) {
		
		return gsonInstancePretty.toJson(object).replaceAll("  ", "\t");
	}
	
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonElement toJSONElement(Object object) {
		return gsonInstance.toJsonTree(object);
	}
	
	
	/*************************************************************************************
	 * Makes a string from an element, without the nasty quotes.
	 * @return value as string, null will be represented as "null"
	 *************************************************************************************/
	public static String toString(JsonElement element) {
		
		if(element == null || element.isJsonNull()) {
			return "null";
		} else if(element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if(primitive.isString()) {
				return element.getAsString(); 
			}else if(primitive.isNumber()) {
				return element.getAsNumber() + ""; 
			}else if(primitive.isBoolean()) {
				return element.getAsBoolean() + ""; 
			}
		}
	
		return toJSON(element);
	}

	/*************************************************************************************
	 * Takes an array with JsonObjects and converts it to CSV.
	 * If there is anything else in the array that is not a JsonObject it will be ignored.
	 *************************************************************************************/
	public static String toCSV(JsonArray array, String delimiter) {
		
		//-----------------------------------
		// Verify Input
		if(array == null || array.isEmpty()) { return ""; }
		
		StringBuilder csv = new StringBuilder();
		
		//-----------------------------------
		// Get All fieldnames
		LinkedHashSet<String> fieldnames = new LinkedHashSet<>();
		for(JsonElement element : array) {
			
			if(element.isJsonObject()) {
				JsonObject object = element.getAsJsonObject();
				fieldnames.addAll( object.keySet() );
			}
		}
		
		//-----------------------------------
		// Create Headers
		for(String fieldname : fieldnames) {
			csv.append("\"")
			   .append( escapeString(fieldname) )
			   .append("\"")
			   .append(delimiter);
		}
		
		csv.deleteCharAt(csv.length()-1); //remove last delimiter
		csv.append("\r\n");
		
		//-----------------------------------
		// Create Records
		for(JsonElement element : array) {
			
			if(element.isJsonObject()) {
				JsonObject object = element.getAsJsonObject();
				
				for(String fieldname : fieldnames) {
					String value = "";
					
					if(object.has(fieldname)) {
						value = toString( object.get(fieldname) );
					}
					
					csv.append("\"")
					   .append( escapeString(value) )
					   .append("\"")
					   .append(delimiter);
				}
				csv.deleteCharAt(csv.length()-1); //remove last delimiter
				csv.append("\r\n");
			}
		}
		
		
		return csv.toString();
		
	}
	
	/*************************************************************************************
	 * Takes an array with JsonObjects and converts it to XML.
	 * If there is anything else in the array that is not a JsonObject it will be ignored.
	 * @param doPretty TODO
	 *************************************************************************************/
	public static String toXML(JsonArray array, boolean doPretty) {
		
		//-----------------------------------
		// Verify Input
		if(array == null || array.isEmpty()) { return "<data></data>"; }
		

		
		//-----------------------------------
		// Create Records
		String tab = "\t";
		String NL = "\n";
		if( !doPretty ) {
			tab = "";
			NL = "";
		}
		
		//-----------------------------------
		// Create Records
		StringBuilder xml = new StringBuilder();
		xml.append("<data>"+NL);
		
		for(JsonElement element : array) {
			
			if(element.isJsonObject()) {
				JsonObject object = element.getAsJsonObject();
				xml.append(tab+"<record>"+NL);
					for(Entry<String, JsonElement> entry : object.entrySet()) {
						
						String fieldname = entry.getKey();
						String value = toString( entry.getValue() );
						xml.append(tab+tab+"<").append(fieldname).append(">")
							.append(value)
							.append("</").append(fieldname).append(">"+NL)
							;
	
					}
				xml.append(tab+"</record>"+NL);
			}
		}
		
		return xml.toString();
		
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonElement fromJson(String jsonString) {
				
		if(jsonString != null && !jsonString.isBlank() ) {
			
			JsonReader reader = gsonInstance.newJsonReader(new StringReader(jsonString));
			reader.setStrictness(Strictness.LENIENT);
			
			JsonElement jsonElement = JsonParser.parseReader(reader);
			
			return jsonElement;
		}else {
			return JsonNull.INSTANCE;
		}
	}
	
	/*************************************************************************************
	 * Converts a json string to a LinkedHashMap 
	 *************************************************************************************/
	public static LinkedHashMap<String,String> fromJsonLinkedHashMap(String jsonString) {

		Type type = new TypeToken<LinkedHashMap<String,String>>(){}.getType();
		LinkedHashMap<String,String> clonedMap = gsonInstance.fromJson(jsonString, type); 
		return clonedMap;
	}
	
	/*************************************************************************************
	 * Converts a json string to a LinkedHashMap 
	 *************************************************************************************/
	public static LinkedHashMap<String,String> fromJsonLinkedHashMap(JsonObject zeObject) {

		Type type = new TypeToken<LinkedHashMap<String,String>>(){}.getType();
		LinkedHashMap<String,String> zeMap = gsonInstance.fromJson(zeObject, type); 
		return zeMap;
	}
	
	/*************************************************************************************
	 * Converts a HashMap to a JsonArray like [{ value: "value", label: "label"}, ...].
	 * 
	 *************************************************************************************/
	public static JsonArray fromHashMapToJsonArray(HashMap<String,String> map) {

		JsonArray result = new JsonArray();
		for(Entry<String, String> entry : map.entrySet()) {
			JsonObject entryObject = new JsonObject();
			entryObject.addProperty("value", entry.getKey());
			entryObject.addProperty("label", entry.getValue());
			result.add(entryObject);
		}
		
		return result;

	}
	
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static String toJSONExposedOnly(Object object) {
		return exposedOnlyInstance.toJson(object);
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static String escapeString(String string) {

		if(string != null) {
	        for (String[] esc : escapes) {
	            string = string.replace(esc[0], esc[1]);
	        }
		}
        return string;
    }
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonArray arrayToJsonArray(Object[] array) {
		
		JsonArray jsonArray = new JsonArray();
		for(Object o : array) {
			if(o instanceof String) {	

				try {
			        jsonArray.add(Double.parseDouble((String)o));
			    } catch (NumberFormatException e) {
			    	jsonArray.add((String)o);
			    }

			}
			else if(o instanceof Number) 		{	jsonArray.add((Number)o); }
			else if(o instanceof Boolean) 		{	jsonArray.add((Boolean)o); }
			else if(o instanceof Character) 	{	jsonArray.add((Character)o); }
			else if(o instanceof JsonElement) 	{	jsonArray.add((JsonElement)o); }
			else {	
				jsonArray.add(gsonInstance.toJsonTree(o)); 
			}
		}
		
		return jsonArray;
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonArray arrayToJsonArray(ArrayList<?> array) {
		
		return arrayToJsonArray(array.toArray());
	
	}
	
	
		
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonElement objectToJsonElement(Object o) {
		return gsonInstance.toJsonTree(o);
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonElement stringToJsonElement(String jsonString) {
		if(jsonString == null || jsonString.isEmpty()) {
			jsonString = "{}";
		}
		JsonElement result  = new JsonObject();
		try {
			result = new JsonParser().parse(jsonString);
		}catch(Exception e) {
			
			logger.error("Error parsing jsonString: "+jsonString, e);
		}
		return result;
	}
		
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static JsonObject stringToJsonObject(String jsonString) {
		if(jsonString == null || jsonString.isEmpty()) {
			jsonString = "{}";
		}
		JsonObject result  = new JsonObject();
		try {
			result = gsonInstance.fromJson(jsonString, JsonObject.class);
		}catch(Exception e) {
			logger.error("Error parsing jsonString: "+jsonString, e);
		}
		return result;
	}
	
	/*************************************************************************************
	 * Converts every member of the JsonObject into a record in the map
	 *************************************************************************************/
	public static LinkedHashMap<String,String> objectToMap(JsonObject object) {
		
		LinkedHashMap<String,String> map = new  LinkedHashMap<>();
		
		if(object != null) {
			for(Entry<String, JsonElement> entry : object.entrySet()) {
				
				map.put(entry.getKey(), elementValueAsString(entry.getValue()));
			}
			
		}
		return map;
	}
	
	/*************************************************************************************
	 * Gets the Element as a string without these double quotes, or null.
	 *************************************************************************************/
	public static String elementValueAsString(JsonElement element) {
		
		if(element.isJsonNull()) {
			return null;
		}
		
		if(element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			
				 if(primitive.isBoolean()) {	return primitive.getAsBoolean()+""; }
			else if(primitive.isNumber()) {		return primitive.getAsNumber()+""; }
			else  							{	return primitive.getAsString(); }
			
		}
		
		if(element.isJsonObject()) {
			return element.getAsJsonObject().toString();
		}
		
		if(element.isJsonArray()) {
			return element.getAsJsonArray().toString();
		}
		
		return null;
	}
	
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static Object[] jsonToObjectArray(JsonArray jsonArray) {
		return gsonInstance.fromJson(jsonArray, Object[].class);  
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static ArrayList<String> jsonToStringArrayList(JsonArray jsonArray) {
		Type listType = new TypeToken<ArrayList<String>>(){}.getType();
		 
		return gsonInstance.fromJson(jsonArray, listType);  
	}
	
	/*************************************************************************************
	 * 
	 *************************************************************************************/
	public static void addObject(JsonObject target, String propertyName, Object object) {
		if(object instanceof String) 			{	target.addProperty(propertyName, (String)object); }
		else if(object instanceof JsonElement) 	{	target.add(propertyName, (JsonElement)object); }
		else if(object instanceof Number) 		{	target.addProperty(propertyName, (Number)object); }
		else if(object instanceof Boolean) 		{	target.addProperty(propertyName, (Boolean)object); }
		else if(object instanceof Character) 	{	target.addProperty(propertyName, (Character)object); }
		else if(object instanceof Date) 		{	target.addProperty(propertyName, ((Date)object).getTime()); }
		else if(object instanceof Clob) 		{	target.addProperty(propertyName, ((Clob)object).toString()); }
		else if(object instanceof Blob) 		{	target.addProperty(propertyName, ((Blob)object).toString()); }
		else if(object instanceof Timestamp) 	{	target.addProperty(propertyName, ((Timestamp)object).getTime()); }
		else if(object instanceof OffsetDateTime) {	target.addProperty(propertyName, ((OffsetDateTime)object).toInstant().toEpochMilli()); }
		else if(object instanceof Object[]) 	{	target.add(propertyName, arrayToJsonArray((Object[])object)); }
		else if(object instanceof ArrayList) 	{	target.add(propertyName, arrayToJsonArray((ArrayList)object)); }
		else {	
			
			target.add(propertyName, gsonInstance.toJsonTree(object)); 
		}
	}
	
	
	/*************************************************************************************
	 * Copies data from the dataToMerge object to the target object.
	 * @param target the object to copy the data into
	 * @param dataToMerge the object to copy the data from
	 * @param createMissing if true, add the members only present int dataToMerge to target,
	 * 		  if false skip missing members.
	 *************************************************************************************/
	public static void mergeData(JsonObject target, JsonObject dataToMerge, boolean createMissing) {
		
		for(Entry<String, JsonElement> entry : dataToMerge.entrySet()) {
			
			String memberName = entry.getKey();
			
			if(target.has(memberName)) {
				target.add(memberName, dataToMerge.get(memberName));
			}else if(createMissing) {
				target.add(memberName, dataToMerge.get(memberName));
			}
		}
	}
	
	/*************************************************************************************
	 * Makes a HTML table string from a JSON Object.
	 * 
	 * @param thisToTable the object to covnert
	 * @param narrow add the Bootstrap table-sm class to the table if true
	 * 
	 *************************************************************************************/
	public static String formatJsonObjectToHTMLTable(JsonObject thisToTable, boolean narrowTable) {
		
		String narrowClass = "";
		if(narrowTable) { narrowClass = "table-sm"; }
		
		StringBuilder html = new StringBuilder("<table class=\"table table-striped "+narrowClass+"\">");
		html.append("<thead> <th>Key</th>  <th>Value</th> </thead>");
		html.append("<tbody>");
		for(Entry<String, JsonElement> entry : thisToTable.entrySet()) {
			html.append("<tr>")
				.append("<td>").append(entry.getKey()).append("</td>")
				.append("<td>").append(entry.getValue().getAsString()).append("</td>")
				.append("</tr>")
				;
		}
		html.append("</tbody></table>");
		
		return html.toString();
		
	}
	
	/*************************************************************************************
	 * Checks if the element is null or Json Null
	 * 
	 * @return empty string if array is empty
	 *************************************************************************************/
	public static boolean isNull(JsonElement e) {
		return e == null || e.isJsonNull();
	}
		
	/*************************************************************************************
	 * Returns a JSON representation for a certificate.
	 *  
	 *************************************************************************************/
	public static JsonObject certificateX509ToJson(X509Certificate cert) {
		
		//-----------------------
		// Check expired
	    long millisUntilExpiry = cert.getNotAfter().getTime() - System.currentTimeMillis();
	    long daysUntilExpiry = TimeUnit.MILLISECONDS.toDays(millisUntilExpiry);
	    
	    //-----------------------
	    // Check expired
	    JsonObject result = new JsonObject();
	    
	    result.addProperty("Signature Algorithm", cert.getSigAlgName());
	    result.addProperty("Signature Algorithm OID", cert.getSigAlgOID());
	    result.addProperty("Issuer X500 Principal Name", cert.getIssuerX500Principal().getName());
	    result.addProperty("Subject X500 Principal Name", cert.getSubjectX500Principal().getName());
	    result.addProperty("Certificate Type", cert.getType());
	    result.addProperty("Certificate Serial Number", cert.getSerialNumber());
	    result.addProperty("Certificate Version", cert.getVersion());
	    result.addProperty("Certificate Valid From", cert.getNotBefore().getTime());
	    result.addProperty("Certificate Valid Until", cert.getNotAfter().getTime());
	    result.addProperty("Certificate Days Until Expiry", daysUntilExpiry);
	    result.addProperty("Certificate Public Key", cert.getPublicKey().toString());
	
	    return result;
	}
	
	

}
