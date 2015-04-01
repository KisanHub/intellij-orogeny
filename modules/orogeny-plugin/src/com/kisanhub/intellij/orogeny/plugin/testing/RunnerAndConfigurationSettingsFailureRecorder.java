/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RunnerAndConfigurationSettingsFailureRecorder
{
	@NotNull
	private final ProjectValidationMessagesRecorder projectValidationMessagesRecorder;

	public RunnerAndConfigurationSettingsFailureRecorder(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		this.projectValidationMessagesRecorder = projectValidationMessagesRecorder;
	}

	@NotNull
	@NonNls
	public static String getRunnerAndConfigurationSettingsName(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings)
	{
		@Nullable final String name = runnerAndConfigurationSettings.getName();
		return name == null ? "<Unknown>" : name;
	}

	public void recordFailure(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final CompilerMessageCategory error, @NotNull final String subCategory, @NotNull final Exception cause)
	{
		recordFailure(runnerAndConfigurationSettings, error, subCategory, cause.getMessage());
	}

	public void recordFailure(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final CompilerMessageCategory error, @NotNull final String subCategory, @NonNls @Nullable final String message)
	{
		final String runnerAndConfigurationSettingsName = getRunnerAndConfigurationSettingsName(runnerAndConfigurationSettings);
		@NonNls final String actualMessage = message == null ? "(null)" : message;
		projectValidationMessagesRecorder.record(error, subCategory, runnerAndConfigurationSettingsName + ':' + actualMessage);
	}
}
