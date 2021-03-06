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
package dk.teachus.frontend.pages.persons;

import java.util.ArrayList;
import java.util.List;

import dk.teachus.backend.dao.PersonDAO;
import dk.teachus.backend.domain.Admin;
import dk.teachus.backend.domain.Person;
import dk.teachus.backend.domain.Pupil;
import dk.teachus.backend.domain.Teacher;
import dk.teachus.frontend.TeachUsApplication;
import dk.teachus.frontend.TeachUsSession;
import dk.teachus.frontend.UserLevel;
import dk.teachus.frontend.components.list.FunctionItem;
import dk.teachus.frontend.components.list.IconFunctionItem;
import dk.teachus.frontend.models.PupilModel;
import dk.teachus.frontend.pages.calendar.PupilCalendarPage;
import dk.teachus.frontend.pages.messages.SendNewPasswordPage;

public class PupilsPage extends PersonsPage<Pupil> {
	private static final long serialVersionUID = 1L;
	
	public PupilsPage() {
		super(UserLevel.TEACHER);
	}

	@Override
	protected List<Pupil> getPersons() {
		List<Pupil> persons = null;
		
		PersonDAO personDAO = TeachUsApplication.get().getPersonDAO();
		Person person = TeachUsSession.get().getPerson();
		if (person instanceof Teacher) {
			persons = personDAO.getPupils((Teacher) person);
		} else if (person instanceof Admin) {
			persons = personDAO.getPersons(Pupil.class);
		}
			
		return persons;
	}

	@Override
	protected String getNewPersonLabel() {
		return TeachUsSession.get().getString("PupilsPage.newPupil"); //$NON-NLS-1$
	}

	@Override
	protected boolean showNewPersonLink() {
		boolean showNewPersonLink = false;
		
		Person person = TeachUsSession.get().getPerson();
		if (person instanceof Teacher) {
			showNewPersonLink = true;
		}
		
		return showNewPersonLink;
	}
	
	@Override
	protected PupilPage getPersonPage(Long personId) {
		return new PupilPage(new PupilModel(personId));
	}

	@Override
	protected List<FunctionItem> getFunctions() {
		List<FunctionItem> functions = new ArrayList<FunctionItem>();

		functions.add(new IconFunctionItem<Pupil>(TeachUsSession.get().getString("General.calendar"), "calendar") { //$NON-NLS-1$
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(Pupil pupil) {
				getRequestCycle().setResponsePage(new PupilCalendarPage(pupil));
			}
		});
		
		functions.add(new IconFunctionItem<Pupil>(TeachUsSession.get().getString("PupilsPage.sendWelcomeMail"), "key") { //$NON-NLS-1$
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(Pupil person) {				
				getRequestCycle().setResponsePage(new SendNewPasswordPage(person.getId()));
			}
		});
		
		functions.add(new IconFunctionItem<Pupil>(TeachUsSession.get().getString("General.delete"), "remove", "danger") { //$NON-NLS-1$
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(Pupil person) {
				PersonDAO personDAO = TeachUsApplication.get().getPersonDAO();
				personDAO.setInactive(person.getId());
				
				getRequestCycle().setResponsePage(PupilsPage.class);
			}
			
			@Override
			public String getClickConfirmText(Pupil person) {
				String deleteText = TeachUsSession.get().getString("PupilPage.deleteConfirm"); //$NON-NLS-1$
				deleteText = deleteText.replace("{personname}", person.getName()); //$NON-NLS-1$
				return deleteText;
			}
		});

		return functions;
	}

	@Override
	public AuthenticatedPageCategory getPageCategory() {
		return AuthenticatedPageCategory.PUPILS;
	}
	
}
