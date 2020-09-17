package eu.wdaqua.qanary.relationlinker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.*;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import eu.wdaqua.qanary.commons.QanaryMessage;
import eu.wdaqua.qanary.commons.QanaryQuestion;
import eu.wdaqua.qanary.commons.QanaryUtils;
import eu.wdaqua.qanary.component.QanaryComponent;


@Component
/**
 * This component connected automatically to the Qanary pipeline.
 * The Qanary pipeline endpoint defined in application.properties (spring.boot.admin.url)
 * @see <a href="https://github.com/WDAqua/Qanary/wiki/How-do-I-integrate-a-new-component-in-Qanary%3F" target="_top">Github wiki howto</a>
 */
public class RelationLinker1 extends QanaryComponent {
	private static final Logger logger = LoggerFactory.getLogger(RelationLinker1.class);

	private final String applicationName;

	public RelationLinker1(@Value("${spring.application.name}") final String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * implement this method encapsulating the functionality of your Qanary
	 * component
	 * @throws Exception 
	 */
	@Override
	public QanaryMessage process(QanaryMessage myQanaryMessage) throws Exception {
		HashSet<String> dbLinkListSet = new HashSet<>();
		logger.info("process: {}", myQanaryMessage);
		// TODO: implement processing of question
		QanaryUtils myQanaryUtils = this.getUtils(myQanaryMessage);
		  QanaryQuestion<String> myQanaryQuestion = new QanaryQuestion(myQanaryMessage);
	      String myQuestion = myQanaryQuestion.getTextualRepresentation();
	      ArrayList<Link> links = new ArrayList<Link>();

	        logger.info("Question: {}", myQuestion);
	        int flag = 0;
	        
//		    try {
//				File f = new File("evaluation-utilities/QANARY-QA-Components/qanary_component-REL-RelationLinker1/src/main/resources/questions.txt");
//		    	FileReader fr = new FileReader(f);
//		    	BufferedReader br  = new BufferedReader(fr);
//				int flag = 0;
//				String line;
////				Object obj = parser.parse(new FileReader("DandelionNER.json"));
////				JSONObject jsonObject = (JSONObject) obj;
////				Iterator<?> keys = jsonObject.keys();
//
//				while((line = br.readLine()) != null && flag == 0) {
//				    String question = line.substring(0, line.indexOf("Answer:"));
//					logger.info("{}", line);
//					logger.info("{}", myQuestion);
//
//				    if(question.trim().equals(myQuestion))
//				    {
//				    	String Answer = line.substring(line.indexOf("Answer:")+"Answer:".length());
//				    	logger.info("Here {}", Answer);
//				    	Answer = Answer.trim();
//				    	JSONArray jsonArr =new JSONArray(Answer);
//				    	if(jsonArr.length()!=0)
//		 	        	   {
//		 	        		   for (int i = 0; i < jsonArr.length(); i++)
//		 	        		   {
//		 	        			   JSONObject explrObject = jsonArr.getJSONObject(i);
//
//		 	        			   logger.info("Question: {}", explrObject);
//
//		 	        			  Link l = new Link();
//			    	                l.begin = (int) explrObject.get("begin");
//			    	                l.end = (int) explrObject.get("end");
//			    	                l.link= explrObject.getString("link");
//			    	                links.add(l);
//			            		}
//			            	}
//				    	flag=1;
//
//				    	break;
//				    }
//
//
//				}
//				br.close();
				if(flag==0)
				{

	        //STEP2
	        HttpClient httpclient = HttpClients.createDefault();
	        HttpPost httppost = new HttpPost("https://labs.tib.eu/falcon/api?mode=long");
	        httppost.addHeader("Accept", "application/json");

			// Request parameters and other properties.
			//List<NameValuePair> params = new ArrayList<NameValuePair>();
			//params.add(new BasicNameValuePair("text", ));
			//httppost.addHeader("Content-Type", "application/json");

			httppost.setEntity(new StringEntity(new JSONObject().put("text", myQuestion).toString(), ContentType.APPLICATION_JSON));
			//httppost.setEntity(new StringEntity(myQuestion));
	        try {
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        if (entity != null) {
	        	InputStream instream = entity.getContent();

				String text2 = IOUtils.toString(instream, StandardCharsets.UTF_8.name());
				logger.info("text2: {}", text2);
				//String text = text2.substring(text2.indexOf('{'));
				//logger.info("Question text: {}", text);
				//JSONObject jsonObject = new JSONObject(text);
				JSONArray relations_list = (new JSONObject(text2)).getJSONArray("relations");
//				Iterator<JsonElement> it = relations_list.iterator();
//				while(it.hasNext()){
//					//System.out.println(it.next());
//					ArrayList<String> list = it.next();
//				}

				//List<String> reln_list = new ArrayList<>();
				//JSONParser parse = new JSONParser();
				//JSONObject jobj = (JSONObject)parse.parse(instream);
				//JSONArray relations_list = (JSONArray) jobj.get("relations");
				logger.info("relations_list: {}", relations_list);
				ArrayList<String> list = new ArrayList<String>();
				logger.info("relations_list type: {}", relations_list.getClass().getName());
				for(int i = 0; i < relations_list.length(); i++){
					//JSONObject o = (JSONObject) relations_list.get(i);
					JSONArray relation_element = (JSONArray) relations_list.get(i); // you will get the json object
					//list.add(relations_list.get(i).toString());
					//logger.info("list: {}", list);
					// pick the first element as it is the link and add it to the hashset
					dbLinkListSet.add(relation_element.get(0).toString());
				}

				//links = relations_list;
	           // String result = getStringFromInputStream(instream);
	            //String text2 = IOUtils.toString(instream, StandardCharsets.UTF_8.name());
	            //logger.info("text2: {}", text2);
//	            Link l = new Link();
//	                l.begin = myQuestion.indexOf(list.get(0));
//	                l.end = l.begin + list.get(0).length();
//	                l.link = test;
//	                links.add(l);
//	            for (int i = 0; i < jsonArray.length(); i++) {
//	                JSONObject explrObject = jsonArray.getJSONObject(i);
//	                int begin = (int) explrObject.get("startOffset");
//	                int end = (int) explrObject.get("endOffset");
//	                if(explrObject.has("features"))
//	                {
//	                	JSONObject features =(JSONObject) explrObject.get("features");
//	                	if(features.has("exactMatch"))
//	                	{
//	                		JSONArray uri = features.getJSONArray("exactMatch");
//	                		String uriLink =  uri.getString(0);
//	                		logger.info("Question: {}", explrObject);
//	    	                logger.info("Question: {}", begin);
//	    	                logger.info("Question: {}", end);
//	                		Link l = new Link();
//	     	                l.begin = begin;
//	     	                l.end = end;
//	     	                l.link = uriLink;
//	     	                links.add(l);
//	                	}
//	                }
//
//
	           // }
//	            JSONObject jsnobject = new JSONObject(text);
//	            JSONArray jsonArray = jsnobject.getJSONArray("endOffset");
//	            for (int i = 0; i < jsonArray.length(); i++) {
//	                JSONObject explrObject = jsonArray.getJSONObject(i);
//	                logger.info("JSONObject: {}", explrObject);
//	                logger.info("JSONArray: {}", jsonArray.getJSONObject(i));
//	                //logger.info("Question: {}", text);
//
//	        }
	            logger.info("Question: {}", myQuestion);
	            //logger.info("Question: {}", jsonArray);
	            try {
	                // do something useful
	            } finally {
	                instream.close();
	            }
	        }

	        //BufferedWriter buffWriter = new BufferedWriter(new FileWriter("evaluation-utilities/QANARY-QA-Components/qanary_component-REL-RelationLinker1/src/main/resources/questions.txt", true));
	        Gson gson = new Gson();

	        String json = gson.toJson(links);
	        logger.info("gsonwala: {}",json);
	        logger.info("DbLinkListSet: "+dbLinkListSet.toString());

	        String MainString = myQuestion + " Answer: "+json;
	        //buffWriter.append(MainString);
	        //buffWriter.newLine();
	        //buffWriter.close();
	        }
		 catch (ClientProtocolException e) {
			 logger.info("Exception: {}", myQuestion);
	        // TODO Auto-generated catch block
	    } catch (IOException e1) {
	    	logger.info("Except: {}", e1);
	        // TODO Auto-generated catch block
	    }
				}
//		    }
//		    catch(IOException e)
//			{
//			    //handle this
//				logger.info("{}", e);
//			}
		logger.info("store data in graph {}", myQanaryMessage.getValues().get(myQanaryMessage.getEndpoint()));
		// TODO: insert data in QanaryMessage.outgraph

		logger.info("apply vocabulary alignment on outgraph {}", myQanaryMessage.getOutGraph());

		// for all URLs found using the called API
		for (String urls : dbLinkListSet) {
			String sparql = "prefix qa: <http://www.wdaqua.eu/qa#> "
					+ "prefix oa: <http://www.w3.org/ns/openannotation/core/> "
					+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#> "
					+ "prefix dbp: <http://dbpedia.org/property/> "
					+ "INSERT { "
					+ "GRAPH <" +  myQanaryQuestion.getOutGraph()  + "> { "
					+ "  ?a a qa:AnnotationOfRelation . "
					+ "  ?a oa:hasTarget [ "
					+ "           a    oa:SpecificResource; "
					+ "           oa:hasSource    <" + myQanaryQuestion.getUri() + ">; "
					+ "  ] ; "
					+ "     oa:hasBody <" + urls + "> ;"
					+ "     oa:annotatedBy <urn:qanary:"+this.applicationName+"> ; "
					+ "	    oa:annotatedAt ?time  "
					+ "}} "
					+ "WHERE { "
					+ "BIND (IRI(str(RAND())) AS ?a) ."
					+ "BIND (now() as ?time) "
					+ "}";
			logger.info("Sparql query {}", sparql);
			logger.info("myQanaryQuestion.getEndpoint().toString(): ", myQanaryQuestion.getEndpoint().toString());
			myQanaryUtils.updateTripleStore(sparql, myQanaryQuestion.getEndpoint().toString());

//		logger.info("apply vocabulary alignment on outgraph");
//		// TODO: implement this (custom for every component)
//		for (Link l : links) {
//		 String sparql = "prefix qa: <http://www.wdaqua.eu/qa#> "
//                 + "prefix oa: <http://www.w3.org/ns/openannotation/core/> "
//                 + "prefix xsd: <http://www.w3.org/2001/XMLSchema#> "
//                 + "prefix dbp: <http://dbpedia.org/property/> "
//                 + "INSERT { "
//                 + "GRAPH <" +  myQanaryQuestion.getOutGraph()  + "> { "
//                 + "  ?a a qa:AnnotationOfRelation . "
//                 + "  ?a oa:hasTarget [ "
//                 + "           a    oa:SpecificResource; "
//                 + "           oa:hasSource    <" + myQanaryQuestion.getUri() + ">; "
//                 + "              oa:start \"" + l.begin + "\"^^xsd:nonNegativeInteger ; " //
//                 + "              oa:end  \"" + l.end + "\"^^xsd:nonNegativeInteger  " //
//                 + "  ] ; "
//                 + "     oa:hasBody <" + l.link + "> ;"
//                 + "     oa:annotatedBy <urn:qanary:"+this.applicationName+"> ; "
//                 + "	    oa:annotatedAt ?time  "
//                 + "}} "
//                 + "WHERE { "
//                 + "BIND (IRI(str(RAND())) AS ?a) ."
//                 + "BIND (now() as ?time) "
//                 + "}";
//         logger.info("Sparql query {}", sparql);
//         logger.info("myQanaryQuestion.getEndpoint().toString(): ", myQanaryQuestion.getEndpoint().toString());
//         myQanaryUtils.updateTripleStore(sparql, myQanaryQuestion.getEndpoint().toString());

		}
 
		return myQanaryMessage;
	}
	class Link {
        public int begin;
        public int end;
        public String link;
    }
	
}
