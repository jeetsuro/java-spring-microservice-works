package com.surajit.spring.jenkins;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.ApplicationArguments;
import javax.annotation.PostConstruct;

// part of spring-boot-starter-web module >
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.*;
import java.io.File;
import java.io.IOException;

import com.surajit.spring.jenkins.utilities.*;

@SpringBootApplication
@RestController
@RequestMapping("/hello-jenkins")

// Build & deploy steps

// mvn clean package -Dspring.profiles.active=dev -Dmy.json.config=customers.json -Dmaven.test.skip=true

// java -jar target\hello-jenkins-ci-dev.jar --welcome.salutation=Holo000,yoyo --spring.profiles.active=DEVELOPMENT --my.json.config=customers.json

// http://localhost:8080/hello-jenkins/hello

/**
 * External Properties test/ properties to be supplied with jar for spring-boot version2.x  @start-up
 * 
 * Takes user-defined external command parameters & also takes external configuration file and work upon them
 * 
 * @author esurpau
 *
 */
public class HelloJenkinsCiApplication implements ApplicationRunner {

	// @Value("${spring.profiles.active : Not-found!!}")
	@Value("${spring.profiles.active}")
	private String profileValue;

	//@Value("${welcome.salutation : Hore-Krishno}")
	@Value("${welcome.salutation}")
	private String replyString;

	private static ConfigurableApplicationContext applicationContext;

	/*
	 * Note - It is bit risky to access variable which having dependency on spring context, because 
	 * at the time of 'constructor' called, those variable might not be properly initialized.
	 * So @PostConstruct is the best place to have all of this kind variable initialization
	 */
	public HelloJenkinsCiApplication(@Value("${my.json.config}") String config) {

		System.out.println("Under HelloJenkinsCiApplication constructor, setting value..");

		if (config != null && !config.isEmpty())
			System.setProperty("my-json.config", config);
	}

	public static void main(String[] args) {

		System.out.println("APP started on main() :::: " + new Date());

		try {
			
			HelloJenkinsCiApplication.applicationContext = SpringApplication.run(HelloJenkinsCiApplication.class, args);
			System.out.println("SUCCESS  : " + applicationContext.getApplicationName() + " App is UP & Running...");
		} catch (Exception ex) {
			
			ex.printStackTrace();
			System.out.println("Exception happened : " + ex.getMessage());
			System.out.println("App going to shut-down...");
			SpringApplication.exit(applicationContext, () -> -1111);
		}
		
		System.out.println("APP_NAME : " + applicationContext.getApplicationName());
		System.out.println("APP_NAME_STATUS : " + applicationContext.isRunning());
		System.out.println("APP completed on main() ::::: " + new Date());
	}

	@PostConstruct
	/**
	 * 
	 * In Spring-Boot all the REST end-points are actualized through
	 * RequestMappingHandlerMapping class. And this REST end-point, '/actuator
	 * kicks-off in after @PostContruct.
	 * 
	 */
	public void startUp() throws Exception {

		System.out.println("Under startUp json config file path given : " + System.getProperty("my-json.config"));
		boolean anyIssue=false;
		String errormsg="";
		
		try {
			System.out.println("Waiting for 60 secs..No REST call will be allowed in next 60 secs..");
			Thread.sleep(1000 * 6);
			System.out.print("spring.profiles.active found to  -> " + profileValue + "\n");
			
			// Special check when active-profile is 'Devlopment' or 'Dev' or 'dev'
			if (profileValue != null && !profileValue.isEmpty() && profileValue.toLowerCase().startsWith("d")) {
				
				System.setProperty("profile-value", "d" );
				if (System.getProperty("my-json.config")!=null) {
					
					if ( Files.exists(Paths.get(System.getProperty("my-json.config")))) {
						
						// Parsing Logic
						System.out.println( " Parsing logic to be implementd on CommandListener...");
					} else {
						
						anyIssue=true;
						System.out.println(" Given json config file : " + System.getProperty("my-json.config") + " Not present in system..");
						throw new Exception (" Given json config file : " + System.getProperty("my-json.config") + " Not present in system..");
					}
				} else {
					
					System.out.println(" Provided active-profile is : " + profileValue + " , my.json.config can't be NULL");
				}
			}
		} catch (Exception ex) {
			
			anyIssue=true;
			errormsg=ex.getMessage();
			System.out.println(" Application to be Shut-down due to " + errormsg);
			ex.printStackTrace();
		} finally {
			
			if (anyIssue) {
				
				// Shut-down policy
				SpringApplication.exit(HelloJenkinsCiApplication.applicationContext, () -> { 
					
					System.out.println(" Shut-down due to input file not present..." );
					return -2222;
				});
			}
		}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		System.out.println("ApplicationRunner started with command-line arguments: " + Arrays.toString(args.getSourceArgs()));
		System.out.println("ApplicationRunner NonOptionArgs: " + args.getNonOptionArgs());
		System.out.println(" ApplicationRunner OptionNames: " + args.getOptionNames());

		for (String name : args.getOptionNames()) {
			System.out.println("arg--->" + name + "=" + args.getOptionValues(name));
		}

		if (args.containsOption("welcome.salutation")) {
			
			System.out.println("welcome.salutation" + "=" + args.getOptionValues("welcome.salutation"));
			
			if (args.getOptionValues("welcome.salutation").get(0).equals("DEVE")) {
				System.out.println("welcome.salutation" + "=" + "%%%%%%%%%%%");
			}
		}
	}
	
