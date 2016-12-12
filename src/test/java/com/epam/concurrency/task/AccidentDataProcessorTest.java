package com.epam.concurrency.task;

//import java.time.LocalDate;
//import java.time.LocalTime;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.epam.data.RoadAccident;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(AccidentDataEnricher.class)
@RunWith(MockitoJUnitRunner.class)
public class AccidentDataProcessorTest {

	@Mock
	private AccidentDataEnricher enricher;

	/*
	 * do not need to mock this, but had strange thread-blocking-issue which does not happen in a normal run (only in
	 * junit test...), so mock it to avoid this issue...
	 */
	@Mock
	private AccidentDataWriter writer;

	@InjectMocks
	private AccidentDataProcessor processor;

	@Before
	public void setup() {

		processor.init();

		RoadAccidentDetails mockedAccidentDtl = Mockito.mock(RoadAccidentDetails.class);
//		LocalDate nowDate = LocalDate.now();
//		LocalTime nowTime = LocalTime.now();
//		Mockito.when(mockedAccidentDtl.getDate()).thenReturn(nowDate);
//		Mockito.when(mockedAccidentDtl.getTime()).thenReturn(nowTime);

		Mockito.when(enricher.enrichRoadAccidentData(Mockito.any())).thenCallRealMethod();
		Mockito.when(enricher.enrichRoadAccidentDataItem(Mockito.any(RoadAccident.class)))
				.thenReturn(mockedAccidentDtl);
	}

	@After
	public void teardown() {
		processor.destroy();
	}

	@Test
	public void shouldSetupProperly() {
		MatcherAssert.assertThat(processor, Matchers.notNullValue());
		MatcherAssert.assertThat(enricher, Matchers.notNullValue());
	}

	@Test
	public void shouldProcessSerial() {

		processor.setParallelRun(false);
		processor.setAsynchronouslyEnrich(false);
		processor.process();

		Mockito.verify(enricher, Mockito.atLeastOnce()).enrichRoadAccidentData(Mockito.any());
		Mockito.verify(enricher, Mockito.atLeastOnce()).enrichRoadAccidentDataItem(Mockito.any());
	}
	
	@Test
	public void shouldProcessParallel() {

		processor.setParallelRun(true);
		processor.setAsynchronouslyEnrich(false);
		processor.process();

		Mockito.verify(enricher, Mockito.atLeastOnce()).enrichRoadAccidentData(Mockito.any());
		Mockito.verify(enricher, Mockito.atLeastOnce()).enrichRoadAccidentDataItem(Mockito.any());
	}
	
	@Test
	public void shouldProcessParallelEnrichAsync() {

		processor.setParallelRun(true);
		processor.setAsynchronouslyEnrich(true);
		processor.process();

		Mockito.verify(enricher, Mockito.atLeastOnce()).enrichRoadAccidentData(Mockito.any());
		Mockito.verify(enricher, Mockito.atLeastOnce()).enrichRoadAccidentDataItem(Mockito.any());
	}

}
