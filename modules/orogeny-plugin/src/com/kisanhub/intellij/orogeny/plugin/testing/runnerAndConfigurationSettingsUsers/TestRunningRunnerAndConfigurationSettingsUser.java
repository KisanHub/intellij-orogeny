/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing.runnerAndConfigurationSettingsUsers;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.*;
import com.intellij.execution.runners.ProgramRunner.Callback;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.kisanhub.intellij.orogeny.plugin.testing.DelegateProgramRunner;
import com.kisanhub.intellij.orogeny.plugin.testing.RunTestsProcessListener;
import com.kisanhub.intellij.orogeny.plugin.testing.RunnerAndConfigurationSettingsFailureRecorder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static com.intellij.openapi.compiler.CompilerMessageCategory.WARNING;

public final class TestRunningRunnerAndConfigurationSettingsUser implements RunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder>
{
	@NotNull
	@NonNls
	private static final String RunTestProgramRunnerSubCategory = "RunTestProgramRunner";

	@NotNull
	@NonNls
	private static final String RunTestExecutionTargetSubCategory = "RunTestExecutionTarget";

	@NotNull
	@NonNls
	private static final String RunTestExecutionSubCategory = "RunTestExecution";

	@NotNull
	private final Executor executor;

	public TestRunningRunnerAndConfigurationSettingsUser(@NotNull final Executor executor)
	{
		this.executor = executor;
	}

	@Override
	public void use(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final RunnerAndConfigurationSettingsFailureRecorder data)
	{
		assert ERROR != null;

		// RunConfiguration might implement CommonProgramRunConfigurationParameters or CommonJavaRunConfigurationParameters (Junit's does)
		// RunConfiguration might implement RunProfile (Junit's does)
		// final RunConfiguration runConfiguration = runnerAndConfigurationSettings.getConfiguration();

		@Nullable final ProgramRunner<?> underlyingProgramRunner = getProgrammerRunner(runnerAndConfigurationSettings);

		if (underlyingProgramRunner == null)
		{
			assert WARNING != null;
			data.recordFailure(runnerAndConfigurationSettings, WARNING, RunTestProgramRunnerSubCategory, "Has no ProgramRunner");
			return;
		}

		@SuppressWarnings("unchecked") final ProgramRunner<?> programRunner = new DelegateProgramRunner<RunnerSettings>((GenericProgramRunner<RunnerSettings>) underlyingProgramRunner);

		// Originally derived from ProgramRunnerUtil
		final ExecutionEnvironment executionEnvironment = newExecutionEnvironment(programRunner, runnerAndConfigurationSettings);

		final ExecutionTarget target = executionEnvironment.getExecutionTarget();
		if (!(runnerAndConfigurationSettings.canRunOn(target) && target.canRun(runnerAndConfigurationSettings)))
		{
			data.recordFailure(runnerAndConfigurationSettings, ERROR, RunTestExecutionTargetSubCategory, "Can not be run for ExecutionTarget - " + target.getDisplayName());
			return;
		}

		@Nullable final RunProfileState runProfileState;
		try
		{
			runProfileState = executionEnvironment.getState();
		}
		catch (final ExecutionException e)
		{
			data.recordFailure(runnerAndConfigurationSettings, ERROR, RunTestExecutionSubCategory, e.getMessage());
			return;
		}

		if (runProfileState == null)
		{
			data.recordFailure(runnerAndConfigurationSettings, ERROR, RunTestExecutionSubCategory, "Null RunProfileState");
			return;
		}

		final ProcessHandler[] processHandlerUsed = new ProcessHandler[1];
		try
		{
			programRunner.execute(executionEnvironment, new Callback()
			{
				@Override
				public void processStarted(@NotNull final RunContentDescriptor descriptor)
				{
					final ProcessHandler processHandler = descriptor.getProcessHandler();
					assert processHandler != null;
					processHandler.addProcessListener(new RunTestsProcessListener(runnerAndConfigurationSettings, data));
					processHandlerUsed[0] = processHandler;
				}
			});
		}
		catch (final ExecutionException e)
		{
			data.recordFailure(runnerAndConfigurationSettings, ERROR, RunTestExecutionSubCategory, e);
		}
		final ProcessHandler processHandler = processHandlerUsed[0];
		assert processHandler != null;
		if (processHandler.isProcessTerminated() || processHandler.isProcessTerminating())
		{
			System.out.println("terminating");
		}
		final boolean b = processHandler.waitFor();
		System.out.println("b = " + b);
	}

	@Nullable
	private ProgramRunner<?> getProgrammerRunner(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings)
	{
		final RunProfile runProfile = runnerAndConfigurationSettings.getConfiguration();
		assert runProfile != null;
		return RunnerRegistry.getInstance().getRunner(executor.getId(), runProfile);
	}

	@NotNull
	private ExecutionEnvironment newExecutionEnvironment(@NotNull final ProgramRunner<?> programRunner, @NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings)
	{
		final RunConfiguration configuration = runnerAndConfigurationSettings.getConfiguration();
		assert configuration != null;
		final Project project = configuration.getProject();
		assert project != null;
		return new ExecutionEnvironmentBuilder(project, executor).runnerAndSettings(programRunner, runnerAndConfigurationSettings).contentToReuse(null).dataContext(null).activeTarget().assignNewId().build();
	}

}