	// http://www.masterspringboot.com/web/rest-services/parsing-json-in-spring-boot-using-jsonparser/
	// https://attacomsian.com/blog/processing-json-spring-boot
	  @Bean
	  public CommandLineRunner myCommandLineRunner1(ApplicationContext ctx) {
		  
		return args -> {
			
			// This command is only valid when profile is 'development'	
			if( !System.getProperty("profile-value").isEmpty() &&  System.getProperty("profile-value").equals("d")) {
				
				System.out.println("\n\n ---- Under CommandLineRunner1.. ");
				System.out.println("\n\n ---- WAY-1 .. ");
				// --------- WAY : 1 
				// JSON paring from .json file
				
		        //create ObjectMapper instance
		        ObjectMapper objectMapper = new ObjectMapper();
		        Customer[] customerArr;
		        List<Customer> customerList=null;

			    //read json file and convert to customer object
		        try {
		        	customerArr =  objectMapper.readValue( new File(System.getProperty("my-json.config")),  Customer[].class );
		        	customerList = (List<Customer>) Arrays.asList(customerArr);
		        } catch (Exception ex) {
		        	
		        	System.out.println("Excption on CommandLineRunner1 - WAY1 errorMessage : " + ex.getMessage());
		        	ex.printStackTrace();
		        	throw ex;
		        }
		        
		        System.out.println("Your config-file details parsed successfully, Obj are at below.. \n");
		        customerList.stream().forEach(x -> { 
		        	System.out.println(x); 
		        	System.out.println("-------------------------");
		        });
		        
				//--------- WAY : 2
				// JSON paring from json string
		          System.out.println("\n\n ---- WAY-2.1");
			      String url = "https://jsonplaceholder.typicode.com/todos/1"; 
		          System.out.println("Json parsing from single-obj json url  : " + url);

			      RestTemplate restTemplate = new RestTemplate();
			      String resp = "";
			      JsonParser springParser = JsonParserFactory.getJsonParser();
			      Map < String, Object > map = null;
			      String mapArray[] =null;
			      resp = restTemplate.getForObject(url, String.class);
			      
			      try {
			    	  map = springParser.parseMap(resp);
			    	  mapArray= new String[map.size()];
			      } catch (Exception ex) {
			    	  
			    	  System.out.println("Excption on CommandLineRunner1 - WAY2-1 errorMessage : " + ex.getMessage());
			    	  ex.printStackTrace();
			    	  throw ex;
			      }
			      System.out.println("Items found: " + mapArray.length);
			      
			      int i = 0;
			      for (Map.Entry < String, Object > entry: map.entrySet()) {
			        System.out.println(entry.getKey() + " = " + entry.getValue());
			        i++;
			      }
			      
		          System.out.println("\n\n ---- WAY-2.2 ");
			      String url2 = "https://jsonplaceholder.typicode.com/todos"; 
		          System.out.println("Json parsing from multi-0bj json url  : " + url2);
		          Todo[] returnTodosArray = null;
		          
		          try {
		        	  
		        	  returnTodosArray = restTemplate.getForObject(url2, Todo[].class);
		  		    	List<Todo> todosActual=null;
		  		    	
		  		    	if (returnTodosArray!=null) {
		  		    		todosActual= Arrays.asList(returnTodosArray);
		  		    		todosActual.stream().forEach(x -> { System.out.println(x) ; System.out.println("##############");} );
		  		    	}
		          } catch (Exception ex) {
		        	  
			    	  System.out.println("Excption on CommandLineRunner1 - WAY2-2 errorMessage : " + ex.getMessage());
			    	  ex.printStackTrace();
			    	  throw ex;
		          }
			}
		};
	  }

	  /* 
	   * 
	  @Autowired
	  CommandLineRunner myCommandLineRunner2 = args -> {
		  
		  System.out.println("Under CommandLineRunner2..");
		  
	      String url = "https://jsonplaceholder.typicode.com/todos/1";
	      
	      RestTemplate restTemplate = new RestTemplate();
	      String resp = restTemplate.getForObject(url, String.class);
	      JsonParser springParser = JsonParserFactory.getJsonParser();
	      Map < String, Object > map = springParser.parseMap(resp);
	      String mapArray[] = new String[map.size()];
	      
	      System.out.println("Items found: " + mapArray.length);
	      
	      int i = 0;
	      for (Map.Entry < String, Object > entry: map.entrySet()) {
	        System.out.println(entry.getKey() + " = " + entry.getValue());
	        i++;
	      }
	    
	  };
	  */

	  ////////
	  ////////  -------------- REST controller -----------
	  ////////
	@GetMapping("/hello")
	public ResponseEntity<String> hello() {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		StringBuilder sb = new StringBuilder();
		sb.append("-- Welcome : " + replyString).append("\n\n").append ("Jenkins running under profile :" + profileValue).append("\n\n").append(new Date().toString() );
		//return ResponseEntity.ok().headers(headers).body( replyString + ", Jenkins running under profile :  "
				//+ profileValue + "\n Date-time : " + new Date().toString() );
		return ResponseEntity.ok().headers(headers).body( sb.toString());
	}
}
