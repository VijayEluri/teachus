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

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

public class DecimalFieldElement extends TextFieldElement {
	private static final long serialVersionUID = 1L;

	public DecimalFieldElement(String label, IModel inputModel) {
		super(label, inputModel);
	}
	
	public DecimalFieldElement(String label, IModel inputModel, int size) {
		super(label, inputModel, size);
	}
	
	public DecimalFieldElement(String label, IModel inputModel, boolean required) {
		super(label, inputModel, required);
	}
	
	public DecimalFieldElement(String label, IModel inputModel, boolean required, int size) {
		super(label, inputModel, required, size);
	}

	@Override
	protected TextField getNewInputComponent(String wicketId, FeedbackPanel feedbackPanel) {
		TextField textField = super.getNewInputComponent(wicketId, feedbackPanel);
		textField.setType(Double.class);
		return textField;
	}
	
}
