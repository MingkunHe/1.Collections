package com.epam.concurrency.task.parallel;

import java.util.List;
import java.util.concurrent.Callable;

import com.epam.concurrency.task.AccidentDataEnricher;
import com.epam.concurrency.task.AccidentDataWriter;
import com.epam.data.RoadAccident;

public class AccidentDataProcessorCallable extends AbstractAccidentDataParallelProcessor implements Callable<Void> {

	public AccidentDataProcessorCallable(List<RoadAccident> roadAccidents, AccidentDataEnricher enricher,
			AccidentDataWriter writer, int batchCount) {
		super(roadAccidents, enricher, writer, batchCount);
	}

	@Override
	public Void call() throws Exception {
		super.process();
		return null;
	}

}
