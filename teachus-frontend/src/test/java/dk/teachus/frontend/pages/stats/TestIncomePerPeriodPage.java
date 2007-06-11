/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.teachus.frontend.pages.stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.joda.time.DateMidnight;

import dk.teachus.backend.dao.BookingDAO;
import dk.teachus.backend.dao.PersonDAO;
import dk.teachus.backend.domain.PupilBooking;
import dk.teachus.backend.domain.Teacher;
import dk.teachus.frontend.pages.stats.teacher.IncomePerPeriodPage;
import dk.teachus.frontend.test.WicketTestCase;

public class TestIncomePerPeriodPage extends WicketTestCase {
	private static final long serialVersionUID = 1L;

	public void testRender() {
		final TeachUsWicketTester tester = createTester();
		
		checking(new Expectations() {{
			PersonDAO personDAO = createPersonDAO();
			BookingDAO bookingDAO = createBookingDAO();
			
			one(personDAO).getPerson(2L);
			Teacher teacher = createTeacher();
			will(returnValue(teacher));
			
			List<Integer> years = new ArrayList<Integer>();
			years.add(2007);
			
			one(bookingDAO).getYearsWithBookings(teacher);
			will(returnValue(years));
			
			Date startDate = new DateMidnight(2007, 1, 1).toDate();
			Date endDate = new DateMidnight(2007, 12, 31).toDate();
			
			List<PupilBooking> bookings = new ArrayList<PupilBooking>();
			PupilBooking pupilBooking = createPupilBooking(1L);
			pupilBooking.setPaid(true);
			bookings.add(pupilBooking);
			
			one(bookingDAO).getPaidBookings(teacher, startDate, endDate);
			will(returnValue(bookings));
			
			bookings = new ArrayList<PupilBooking>();
			pupilBooking = createPupilBooking(2L);
			pupilBooking.setPaid(false);
			bookings.add(pupilBooking);
			
			one(bookingDAO).getUnPaidBookings(teacher, startDate, endDate);
			will(returnValue(bookings));
			
			tester.setPersonDAO(personDAO);
			tester.setBookingDAO(bookingDAO);
		}});
		
		tester.startPage(IncomePerPeriodPage.class);
		
		tester.assertRenderedPage(IncomePerPeriodPage.class);
	}
	
}