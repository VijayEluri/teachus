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
package dk.teachus.frontend.pages.stats.teacher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.DateMidnight;

import dk.teachus.backend.dao.BookingDAO;
import dk.teachus.backend.domain.Pupil;
import dk.teachus.backend.domain.PupilBooking;
import dk.teachus.frontend.TeachUsApplication;
import dk.teachus.frontend.TeachUsSession;
import dk.teachus.frontend.components.list.ListPanel;
import dk.teachus.frontend.components.list.RendererPropertyColumn;
import dk.teachus.frontend.utils.CurrencyChoiceRenderer;
import dk.teachus.frontend.utils.PercentChoiceRenderer;

public class IncomePerPupilPage extends AbstractTeacherStatisticsPage {
	private static final long serialVersionUID = 1L;

	private DateMidnight startDate;
	
	private DateMidnight endDate;
	
	public IncomePerPupilPage() {
		this(null, null);
	}
	
	public IncomePerPupilPage(DateMidnight startDate, DateMidnight endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		
		Form<Void> form = new Form<Void>("form"); //$NON-NLS-1$
		add(form);
		
		form.add(new Label("startDateLabel", TeachUsSession.get().getString("General.startDate"))); //$NON-NLS-1$ //$NON-NLS-2$
		form.add(new DateField("startDate", new PropertyModel<Date>(this, "startDate.date"))); //$NON-NLS-1$
		
		form.add(new Label("endDateLabel", TeachUsSession.get().getString("General.endDate"))); //$NON-NLS-1$ //$NON-NLS-2$
		form.add(new DateField("endDate", new PropertyModel<Date>(this, "endDate.date"))); //$NON-NLS-1$
		
		form.add(new Button("execute", new Model<String>(TeachUsSession.get().getString("IncomePerPupilPage.execute"))) { //$NON-NLS-1$ //$NON-NLS-2$
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				getRequestCycle().setResponsePage(new IncomePerPupilPage(getStartDate(), getEndDate()));
			}
		});
		
		createPercentDistribution();
	}

	private void createPercentDistribution() {
		add(new Label("pctDistribution", TeachUsSession.get().getString("IncomePerPupilPage.percentageDistribution"))); //$NON-NLS-1$ //$NON-NLS-2$
		
		BookingDAO bookingDAO = TeachUsApplication.get().getBookingDAO();
		List<PupilBooking> bookings = bookingDAO.getPaidBookings(getPerson(), startDate, endDate);
		
		// Build the dataset
		final List<PupilSummary> sumList = new ArrayList<PupilSummary>();
		double total = 0;
		for (PupilBooking booking : bookings) {
			PupilSummary pupilSummary = new PupilSummary(booking.getPupil());
			if (sumList.contains(pupilSummary)) {
				pupilSummary = sumList.get(sumList.indexOf(pupilSummary));
			} else {
				sumList.add(pupilSummary);
			}
			
			total += booking.getPeriod().getPrice();
			pupilSummary.addAmount(booking.getPeriod().getPrice());
		}
		for (PupilSummary summary : sumList) {
			summary.calculatePercent(total);
		}
		
		Collections.sort(sumList, new PupilSummaryComparator());
		
		// Sheet
		List<IColumn<PupilSummary>> columns = new ArrayList<IColumn<PupilSummary>>();
		columns.add(new PropertyColumn<PupilSummary>(new Model<String>(TeachUsSession.get().getString("General.pupil")), "pupil.name")); //$NON-NLS-1$ //$NON-NLS-2$
		columns.add(new RendererPropertyColumn<PupilSummary,Object>(new Model<String>(TeachUsSession.get().getString("General.total")), "total", new CurrencyChoiceRenderer())); //$NON-NLS-1$ //$NON-NLS-2$
		columns.add(new RendererPropertyColumn<PupilSummary,Double>(new Model<String>(TeachUsSession.get().getString("General.percent")), "percent", new PercentChoiceRenderer())); //$NON-NLS-1$ //$NON-NLS-2$

		
		ListPanel<PupilSummary> pctDistributionSheet = new ListPanel<PupilSummary>("pctDistributionSheet", columns, sumList);
		add(pctDistributionSheet);
		
		// Chart
		WebComponent chart = new WebComponent("chart") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void renderHead(IHeaderResponse response) {
				response.renderJavaScriptReference("https://www.google.com/jsapi");
				StringBuilder b = new StringBuilder();
				b.append("google.load('visualization', '1', {packages:['corechart']});");
				b.append("google.setOnLoadCallback(drawChart);");
				b.append("function drawChart() {");
				b.append("var data = google.visualization.arrayToDataTable([");
				b.append("['").append(TeachUsSession.get().getString("General.pupil")).append("', ");
				b.append("'").append(TeachUsSession.get().getString("General.total")).append("'],");
				StringBuilder d = new StringBuilder();
				for (PupilSummary pupilSummary : sumList) {
					if (d.length() > 0) {
						d.append(",");
					}
					d.append("[");
					d.append("'").append(pupilSummary.getPupil().getName()).append("', ");
					d.append(pupilSummary.getTotal());
					d.append("]");
				}
				b.append(d);
				b.append("]);");

				b.append("var options = {");
				b.append("};");

				b.append("var chart = new google.visualization.PieChart(document.getElementById('").append(getMarkupId()).append("'));");
				b.append("chart.draw(data, options);");
				b.append("}");
				response.renderJavaScript(b, "chart");
			}
		};
		chart.setOutputMarkupId(true);
		add(chart);
	}

	public DateMidnight getEndDate() {
		return endDate;
	}

	public void setEndDate(DateMidnight endDate) {
		this.endDate = endDate;
	}

	public DateMidnight getStartDate() {
		return startDate;
	}

	public void setStartDate(DateMidnight startDate) {
		this.startDate = startDate;
	}

	static class PupilSummary implements Serializable {
		private static final long serialVersionUID = 1L;

		private double total;

		private Pupil pupil;
		
		private double percent;

		public PupilSummary(Pupil pupil) {
			this.pupil = pupil;
		}

		public Pupil getPupil() {
			return pupil;
		}

		public double getTotal() {
			return total;
		}

		public void setTotal(double total) {
			this.total = total;
		}

		public void addAmount(double amount) {
			total += amount;
		}

		public double getPercent() {
			return percent;
		}
		
		public void calculatePercent(double overAllTotal) {
			percent = total / overAllTotal;
		}

		@Override
		public boolean equals(Object o) {
			boolean equals = false;

			if (this == o) {
				equals = true;
			} else if (o != null) {
				if (o instanceof PupilSummary) {
					PupilSummary pupilSummary = (PupilSummary) o;

					if (pupilSummary.getPupil() == getPupil()) {
						equals = true;
					}
				}
			}

			return equals;
		}
	}
	
	private static class PupilSummaryComparator implements Comparator<PupilSummary> {
		public int compare(PupilSummary o1, PupilSummary o2) {
			int compare = 0;
			
			if (o1 != null && o2 != null) {
				compare = new Double(o2.getTotal()).compareTo(o1.getTotal());
				if (compare == 0) {
					if (o1.getPupil() != null && o2.getPupil() != null) {
						compare = o1.getPupil().getName().compareTo(o2.getPupil().getName());
					} else if (o1.getPupil() != null) {
						compare = -1;
					} else if (o2.getPupil() != null) {
						compare = 1;
					}
				}
			} else if (o1 != null) {
				compare = -1;
			} else if (o2 != null) {
				compare = 1;
			}
			
			return compare;
		}
	}
	
}
