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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import dk.teachus.frontend.components.RenderingLabel;

public class ReadOnlyElement extends FormElement {
	private static final long serialVersionUID = 1L;
	
	public ReadOnlyElement(String label, IModel inputModel) {
		this(label, inputModel, null);
	}
	
	public ReadOnlyElement(String label, IModel inputModel, IChoiceRenderer renderer) {		
		add(new Label("label", label));
		add(new RenderingLabel("readOnly", inputModel, renderer));
	}

}
