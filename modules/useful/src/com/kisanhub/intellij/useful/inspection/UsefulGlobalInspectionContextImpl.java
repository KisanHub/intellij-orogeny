/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.inspection;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.ex.GlobalInspectionContextImpl;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.codeInspection.ui.InspectionToolPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.ui.content.ContentManager;
import com.kisanhub.intellij.useful.inspection.inspectionToolPresentationFactories.InspectionToolPresentationFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class UsefulGlobalInspectionContextImpl extends GlobalInspectionContextImpl
{
	@NotNull
	private final Map<InspectionToolWrapper<?, ?>, InspectionToolPresentation> presentations;

	@NotNull
	private final InspectionToolPresentationFactory inspectionToolPresentationFactory;

	// Replicates logic of InspectionManagerEx.createNewGlobalContext
	@SuppressWarnings("ThisEscapedInObjectConstruction")
	public UsefulGlobalInspectionContextImpl(@NotNull final Project project, @NotNull final NotNullLazyValue<ContentManager> contentManager, @NotNull final Collection<GlobalInspectionContextImpl> runningContexts, @NotNull final InspectionProfile inspectionProfile, @NotNull final InspectionToolPresentationFactory inspectionToolPresentationFactory)
	{
		super(project, contentManager);
		this.inspectionToolPresentationFactory = inspectionToolPresentationFactory;
		runningContexts.add(this);
		setExternalProfile(inspectionProfile);
		presentations = new LinkedHashMap<InspectionToolWrapper<?, ?>, InspectionToolPresentation>(16);
	}

	@Override
	@NotNull
	public InspectionProfile getCurrentProfile()
	{
		final InspectionProfile currentProfile = super.getCurrentProfile();
		assert currentProfile != null;
		return currentProfile;
	}

	@SuppressWarnings("RefusedBequest")
	@NotNull
	@Override
	public InspectionToolPresentation getPresentation(@SuppressWarnings("rawtypes") @NotNull final InspectionToolWrapper toolWrapper)
	{
		@Nullable final InspectionToolPresentation inspectionToolPresentation = presentations.get(toolWrapper);
		if (inspectionToolPresentation != null)
		{
			return inspectionToolPresentation;
		}

		final InspectionToolPresentation usefulInspectionToolPresentation = inspectionToolPresentationFactory.inspectionToolPresentation(this, toolWrapper);
		presentations.put(toolWrapper, usefulInspectionToolPresentation);
		return usefulInspectionToolPresentation;
	}

	public void inspect(@NotNull final AnalysisScope scope, final boolean runGlobalToolsOnly)
	{
		performInspectionsWithProgress(scope, runGlobalToolsOnly);
	}
}
