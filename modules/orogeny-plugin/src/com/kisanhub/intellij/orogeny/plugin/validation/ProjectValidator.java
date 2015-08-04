/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.validation;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.project.Project;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.artifacts.ArtifactManager;
import com.intellij.packaging.artifacts.ArtifactType;
import com.kisanhub.intellij.orogeny.plugin.testing.RunnerAndConfigurationSettingsFailureRecorder;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import com.kisanhub.intellij.useful.UsefulProject;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.codeInspection.ex.GlobalInspectionContextUtil.canRunInspections;
import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static com.intellij.openapi.compiler.CompilerMessageCategory.WARNING;
import static com.kisanhub.intellij.orogeny.plugin.validation.OrderEntryValidatingRootPolicy.OrderEntryValidatingRootPolicyInstance;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class ProjectValidator
{
	@NonNls
	@NotNull
	private static final String ArtifactSubCategory = "Artifact";

	@NonNls
	@NotNull
	private static final String InspectionSubCategory = "Inspection";

	@NonNls
	@NotNull
	private static final String InvalidId = "invalid";

	private static final String RunConfigurationSubCategory = "RunConfiguration";

	@NotNull
	private final UsefulProject usefulProject;

	@NotNull
	private final Project project;

	@NotNull
	private final ArtifactManager artifactManager;

	public ProjectValidator(@NotNull final UsefulProject usefulProject)
	{
		this.usefulProject = usefulProject;
		project = usefulProject.project;
		artifactManager = usefulProject.artifactManager;
	}

	public boolean validateArtifacts(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		final List<? extends Artifact> allArtifactsIncludingInvalid = artifactManager.getAllArtifactsIncludingInvalid();
		assert allArtifactsIncludingInvalid != null;
		boolean allArtifactsValid = true;
		for (final Artifact artifact : allArtifactsIncludingInvalid)
		{
			final ArtifactType artifactType = artifact.getArtifactType();
			if (InvalidId.equals(artifactType.getId()))
			{
				final String format = format(ENGLISH, "The artifact '%1$s' is invalid", artifact.getName()); //NON-NLS
				assert format != null;

				assert ERROR != null;
				projectValidationMessagesRecorder.record(ERROR, ArtifactSubCategory, format);
				allArtifactsValid = false;
			}
		}
		return allArtifactsValid;
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	private static boolean recordInvalidRunConfiguration(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder, @NotNull final RuntimeConfigurationException e, @NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @Nullable final CompilerMessageCategory compilerMessageCategory)
	{
		assert compilerMessageCategory != null;

		@Nullable final String message = e.getMessage();

		projectValidationMessagesRecorder.record(compilerMessageCategory, RunConfigurationSubCategory, RunnerAndConfigurationSettingsFailureRecorder.getRunnerAndConfigurationSettingsName(runnerAndConfigurationSettings) + ':' + (message == null ? "(null)" : message));
		return false;
	}

	public void validateModuleOrderEntriesInModuleDependencyOrder(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		usefulProject.useModuleOrderEntriesInModuleDependencyOrder(OrderEntryValidatingRootPolicyInstance, projectValidationMessagesRecorder);
	}

	public boolean validateCanRunInspections(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		if (canRunInspections(project, false))
		{
			return true;
		}
		assert WARNING != null;
		projectValidationMessagesRecorder.record(WARNING, InspectionSubCategory, "We can not run inspections");
		return false;
	}

	public void validateInspections(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		usefulProject.inspect(new ValidatingProblemRefElementHandler(projectValidationMessagesRecorder));
	}
}
