/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin;

import com.intellij.execution.Executor;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.kisanhub.intellij.orogeny.plugin.rebuilding.ProjectOfflineRebuilder;
import com.kisanhub.intellij.orogeny.plugin.testing.RunnerAndConfigurationSettingsFailureRecorder;
import com.kisanhub.intellij.orogeny.plugin.testing.runnerAndConfigurationSettingsUsers.*;
import com.kisanhub.intellij.orogeny.plugin.validation.ProjectValidator;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.CategorisedProjectValidationMessagesRecorder;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ExitingProjectValidationMessagesRecorder;
import com.kisanhub.intellij.useful.UsefulProject;
import com.kisanhub.intellij.useful.commandLine.UsingExecutor;
import org.jetbrains.annotations.NotNull;

import static com.kisanhub.intellij.orogeny.plugin.testing.runnerAndConfigurationSettingsUsers.CheckSettingsRunnerAndConfigurationSettingsUser.RunExecutor;
import static com.kisanhub.intellij.orogeny.plugin.testing.runnerAndConfigurationSettingsUsers.HasTypeIdPassConfigurationRunnerAndConfigurationSettingsUser.JUnitTypeId;
import static java.lang.Class.forName;
import static java.lang.System.out;

public final class BuilderUsefulProjectUsingExecutor implements UsingExecutor<UsefulProject>
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	@Override
	public void use(@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @NotNull final UsefulProject usefulProject)
	{
		assert out != null;
		final ExitingProjectValidationMessagesRecorder projectValidationMessagesRecorder = new ExitingProjectValidationMessagesRecorder(new CategorisedProjectValidationMessagesRecorder(), out);

		final ProjectValidator projectValidator = new ProjectValidator(usefulProject);
		projectValidator.validateArtifacts(projectValidationMessagesRecorder);
		projectValidator.validateModuleOrderEntriesInModuleDependencyOrder(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		projectValidator.validateCanRunInspections(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		projectValidator.validateInspections(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		final ProjectOfflineRebuilder projectOfflineRebuilder = new ProjectOfflineRebuilder(usefulProject, ApplicationManager.getApplication().isHeadlessEnvironment());

		projectOfflineRebuilder.offlineRebuild(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		projectOfflineRebuilder.rebuildAllArtifactsNotBuiltOnMake(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		validateTests(usefulProject, projectValidationMessagesRecorder);

		projectValidationMessagesRecorder.writeToPrintStream();
	}

	private static void validateTests(@NotNull final UsefulProject usefulProject, @NotNull final ExitingProjectValidationMessagesRecorder projectValidationMessagesRecorder)
	{
		final RunManagerEx runManager = usefulProject.runManager;
		final RunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder> runnerAndConfigurationSettingsUser = runnerAndConfigurationSettings(runManager);
		final RunnerAndConfigurationSettingsFailureRecorder data = new RunnerAndConfigurationSettingsFailureRecorder(projectValidationMessagesRecorder);

		for (final RunnerAndConfigurationSettings runnerAndConfigurationSettings : runManager.getAllSettings())
		{
			runnerAndConfigurationSettingsUser.use(runnerAndConfigurationSettings, data);
		}

		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();
	}

	@NotNull
	private static RunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder> runnerAndConfigurationSettings(@NotNull final RunManagerEx runManager)
	{
		final Executor executor = RunExecutor;
		return new SharedOnlyRunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder>
		(
			runManager,
			new NotTemplateRunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder>
			(
				new NotTemporaryRunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder>
				(
					new NotEditBeforeRunRunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder>
					(
						new HasTypeIdPassConfigurationRunnerAndConfigurationSettingsUser<RunnerAndConfigurationSettingsFailureRecorder>
						(
							JUnitTypeId,
							new CheckSettingsRunnerAndConfigurationSettingsUser
							(
								executor,
								new TestRunningRunnerAndConfigurationSettingsUser(executor)
							)
						)
					)
				)
			)
		);
	}

}
