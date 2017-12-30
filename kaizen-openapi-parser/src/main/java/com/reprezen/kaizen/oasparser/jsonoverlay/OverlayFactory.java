/*******************************************************************************
 *  Copyright (c) 2017 ModelSolv, Inc. and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     ModelSolv, Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package com.reprezen.kaizen.oasparser.jsonoverlay;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class OverlayFactory<V, OV extends JsonOverlay<V>> {

	private final Class<? super OV> overlayClass = getOverlayClass();

	public OV create(V value, JsonOverlay<?> parent, ReferenceRegistry refReg, Reference ref) {
		OV overlay = _create(value, parent, refReg);
		overlay.setReference(ref);
		return overlay;
	}

	public OV create(JsonNode json, JsonOverlay<?> parent, ReferenceRegistry refReg, Reference ref) {
		OV overlay = create(json, parent, false, refReg);
		overlay.setReference(ref);
		return overlay;
	}

	protected OV create(JsonNode json, JsonOverlay<?> parent, boolean partial, ReferenceRegistry refReg) {
		if (!partial && refReg.hasOverlay(json)) {
			@SuppressWarnings("unchecked")
			OV overlay = (OV) refReg.getOverlay(json);
			if (parent != null) {
				overlay.setParent(parent);
			}
			return overlay;
		} else {
			OV overlay = _create(json, parent, refReg);
			if (!partial) {
				refReg.setOverlay(json, overlay);
			}
			return overlay;
		}
	}

	public boolean isCompatible(JsonOverlay<?> overlay) {
		return overlayClass.isAssignableFrom(overlay.getClass());
	}

	protected abstract Class<? super OV> getOverlayClass();

	protected abstract OV _create(V value, JsonOverlay<?> parent, ReferenceRegistry refReg);

	protected abstract OV _create(JsonNode json, JsonOverlay<?> parent, ReferenceRegistry refReg);
}
