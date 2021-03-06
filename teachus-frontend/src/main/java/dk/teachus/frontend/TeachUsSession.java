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
package dk.teachus.frontend;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.cookies.CookieUtils;
import org.apache.wicket.util.string.Strings;

import dk.teachus.backend.dao.PersonDAO;
import dk.teachus.backend.domain.Admin;
import dk.teachus.backend.domain.Person;
import dk.teachus.backend.domain.Pupil;
import dk.teachus.backend.domain.Teacher;
import dk.teachus.backend.domain.TeacherAttribute;
import dk.teachus.utils.ClassUtils;

public class TeachUsSession extends WebSession {
	private static final long serialVersionUID = 1L;
	private static final String BUNDLE_NAME = ClassUtils.getAsResourceBundlePath(TeachUsSession.class, "messages"); //$NON-NLS-1$
	
	protected Person person;
	private Properties resourceBundle;
	
	private TeacherAttributes teacherAttributes = new TeacherAttributes();
	
	public TeachUsSession(Request request) {
		super(request);

		CookieUtils cookieUtils = new CookieUtils();
		String username = cookieUtils.load("username");
		String password = cookieUtils.load("password");
		if (false == Strings.isEmpty(username) && false == Strings.isEmpty(password)) {
			signIn(username, password);
		} else {
			changeLocale(getLocale());
		}
	}

	public boolean isAuthenticated() {
		return getPerson() != null;
	}
	
	public void signIn(String username, String password) {
		PersonDAO personDAO = TeachUsApplication.get().getPersonDAO();

		person = personDAO.authenticatePerson(username, password);

		setLocale();
	}
	
	public void signInWithPrivateKey(String username, String privateKey) {
		PersonDAO personDAO = TeachUsApplication.get().getPersonDAO();
		
		person = personDAO.authenticatePersonWithPrivateKey(username, privateKey);

		setLocale();
	}

	private void setLocale() {
		Person p = getPerson();
		if (p != null) {
			if (p.getLocale() != null) {
				setLocale(p.getLocale());
			} else if (p instanceof Pupil) {
				Pupil pupil = (Pupil) p;
				setLocale(pupil.getTeacher().getLocale());
			}
		}
		
		changeLocale(getLocale());
	}

	/**
	 * Should only be used for an Admin to be able to login as a teacher to see his/hers settings.
	 * 
	 * @param teacher The teacher to authenticate as.
	 */
	public void setAuthenticatedPerson(Teacher teacher) {
		if (getUserLevel() == UserLevel.ADMIN) {
			person = teacher;
		}
		
		// We don't set any locale because the teacher might not have the same as the admin.
	}
	
	public void changeLocale(Locale locale) {
		setLocale(locale);
		resourceBundle = createResourceBundle(locale);
	}
	
	public Properties createResourceBundle(Locale locale) {
		Properties properties = new Properties();
		
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.setProperty(key, bundle.getString(key));
		}
		
		return properties;
	}

	public void signOut() {
		CookieUtils cookieUtils = new CookieUtils();
		cookieUtils.remove("username");
		cookieUtils.remove("password");
		person = null;
		invalidateNow();
	}
	
	public Person getPerson() {
		return person;
	}
	
	public Teacher getTeacher() {
		return (Teacher) getPerson();
	}
	
	public List<TeacherAttribute> getTeacherAttributes() {
		Person p = getPerson();
		return teacherAttributes.getTeacherAttributes(p);
	}
	
	public <A extends TeacherAttribute> A getTeacherAttribute(Class<A> attributeClass) {
		return teacherAttributes.getTeacherAttribute(attributeClass, getPerson());
	}

	public <A extends TeacherAttribute> A getTeacherAttribute(Class<A> attributeClass, Teacher teacher) {
		return teacherAttributes.getTeacherAttribute(attributeClass, teacher);
	}
	
	public void saveNewTeacherAttribute(TeacherAttribute teacherAttribute) {
		if (teacherAttribute != null && teacherAttribute.getId() == null) {
			TeachUsApplication.get().getPersonDAO().saveAttribute(teacherAttribute);
			
			teacherAttributes.refreshAttributes(teacherAttribute.getTeacher());
		}
	}
	
	public UserLevel getUserLevel() {
		UserLevel userLevel = null;
		
		if (person != null) {
			if (person instanceof Admin) {
				userLevel = UserLevel.ADMIN;
			} else if (person instanceof Teacher) {
				userLevel = UserLevel.TEACHER;
			} else if (person instanceof Pupil) {
				userLevel = UserLevel.PUPIL;
			} else {
				throw new IllegalStateException("Unsupported person in user: "+person.getClass().getName()); //$NON-NLS-1$
			}
		}
		
		return userLevel;
	}

	public String getString(String key) {
		try {
			return resourceBundle.getProperty(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static TeachUsSession get() {
		return (TeachUsSession) Session.get();
	}
	
}
