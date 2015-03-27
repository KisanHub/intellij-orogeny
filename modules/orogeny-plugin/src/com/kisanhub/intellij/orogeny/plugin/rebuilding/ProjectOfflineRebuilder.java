/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.rebuilding;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.ui.TestDialog;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.artifacts.ArtifactManager;
import com.intellij.packaging.impl.compiler.ArtifactCompileScope;
import com.intellij.packaging.impl.compiler.ArtifactsCompiler;
import com.intellij.packaging.impl.compiler.ArtifactsWorkspaceSettings;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import com.kisanhub.intellij.useful.UsefulProject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.intellij.openapi.ui.Messages.setTestDialog;
import static com.intellij.packaging.impl.compiler.ArtifactCompileScope.createArtifactsScope;
import static com.kisanhub.intellij.orogeny.plugin.rebuilding.NoTestDialog.AnswerAlwaysAsNo;

public final class ProjectOfflineRebuilder
{
	@NotNull
	private final UsefulProject usefulProject;

	public ProjectOfflineRebuilder(@NotNull final UsefulProject usefulProject)
	{
		this.usefulProject = usefulProject;
	}

	public void offlineRebuild(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		final TestDialog oldValue = setTestDialog(AnswerAlwaysAsNo);
		usefulProject.compilerManager.rebuild(new RebuildCompileStatusNotification(projectValidationMessagesRecorder)
		{
			@Override
			public void finished(final boolean aborted, final int errors, final int warnings, @NotNull final CompileContext compileContext)
			{
				super.finished(aborted, errors, warnings, compileContext);
				setTestDialog(oldValue);
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
}
