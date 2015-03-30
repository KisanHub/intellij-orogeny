/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.ProgramRunner.Callback;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.project.Project;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import com.kisanhub.intellij.useful.UsefulProject;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static com.intellij.openapi.compiler.CompilerMessageCategory.WARNING;
import static com.kisanhub.intellij.orogeny.plugin.validation.ProjectValidator.getRunnerAndConfigurationSettingsName;

// Requires the plugin 'junit'
public final class TestsRunner
{
	@NotNull
	private static final Executor RunExecutor = DefaultRunExecutor.getRunExecutorInstance();

	@NotNull
	@NonNls
	private static final String RunTestChecksSubCategory = "RunTestChecks";

	@NotNull
	private final Project project;

	@NotNull
	private final List<RunnerAndConfigurationSettings> runnerAndConfigurationSettingsList;

	// eg "JUnit" for junit
	public TestsRunner(@NotNull final UsefulProject usefulProject, @NonNls @NotNull final String typeId)
	{
		project = usefulProject.project;
		runnerAndConfigurationSettingsList = findWithoutKnowingConfigurationTypeClass(usefulProject.runManager, typeId);
	}

	// eg JUnitConfigurationType.getInstance(); but as this lives inside a plugin, not something we ought to rely on being present in IntelliJ for compiler purposes, perhaps
	public TestsRunner(@NotNull final UsefulProject usefulProject, @NotNull final ConfigurationType configurationType)
	{
		project = usefulProject.project;
		runnerAndConfigurationSettingsList = usefulProject.runManager.getConfigurationSettingsList(configurationType);
	}

	@Nullable
	private static ProgramRunner<?> getProgrammerRunner(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings)
	{
		final RunProfile runProfile = runnerAndConfigurationSettings.getConfiguration();
		assert runProfile != null;
		return RunnerRegistry.getInstance().getRunner(RunExecutor.getId(), runProfile);
	}

	// This method avoids having to have a plugin active to find settings
	@NotNull
	private static List<RunnerAndConfigurationSettings> findWithoutKnowingConfigurationTypeClass(@NotNull final RunManagerEx runManager, @NotNull final String typeId)
	{
		final List<RunnerAndConfigurationSettings> allRunnerAndConfigurationSettings = runManager.getAllSettings();
		final List<RunnerAndConfigurationSettings> foundRunnerAndConfigurationSettings = new ArrayList<RunnerAndConfigurationSettings>(allRunnerAndConfigurationSettings.size());
		for (final RunnerAndConfigurationSettings runnerAndConfigurationSettings : allRunnerAndConfigurationSettings)
		{
			final ConfigurationType type = runnerAndConfigurationSettings.getType();
			assert type != null;
			if (typeId.equals(type.getId()))
			{
				allRunnerAndConfigurationSettings.add(runnerAndConfigurationSettings);
			}
		}
		return foundRunnerAndConfigurationSettings;
	}

	public void runTestConfigurations(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		for (final RunnerAndConfigurationSettings runnerAndConfigurationSettings : runnerAndConfigurationSettingsList)
		{
			runTestConfiguration(runnerAndConfigurationSettings, projectValidationMessagesRecorder);
		}
	}

	private void runTestConfiguration(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		assert WARNING != null;
		assert ERROR != null;

		if (runnerAndConfigurationSettings.isEditBeforeRun())
		{
			return;
		}

		@Nullable final ProgramRunner<?> programRunner = getProgrammerRunner(runnerAndConfigurationSettings);

		if (programRunner == null)
		{
			recordFailure(runnerAndConfigurationSettings, projectValidationMessagesRecorder, WARNING, RunTestChecksSubCategory, "Has no ProgramRunner");
			return;
		}

		// Originally derived from ProgramRunnerUtil
		final ExecutionEnvironment executionEnvironment = newExecutionEnvironment(programRunner, runnerAndConfigurationSettings);

		final ExecutionTarget target = executionEnvironment.getExecutionTarget();
		if (!(runnerAndConfigurationSettings.canRunOn(target) && target.canRun(runnerAndConfigurationSettings)))
		{
			recordFailure(runnerAndConfigurationSettings, projectValidationMessagesRecorder, ERROR, RunTestChecksSubCategory, "Can not be run for ExecutionTarget");
			return;
		}

		try
		{
			runnerAndConfigurationSettings.checkSettings(RunExecutor);
		}
		catch (final RuntimeConfigurationWarning e)
		{
			recordFailure(runnerAndConfigurationSettings, projectValidationMessagesRecorder, WARNING, RunTestChecksSubCategory, e);
			return;
		}
		catch (final RuntimeConfigurationError e)
		{
			recordFailure(runnerAndConfigurationSettings, projectValidationMessagesRecorder, ERROR, RunTestChecksSubCategory, e);
			return;
		}
		catch (final RuntimeConfigurationException e)
		{
			recordFailure(runnerAndConfigurationSettings, projectValidationMessagesRecorder, ERROR, RunTestChecksSubCategory, e);
			return;
		}

		if (ExecutorRegistry.getInstance().isStarting(executionEnvironment))
		{
			return;
		}

		executionEnvironment.assignNewExecutionId();

		try
		{
			programRunner.execute(executionEnvironment, new Callback()
			{
				@Override
				public void processStarted(@NotNull final RunContentDescriptor descriptor)
				{
					final ProcessHandler processHandler = descriptor.getProcessHandler();
					assert processHandler != null;
					processHandler.addProcessListener(new RunTestsProcessListener(runnerAndConfigurationSettings, projectValidationMessagesRecorder));
				}
			});
		}
		catch (final ExecutionException e)
		{
			recordFailure(runnerAndConfigurationSettings, projectValidationMessagesRecorder, ERROR, RunTestChecksSubCategory, e);
		}
	}

	private static void recordFailure(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder, @NotNull final CompilerMessageCategory error, @NotNull final String subCategory, @NotNull final Exception cause)
	{
		recordFailure(runnerAndConfigurationSettings, projectValidationMessagesRecorder, error, subCategory, cause.getMessage());
	}

	public static void recordFailure(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder, @NotNull final CompilerMessageCategory error, @NotNull final String subCategory, @NonNls @Nullable final String message)
	{
		@NonNls final String actualMessage = message == null ? "(null)" : message;
		final String runnerAndConfigurationSettingsName = getRunnerAndConfigurationSettingsName(runnerAndConfigurationSettings);
		projectValidationMessagesRecorder.record(error, subCategory, runnerAndConfigurationSettingsName + ':' + actualMessage);
	}

	@NotNull
	private ExecutionEnvironment newExecutionEnvironment(@NotNull final ProgramRunner<?> programRunner, @NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings)
	{
		return new ExecutionEnvironmentBuilder(project, RunExecutor).runnerAndSettings(programRunner, runnerAndConfigurationSettings).contentToReuse(null).dataContext(null).activeTarget().build();
	}

}
