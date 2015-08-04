/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.rebuilding;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TestDialog;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.artifacts.ArtifactManager;
import com.kisanhub.intellij.useful.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import com.kisanhub.intellij.useful.UsefulProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;

import static com.intellij.packaging.impl.compiler.ArtifactCompileScope.createArtifactsScope;
import static com.kisanhub.intellij.orogeny.plugin.rebuilding.NoTestDialog.AnswerAlwaysAsNo;

public final class ProjectOfflineRebuilder
{
	@NotNull
	private final UsefulProject usefulProject;

	private final boolean isHeadlessEnvironment;

	public ProjectOfflineRebuilder(@NotNull final UsefulProject usefulProject, final boolean isHeadlessEnvironment)
	{
		this.usefulProject = usefulProject;
		this.isHeadlessEnvironment = isHeadlessEnvironment;
	}

	public void offlineRebuild(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		@Nullable final TestDialog oldValue = isHeadlessEnvironment ? setTestDialogBypassingLoggingBug(AnswerAlwaysAsNo) : null;
		usefulProject.compilerManager.rebuild(new RebuildCompileStatusNotification(projectValidationMessagesRecorder)
		{
			@Override
			public void finished(final boolean aborted, final int errors, final int warnings, @NotNull final CompileContext compileContext)
			{
				super.finished(aborted, errors, warnings, compileContext);
				if (isHeadlessEnvironment)
				{
					setTestDialogBypassingLoggingBug(oldValue);
				}
			}
		});
	}

	public void rebuildAllArtifactsNotBuiltOnMake(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		final ArtifactManager artifactManager = usefulProject.artifactManager;
		final Collection<Artifact> artifacts = new LinkedHashSet<Artifact>(10);
		for (final Artifact artifact : artifactManager.getArtifacts())
		{
			if (artifact.isBuildOnMake())
			{
				continue;
			}
			artifacts.add(artifact);
		}

		if (artifacts.isEmpty())
		{
			return;
		}

		final boolean forceArtifactBuild = true;
		final CompileScope scope = createArtifactsScope(usefulProject.project, artifacts, forceArtifactBuild);
		assert scope != null;

		//ArtifactsWorkspaceSettings.getInstance(usefulProject.project).setArtifactsToBuild(artifacts);
		usefulProject.compilerManager.make(scope, new RebuildCompileStatusNotification(projectValidationMessagesRecorder));
	}

	@Nullable
	private static TestDialog setTestDialogBypassingLoggingBug(@Nullable final TestDialog testDialog)
	{
		// We're supposed to uses Messages.setTestDialog(), but it's set to check the application is being unit tested, even though the internal logic checks isHeadlessEnvironment() before using the field.

		final Field ourTestImplementation;
		try
		{
			ourTestImplementation = Messages.class.getDeclaredField("ourTestImplementation");
		}
		catch (final NoSuchFieldException e)
		{
			throw new IllegalStateException("Could not setTestDialog()", e);
		}
		assert ourTestImplementation != null;
		ourTestImplementation.setAccessible(true);

		final TestDialog oldValue;
		try
		{
			oldValue = (TestDialog) ourTestImplementation.get(null);
		}
		catch (final IllegalAccessException e)
		{
			throw new IllegalStateException("Should never happen as we made the field accessible before get", e);
		}

		try
		{
			ourTestImplementation.set(null, testDialog);
		}
		catch (final IllegalAccessException e)
		{
			throw new IllegalStateException("Should never happen as we made the field accessible before set", e);
		}
		return oldValue;
	}
}
