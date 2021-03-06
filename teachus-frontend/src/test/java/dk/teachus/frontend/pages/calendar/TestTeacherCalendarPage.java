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
package dk.teachus.frontend.pages.calendar;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.jmock.Expectations;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import dk.teachus.backend.dao.BookingDAO;
import dk.teachus.backend.dao.PeriodDAO;
import dk.teachus.backend.dao.PersonDAO;
import dk.teachus.backend.domain.Booking;
import dk.teachus.backend.domain.Period;
import dk.teachus.backend.domain.Periods;
import dk.teachus.backend.domain.Teacher;
import dk.teachus.backend.domain.TeacherAttribute;
import dk.teachus.backend.domain.impl.BookingsImpl;
import dk.teachus.backend.domain.impl.PeriodImpl.WeekDay;
import dk.teachus.backend.domain.impl.PeriodsImpl;
import dk.teachus.backend.domain.impl.TeacherBookingImpl;
import dk.teachus.frontend.components.calendar.TeacherPeriodsCalendarPanel;
import dk.teachus.frontend.test.WicketTestCase;

public class TestTeacherCalendarPage extends WicketTestCase {
	private static final long serialVersionUID = 1L;

	public void testRender() {
		final TeachUsWicketTester tester = createTester();
		
		checking(new Expectations() {{
			PersonDAO personDAO = createPersonDAO();
			PeriodDAO periodDAO = createPeriodDAO();
			
			one(personDAO).getPerson(2L);
			Teacher teacher = createTeacher(2L);
			will(returnValue(teacher));
			
			one(periodDAO).getPeriods(with(aNonNull(Teacher.class)), with(same(true)));
			will(returnValue(new PeriodsImpl()));
			
			one(personDAO).getAttributes(teacher);
			will(returnValue(new ArrayList<TeacherAttribute>()));
			
			tester.setPersonDAO(personDAO);
			tester.setPeriodDAO(periodDAO);
		}});
		
		tester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new TeacherCalendarPage();
			}			
		});
		
		tester.assertRenderedPage(TeacherCalendarPage.class);
		
		tester.assertComponent("calendar", TeacherPeriodsCalendarPanel.class);
	}
	
	public void testBookTeacher() {
		final TeachUsWicketTester tester = createTester();
		
		checking(new Expectations() {{
			Teacher teacher = createTeacher(2L);
			
			PersonDAO personDAO = createPersonDAO();
			PeriodDAO periodDAO = createPeriodDAO();
			BookingDAO bookingDAO = createBookingDAO();
			
			one(personDAO).getPerson(2L);
			will(returnValue(teacher));
			
			List<Period> periods = new ArrayList<Period>();
			Period period = createPeriod(1L);
			periods.add(period);
			PeriodsImpl periodsImpl = new PeriodsImpl();
			periodsImpl.setPeriods(periods);
			
			exactly(3).of(periodDAO).getPeriods(with(aNonNull(Teacher.class)), with(same(true)));
			will(returnValue(periodsImpl));
			
			exactly(1).of(bookingDAO).getBookings(with(aNonNull(Teacher.class)), with(aNonNull(DateMidnight.class)), with(aNonNull(DateMidnight.class)));
			will(returnValue(new BookingsImpl(new ArrayList<Booking>())));
			
			one(bookingDAO).createTeacherBookingObject();
			will(returnValue(new TeacherBookingImpl()));
			
			one(personDAO).getAttributes(teacher);
			will(returnValue(new ArrayList<TeacherAttribute>()));
			
			one(bookingDAO).book(with(aNonNull(Booking.class)));
			
			exactly(2).of(bookingDAO).getBookings(with(aNonNull(Teacher.class)), with(aNonNull(DateMidnight.class)), with(aNonNull(DateMidnight.class)));
			TeacherBookingImpl teacherBooking = new TeacherBookingImpl();
			teacherBooking.setPeriod(period);
			teacherBooking.setTeacher(teacher);
			teacherBooking.setId(123567L);
			teacherBooking.setActive(true);
			teacherBooking.setCreateDate(new DateTime());
			teacherBooking.setUpdateDate(new DateTime());
			teacherBooking.setDate(new DateTime().withDate(2007, 5, 9).withTime(13, 0, 0, 0));
			List<Booking> bookings = new ArrayList<Booking>();
			bookings.add(teacherBooking);
			will(returnValue(new BookingsImpl(bookings)));
			
			tester.setBookingDAO(bookingDAO);
			tester.setPeriodDAO(periodDAO);
			tester.setPersonDAO(personDAO);
		}});
			
		tester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new TeacherCalendarPage(new DateMidnight(2007, 5, 9));
			}			
		});
		
		tester.assertRenderedPage(TeacherCalendarPage.class);

		String timePath = "calendar:days:2:timeSlots:3";
		
		assertTimeNotSelected(tester, timePath);

		// Book time
		tester.executeAjaxEvent(timePath, "onclick");
		
		// Show the page again to check that it is displaying
		tester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new TeacherCalendarPage(new DateMidnight(2007, 5, 9));
			}			
		});
		
		assertTimeSelected(tester, timePath);		
	}
	
	public void testNavigation() {
		final TeachUsWicketTester tester = createTester();
		
		checking(new Expectations() {{
			
			PersonDAO personDAO = createPersonDAO();
			PeriodDAO periodDAO = createPeriodDAO();
			BookingDAO bookingDAO = createBookingDAO();
			
			one(personDAO).getPerson(2L);
			Teacher teacher = createTeacher(2L);
			will(returnValue(teacher));
			
			one(personDAO).getAttributes(teacher);
			will(returnValue(new ArrayList<TeacherAttribute>()));
			
			Periods periods = new PeriodsImpl();
			Period period = createPeriod();
			periods.addPeriod(period);
			exactly(2).of(periodDAO).getPeriods(with(aNonNull(Teacher.class)), with(same(true)));
			will(returnValue(periods));
			
			one(bookingDAO).getBookings(teacher, new DateMidnight(2007, 11, 5), new DateMidnight(2007, 11, 11));
			will(returnValue(new BookingsImpl(new ArrayList<Booking>())));
			
			one(bookingDAO).getBookings(teacher, new DateMidnight(2007, 10, 29), new DateMidnight(2007, 11, 4));
			will(returnValue(new BookingsImpl(new ArrayList<Booking>())));
			
			tester.setPersonDAO(personDAO);
			tester.setPeriodDAO(periodDAO);
			tester.setBookingDAO(bookingDAO);
		}});
		
		tester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new TeacherCalendarPage(new DateMidnight(2007, 11, 7));
			}
		});
		
		tester.assertRenderedPage(TeacherCalendarPage.class);
		
		tester.clickLink("calendar:previousWeek");

		tester.assertRenderedPage(TeacherCalendarPage.class);
	}
	
	public void testNavigation_fullWeek() {
		final TeachUsWicketTester tester = createTester();
		
		checking(new Expectations() {{
			PersonDAO personDAO = createPersonDAO();
			PeriodDAO periodDAO = createPeriodDAO();
			BookingDAO bookingDAO = createBookingDAO();
			
			one(personDAO).getPerson(2L);
			Teacher teacher = createTeacher(2L);
			will(returnValue(teacher));
			
			one(personDAO).getAttributes(teacher);
			will(returnValue(new ArrayList<TeacherAttribute>()));
			
			Periods periods = new PeriodsImpl();
			Period period = createPeriod();
			period.addWeekDay(WeekDay.TUESDAY);
			period.addWeekDay(WeekDay.THURSDAY);
			period.addWeekDay(WeekDay.SATURDAY);
			period.addWeekDay(WeekDay.SUNDAY);
			periods.addPeriod(period);
			exactly(2).of(periodDAO).getPeriods(with(aNonNull(Teacher.class)), with(same(true)));
			will(returnValue(periods));
			
			one(bookingDAO).getBookings(teacher, new DateMidnight(2007, 11, 5), new DateMidnight(2007, 11, 11));
			will(returnValue(new BookingsImpl(new ArrayList<Booking>())));
			
			one(bookingDAO).getBookings(teacher, new DateMidnight(2007, 10, 29), new DateMidnight(2007, 11, 4));
			will(returnValue(new BookingsImpl(new ArrayList<Booking>())));
			
			tester.setPersonDAO(personDAO);
			tester.setPeriodDAO(periodDAO);
			tester.setBookingDAO(bookingDAO);
		}});
		
		tester.startPage(new ITestPageSource() {
			private static final long serialVersionUID = 1L;

			public Page getTestPage() {
				return new TeacherCalendarPage(new DateMidnight(2007, 11, 7));
			}
		});
		
		tester.assertRenderedPage(TeacherCalendarPage.class);
		
		tester.clickLink("calendar:previousWeek");

		tester.assertRenderedPage(TeacherCalendarPage.class);
	}
}
