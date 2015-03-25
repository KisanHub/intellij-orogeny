/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders;

import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;

public final class ErrorTrackingProjectValidationMessagesRecords implements ProjectValidationMessagesRecorder
{
	@NotNull
	private final ProjectValidationMessagesRecorder delegateTo;

	private boolean hasErrors;

	public ErrorTrackingProjectValidationMessagesRecords(@NotNull final ProjectValidationMessagesRecorder delegateTo)
	{
		this.delegateTo = delegateTo;
		hasErrors = false;
	}

	@Override
	public void record(@NotNull final Project project, @NotNull final CompilerMessageCategory compilerMessageCategory, @NotNull final String message)
	{
		delegateTo.record(project, compilerMessageCategory, message);
		hasErrors = compilerMessageCategory == ERROR;
	}

	public boolean hasErrors()
	{
		return hasErrors;
	}
}
