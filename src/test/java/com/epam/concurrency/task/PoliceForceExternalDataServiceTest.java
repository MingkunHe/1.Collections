package com.epam.concurrency.task;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PoliceForceExternalDataServiceTest {

	private PoliceForceExternalDataService svc = new PoliceForceExternalDataService();

	@Test
	public void shouldGetContactWithoutDelay() {
		String contact = svc.getContactNoWithoutDelay("Metropolitan Police");
		MatcherAssert.assertThat(contact, Matchers.startsWith("13163862"));
	}

	@Test
	public void shouldGetContactWithDelay() {
		for (int i = 0; i < 500; i++) {
			svc.getContactNoWithDelay("Metropolitan Police");
		}
		System.out.println("Testing: PoliceForceExternalDataService, HALT_FOR_TO_TEST_THREAD_HANDLING is triggered...");
		System.out.println("Let's wait 10 minutes before it's done...");

		String contact = svc.getContactNoWithDelay("Metropolitan Police");
		MatcherAssert.assertThat(contact, Matchers.startsWith("13163862"));
	}

}
