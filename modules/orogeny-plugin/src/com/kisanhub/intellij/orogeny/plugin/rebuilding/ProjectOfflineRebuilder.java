/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.rebuilding;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.ui.TestDialog;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import com.kisanhub.intellij.useful.UsefulProject;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.ui.Messages.setTestDialog;
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
		usefulProject.compilerManager.rebuild(new RebuildCompileStatusNotification(projectValidationMessagesRecorder, usefulProject.project)
		{
			@Override
			public void finished(final boolean aborted, final int errors, final int warnings, @NotNull final CompileContext compileContext)
			{
				super.finished(aborted, errors, warnings, compileContext);
				setTestDialog(oldValue);
			}
		});
	}
}
