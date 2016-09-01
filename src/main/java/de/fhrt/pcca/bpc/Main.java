
package de.fhrt.pcca.bpc;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import de.fhrt.pcca.bpc.sockets.Instance;


public class Main {
	public static final Logger logger = LogManager.getLogger("console");
	public static final Logger statsLogger = LogManager.getLogger("stats");
	public static Properties properties = new Properties();

	/**
	 * @Invoker {@link Main} {@link ServerDialog}
	 */
	public static volatile long startTime;
	
	public static void main(String[] args) {
	
		loadPropertiesFile(properties);

		setLevel(logger, String.valueOf(properties.get("debuglevel")));
		
		System.out.println(logger.getLevel());
		long amountOfWorkDone = 0;
		
		logger.info("##### Simple hashbenchmark started #####");

		switch (args[0]) {
		case "hashSeq":
//			logger.info("##### Sequential #####");
//			Main.startTime = System.currentTimeMillis();
//			SequentialHashbenchmark hashbenchmark = new SequentialHashbenchmark(createSolverOutOfProperties(properties));
//			amountOfWorkDone = hashbenchmark.search();
			break;
		case "hashS":
			logger.info("##### Server #####");
			Instance serverInstance = new Instance();
			amountOfWorkDone=serverInstance.start();
			break;
			
		case "hashC":
			logger.info("##### Client #####");
			startTime = System.currentTimeMillis();
			Instance clientInstance=new Instance(args[1]);
			amountOfWorkDone = clientInstance.start();
			break;
			
		case "hashCS":
			logger.info("##### Standby-client #####");
			startTime = System.currentTimeMillis();
			Instance clientStandbyInstance=new Instance(args[1]);
			amountOfWorkDone = clientStandbyInstance.standbyStart();
			break;
//
//		case "shMem":
//			logger.info("##### Shared memory UTS started #####");
//			ShMemSearch shMemSearch = new ShMemSearch(converter);
//			amountOfWorkDone = shMemSearch.search();
//			break;
//
//		case "socketS":
//			logger.info("##### Server socket UTS started #####");
//			SocketSearchServer server = new SocketSearchServer(converter);
//			amountOfWorkDone=server.doSearch();
//			break;
//		case "socketC":
//			logger.info("##### Client socket UTS started #####");
//			SocketSearchClient client = new SocketSearchClient(args[1]);
//			amountOfWorkDone = client.search();
//			break;
//		default:
//			break;
		}

		logger.info("Nodes: " + amountOfWorkDone);
		logger.info("Time: " + (System.currentTimeMillis() - startTime));
	}
	
	private static void loadPropertiesFile(Properties properties) {
		try {
			properties.load(ClassLoader.getSystemResourceAsStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	public static void setLevel(Logger log, String level) {
		  LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
		  Configuration conf = ctx.getConfiguration();
		  LoggerConfig lconf = conf.getLoggerConfig(log.getName());
		  lconf.setLevel(Level.toLevel(level));
		  ctx.updateLoggers(conf);
		}
}

