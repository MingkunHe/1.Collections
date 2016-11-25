package com.epam.concurrency.task.parallel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.concurrency.task.AccidentDataEnricher;
import com.epam.concurrency.task.AccidentDataWriter;
import com.epam.concurrency.task.RoadAccidentDetails;
import com.epam.data.RoadAccident;

public class AbstractAccidentDataParallelProcessor {

	private Logger log = LoggerFactory.getLogger(AbstractAccidentDataParallelProcessor.class);

	private List<RoadAccident> roadAccidents;
	private AccidentDataEnricher enricher;
	private AccidentDataWriter writer;
	private int batchCount;

	public AbstractAccidentDataParallelProcessor(List<RoadAccident> roadAccidents, AccidentDataEnricher enricher,
			AccidentDataWriter writer, int batchCount) {

		this.roadAccidents = roadAccidents;
		this.enricher = enricher;
		this.writer = writer;
		this.batchCount = batchCount;
	}

	void process() {
		List<RoadAccidentDetails> roadAccidentDetailsList = enricher.enrichRoadAccidentData(roadAccidents);
		log.info(String.format("AccidentDataParallelProcessor-[%d] Enriched records", batchCount));
		writer.writeAccidentData(roadAccidentDetailsList);
		log.info(String.format("AccidentDataParallelProcessor-[%d] Written records", batchCount));
	}

}
