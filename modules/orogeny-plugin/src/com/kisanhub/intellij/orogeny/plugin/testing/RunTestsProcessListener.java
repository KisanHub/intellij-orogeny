/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT;
import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static com.intellij.openapi.compiler.CompilerMessageCategory.INFORMATION;

public final class RunTestsProcessListener implements ProcessListener
{
	@NotNull
	@NonNls
	private static final String RunTestSubCategory = "RunTests";

	@NotNull
	private final RunnerAndConfigurationSettings runnerAndConfigurationSettings;

	@NotNull
	private final RunnerAndConfigurationSettingsFailureRecorder runnerAndConfigurationSettingsFailureRecorder;

	public RunTestsProcessListener(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final RunnerAndConfigurationSettingsFailureRecorder runnerAndConfigurationSettingsFailureRecorder)
	{
		this.runnerAndConfigurationSettings = runnerAndConfigurationSettings;
		this.runnerAndConfigurationSettingsFailureRecorder = runnerAndConfigurationSettingsFailureRecorder;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void onTextAvailable(@NotNull final ProcessEvent event, @SuppressWarnings("rawtypes") final Key outputType)
	{
		System.out.println("onTextAvailable = " + event);

		final ConsoleViewContentType consoleViewType = ConsoleViewContentType.getConsoleViewType(outputType);
		assert consoleViewType != null;
		final String text = event.getText();

		assert ERROR != null;
		assert INFORMATION != null;
		runnerAndConfigurationSettingsFailureRecorder.recordFailure(runnerAndConfigurationSettings, consoleViewType.equals(ERROR_OUTPUT) ? ERROR : INFORMATION, RunTestSubCategory, text);
	}

	@Override
	public void startNotified(final ProcessEvent event)
	{
		System.out.println("startNotified = " + event);
	}

	@Override
	public void processWillTerminate(final ProcessEvent event, final boolean willBeDestroyed)
	{
		System.out.println("processWillTerminate = " + event);
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void processTerminated(@NotNull final ProcessEvent event)
	{
		System.out.println("processTerminated = " + event);
		final ProcessHandler processHandler = event.getProcessHandler();
		assert processHandler != null;
		processHandler.removeProcessListener(this);
		final int exitCode = event.getExitCode();

		if (exitCode != 0)
		{
			assert ERROR != null;
			runnerAndConfigurationSettingsFailureRecorder.recordFailure(runnerAndConfigurationSettings, ERROR, RunTestSubCategory, "Tests failed (exit code was " + exitCode + ')');
		}
	}
}
