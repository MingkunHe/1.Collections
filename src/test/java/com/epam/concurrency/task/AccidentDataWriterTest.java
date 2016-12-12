package com.epam.concurrency.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.epam.data.RoadAccident;
import com.epam.data.RoadAccidentBuilder;

@RunWith(JUnit4.class)
public class AccidentDataWriterTest {

	private AccidentDataWriter writer = new AccidentDataWriter();

	@Test
	public void shouldWwriteAccidentData() {

		String filePath = "target/DfTRoadSafety_Accidents_consolidated.csv";
		writer.init(filePath);
		File dataFile = new File(filePath);
		MatcherAssert.assertThat(dataFile.exists(), Matchers.is(true));
		MatcherAssert.assertThat(dataFile.length(), Matchers.equalTo(0l));

		RoadAccidentBuilder roadAccidentBuilder = new RoadAccidentBuilder("200901BS70001");
		RoadAccident roadAccident = roadAccidentBuilder.withAccidentSeverity("1").build();
		List<RoadAccidentDetails> accidentDetailsList = new ArrayList<>(1);
		accidentDetailsList.add(new RoadAccidentDetails(roadAccident));
		writer.writeAccidentData(accidentDetailsList);
		MatcherAssert.assertThat(dataFile.length(), Matchers.greaterThan(0l));

		writer.close();

	}

}
