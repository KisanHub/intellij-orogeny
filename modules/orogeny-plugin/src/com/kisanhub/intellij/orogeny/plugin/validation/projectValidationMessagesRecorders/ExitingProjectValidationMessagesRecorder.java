/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders;

import com.intellij.openapi.compiler.CompilerMessageCategory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static java.lang.System.exit;

public final class ExitingProjectValidationMessagesRecorder implements ProjectValidationMessagesRecorder
{
	private static final int ProjectDidNotBuildExitCode = 3;

	@NotNull
	private final CategorisedProjectValidationMessagesRecorder categorisedProjectValidationMessagesRecorder;

	@NotNull
	private final PrintStream printStream;

	private boolean hasErrors;

	public ExitingProjectValidationMessagesRecorder(@NotNull final CategorisedProjectValidationMessagesRecorder categorisedProjectValidationMessagesRecorder, @NotNull final PrintStream printStream)
	{
		this.categorisedProjectValidationMessagesRecorder = categorisedProjectValidationMessagesRecorder;
		this.printStream = printStream;
		hasErrors = false;
	}

	@Override
	public void record(@NotNull final CompilerMessageCategory compilerMessageCategory, @NonNls @NotNull final String subCategory, @NonNls @NotNull final String message)
	{
		categorisedProjectValidationMessagesRecorder.record(compilerMessageCategory, subCategory, message);
		if (!hasErrors)
		{
			hasErrors = compilerMessageCategory == ERROR;
		}
	}

	@SuppressWarnings("CallToSystemExit")
	public void writeToPrintStreamAndExitIfHasErrors()
	{
		if (hasErrors)
		{
			writeToPrintStream();
			exit(ProjectDidNotBuildExitCode);
		}
	}

	@SuppressWarnings("CallToSystemExit")
	public void writeToPrintStream()
	{
		categorisedProjectValidationMessagesRecorder.recordToPrintStream(printStream);
	}
}
