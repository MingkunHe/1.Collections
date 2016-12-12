package com.epam.concurrency.task;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.epam.data.RoadAccident;

//@RunWith(PowerMockRunner.class)
@RunWith(MockitoJUnitRunner.class)
public class AccidentDataEnricherTest {

	private static final int BATCH_SIZE = 10000;

	private AccidentDataReader reader = new AccidentDataReader();

	private List<RoadAccident> roadAccidents;

	@Mock
	private PoliceForceExternalDataService policeForceSvc;

	@InjectMocks
	private AccidentDataEnricher enricher;

	@Before
	public void setup() {
		reader.init(BATCH_SIZE, "src/main/resources/DfTRoadSafety_Accidents_2010.csv");
		roadAccidents = reader.getNextBatch();
	}

	@Test
	public void shouldSetupProperly() {
		MatcherAssert.assertThat(reader, Matchers.notNullValue());
		MatcherAssert.assertThat(enricher, Matchers.notNullValue());
		MatcherAssert.assertThat(roadAccidents, Matchers.hasSize(BATCH_SIZE));
	}

	@Test
	public void shouldEnrichSynchronously() {

		Mockito.when(policeForceSvc.getContactNoWithoutDelay(Mockito.anyString())).thenReturn("mock_string_0");

		enricher.init(false);

		RoadAccidentDetails roadAccidentDetail = enricher.enrichRoadAccidentDataItem(roadAccidents.get(0));
		MatcherAssert.assertThat(roadAccidentDetail, Matchers.notNullValue());
		MatcherAssert.assertThat(roadAccidentDetail.getPoliceForceContact(), Matchers.equalTo("mock_string_0"));

		List<RoadAccidentDetails> roadAccidentDetails = enricher.enrichRoadAccidentData(roadAccidents);
		MatcherAssert.assertThat(roadAccidentDetails, Matchers.hasSize(BATCH_SIZE));

		Mockito.verify(policeForceSvc, Mockito.times(BATCH_SIZE + 1)).getContactNoWithoutDelay(Mockito.anyString());
		Mockito.verify(policeForceSvc, Mockito.never()).getContactNoWithDelay(Mockito.anyString());
	}

	@Test
	public void shouldEnrichAsynchronously() {

		Mockito.when(policeForceSvc.getContactNoWithDelay(Mockito.anyString())).thenReturn("mock_string_1");

		enricher.init(true);

		RoadAccidentDetails roadAccidentDetail = enricher.enrichRoadAccidentDataItem(roadAccidents.get(0));
		MatcherAssert.assertThat(roadAccidentDetail, Matchers.notNullValue());
		MatcherAssert.assertThat(roadAccidentDetail.getPoliceForceContact(), Matchers.equalTo("mock_string_1"));

		List<RoadAccidentDetails> roadAccidentDetails = enricher.enrichRoadAccidentData(roadAccidents);
		MatcherAssert.assertThat(roadAccidentDetails, Matchers.hasSize(BATCH_SIZE));

		Mockito.verify(policeForceSvc, Mockito.times(BATCH_SIZE + 1)).getContactNoWithDelay(Mockito.anyString());
		Mockito.verify(policeForceSvc, Mockito.never()).getContactNoWithoutDelay(Mockito.anyString());
	}

}
