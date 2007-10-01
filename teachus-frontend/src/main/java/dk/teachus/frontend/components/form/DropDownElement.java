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
package dk.teachus.frontend.components.form;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidator;

public class DropDownElement extends AbstractChoiceElement {
	private static final long serialVersionUID = 1L;
	
	private DropDownChoice dropDownChoice;
	
	public DropDownElement(String label, IModel inputModel, List<?> choices) {
		this(label, inputModel, choices, null, false);
	}
	
	public DropDownElement(String label, IModel inputModel, List<?> choices, IChoiceRenderer choiceRenderer) {
		this(label, inputModel, choices, choiceRenderer, false);
	}
	
	public DropDownElement(String label, IModel inputModel, List<?> choices, boolean required) {
		this(label, inputModel, choices, null, required);
	}
	
	public DropDownElement(String label, IModel inputModel, List<?> choices, IChoiceRenderer choiceRenderer, boolean required) {
		super(label, inputModel, choices, choiceRenderer, required);
	}
	
	@Override
	protected IFeedbackMessageFilter getFeedbackMessageFilter() {
		return new ComponentFeedbackMessageFilter(dropDownChoice);
	}

	@Override
	protected Component newInputComponent(String wicketId, FeedbackPanel feedbackPanel) {
		DropDownElementPanel elementPanel = new DropDownElementPanel(wicketId);
		elementPanel.setRenderBodyOnly(true);
		
		dropDownChoice = new DropDownChoice("dropDown", inputModel, choices, choiceRenderer) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return isReadOnly() == false;
			}
		};
		dropDownChoice.setLabel(new Model(label));
		elementPanel.add(dropDownChoice);
		dropDownChoice.setRequired(required);
		dropDownChoice.setNullValid(required == false);
		dropDownChoice.setEnabled(isReadOnly() == false);
				
		return elementPanel;
	}
	
	@Override
	protected void addValidator(IValidator validator) {
		dropDownChoice.add(validator);
	}
	
	@Override
	public FormComponent getFormComponent() {
		return dropDownChoice;
	}

	@Override
	protected String getValidationEvent() {
		return "onchange";
	}
	
}
