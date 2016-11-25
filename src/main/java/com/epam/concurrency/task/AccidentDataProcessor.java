package com.epam.concurrency.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.concurrency.task.parallel.AccidentDataProcessorCallable;
import com.epam.concurrency.task.parallel.AccidentDataProcessorThread;
import com.epam.data.RoadAccident;

/**
 * Created by Tanmoy on 6/17/2016.
 */
public class AccidentDataProcessor {

    private static final String FILE_PATH_1 = "src/main/resources/DfTRoadSafety_Accidents_2010.csv";
	private static final String FILE_PATH_2 = "src/main/resources/DfTRoadSafety_Accidents_2011.csv";
	private static final String FILE_PATH_3 = "src/main/resources/DfTRoadSafety_Accidents_2012.csv";
	private static final String FILE_PATH_4 = "src/main/resources/DfTRoadSafety_Accidents_2013.csv";

	private static final String OUTPUT_FILE_PATH = "target/DfTRoadSafety_Accidents_consolidated.csv";

	private static final int DATA_PROCESSING_BATCH_SIZE = 10000;

	private AccidentDataReader accidentDataReader = new AccidentDataReader();
	private AccidentDataEnricher accidentDataEnricher = new AccidentDataEnricher();
	private AccidentDataWriter accidentDataWriter = new AccidentDataWriter();

	private List<String> fileQueue = new ArrayList<String>();

	private Logger log = LoggerFactory.getLogger(AccidentDataProcessor.class);

	private static final boolean RUN_PARALLEL = true;
	private static final boolean ENRICH_ASYNC = true; // set TRUE for optional task, otherwise FALSE.
	private static final int THREAD_POOL_SIZE = 5;

	public void init() {
		fileQueue.add(FILE_PATH_1);
		// fileQueue.add(FILE_PATH_2);
		// fileQueue.add(FILE_PATH_3);
		// fileQueue.add(FILE_PATH_4);

		accidentDataEnricher.init(RUN_PARALLEL && ENRICH_ASYNC); // in case run serial and enrich asynchronously.
		accidentDataWriter.init(OUTPUT_FILE_PATH);
	}

	public void process() {
		for (String accidentDataFile : fileQueue) {
			log.info("Starting to process {} file ", accidentDataFile);
			accidentDataReader.init(DATA_PROCESSING_BATCH_SIZE, accidentDataFile);
			if (!RUN_PARALLEL) {
				processFile();
			} else if (!ENRICH_ASYNC) {
				parallelProcessFile();
			} else {
				parallelProcessFileEnrichingAsync();
			}
			log.info("Complete processing {} file ", accidentDataFile);
		}
	}

	private void processFile() {
		int batchCount = 1;
		while (!accidentDataReader.hasFinished()) {
			List<RoadAccident> roadAccidents = accidentDataReader.getNextBatch();
			log.info("Read [{}] records in batch [{}]", roadAccidents.size(), batchCount++);
			List<RoadAccidentDetails> roadAccidentDetailsList = accidentDataEnricher
					.enrichRoadAccidentData(roadAccidents);
			log.info("Enriched records");
			accidentDataWriter.writeAccidentData(roadAccidentDetailsList);
			log.info("Written records");
		}
	}

	private void parallelProcessFile() {

		int batchCount = 1;
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		while (!accidentDataReader.hasFinished()) {
			List<RoadAccident> roadAccidents = accidentDataReader.getNextBatch();
			if (roadAccidents.size() > 0) {
				log.info("Read [{}] records in batch [{}]", roadAccidents.size(), batchCount);
				executor.execute(new AccidentDataProcessorThread(roadAccidents, accidentDataEnricher,
						accidentDataWriter, batchCount));
				batchCount++;
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// wait.
		}
	}

	private void parallelProcessFileEnrichingAsync() {

		int batchCount = 1;
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		List<Future<?>> processFutures = new ArrayList<Future<?>>();
		while (!accidentDataReader.hasFinished()) {
			List<RoadAccident> roadAccidents = accidentDataReader.getNextBatch();
			if (roadAccidents.size() > 0) {
				log.info("Read [{}] records in batch [{}]", roadAccidents.size(), batchCount);
				Future<Void> accidentDataProcessResult = executor.submit(new AccidentDataProcessorCallable(
						roadAccidents, accidentDataEnricher, accidentDataWriter, batchCount));
				processFutures.add(accidentDataProcessResult);
				batchCount++;
			}
		}

		for (Future<?> accidentDataProcessResult : processFutures) {
			try {
				accidentDataProcessResult.get(2, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				log.error("Error enhancing accidents data, force cancelling task. ");
				e.printStackTrace();
				accidentDataProcessResult.cancel(true);
			}
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
			// wait.
		}
	}

	public static void main(String[] args) {
		AccidentDataProcessor dataProcessor = new AccidentDataProcessor();
		long start = System.currentTimeMillis();
		dataProcessor.init();
		dataProcessor.process();
		long end = System.currentTimeMillis();
		System.out.println("Process finished in s : " + (end - start) / 1000);
	}

}
