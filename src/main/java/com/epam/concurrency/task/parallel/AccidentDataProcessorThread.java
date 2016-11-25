package com.epam.concurrency.task.parallel;

import java.util.List;

import com.epam.concurrency.task.AccidentDataEnricher;
import com.epam.concurrency.task.AccidentDataWriter;
import com.epam.data.RoadAccident;

public class AccidentDataProcessorThread extends AbstractAccidentDataParallelProcessor implements Runnable {

	public AccidentDataProcessorThread(List<RoadAccident> roadAccidents, AccidentDataEnricher enricher,
			AccidentDataWriter writer, int batchCount) {
		super(roadAccidents, enricher, writer, batchCount);
	}

	@Override
	public void run() {
		super.process();
	}

}
