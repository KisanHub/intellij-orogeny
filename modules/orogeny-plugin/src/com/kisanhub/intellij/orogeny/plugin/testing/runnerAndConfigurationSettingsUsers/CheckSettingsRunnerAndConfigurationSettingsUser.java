/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing.runnerAndConfigurationSettingsUsers;

import com.intellij.execution.Executor;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.kisanhub.intellij.orogeny.plugin.testing.RunnerAndConfigurationSettingsFailureRecorder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static com.intellij.openapi.compiler.CompilerMessageCategory.WARNING;

public final class CheckSettingsRunnerAndConfigurationSettingsUser extends AbstractPassConfigurationRunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder>
{
	@NotNull
	public static final Executor RunExecutor = DefaultRunExecutor.getRunExecutorInstance();

	@NotNull
	@NonNls
	private static final String RunTestCheckSettingsSubCategory = "RunTestCheckSettings";

	@NotNull
	private final Executor executor;

	public CheckSettingsRunnerAndConfigurationSettingsUser(@NotNull final Executor executor, @NotNull final RunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder> passConfigurationTo)
	{
		super(passConfigurationTo);
		this.executor = executor;
	}

	@Override
	protected boolean shouldNotPassOnConfiguration(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final RunnerAndConfigurationSettingsFailureRecorder data)
	{
		assert WARNING != null;
		assert ERROR != null;

		try
		{
			runnerAndConfigurationSettings.checkSettings(executor);
		}
		catch (final RuntimeConfigurationWarning e)
		{
			data.recordFailure(runnerAndConfigurationSettings, WARNING, RunTestCheckSettingsSubCategory, e);
			return true;
		}
		catch (final RuntimeConfigurationError e)
		{
			data.recordFailure(runnerAndConfigurationSettings, ERROR, RunTestCheckSettingsSubCategory, e);
			return true;
		}
		catch (final RuntimeConfigurationException e)
		{
			data.recordFailure(runnerAndConfigurationSettings, ERROR, RunTestCheckSettingsSubCategory, e);
			return true;
		}
		return false;
	}
}
