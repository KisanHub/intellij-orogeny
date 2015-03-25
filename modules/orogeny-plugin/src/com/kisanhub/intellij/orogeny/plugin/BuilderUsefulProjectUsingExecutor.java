/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin;

import com.kisanhub.intellij.orogeny.plugin.rebuilding.ProjectOfflineRebuilder;
import com.kisanhub.intellij.orogeny.plugin.validation.ProjectValidator;
import com.kisanhub.intellij.useful.UsefulProject;
import com.kisanhub.intellij.useful.commandLine.usingExecutors.UsingExecutor;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ErrorTrackingProjectValidationMessagesRecords;
import org.jetbrains.annotations.NotNull;

import static com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.PrintStreamProjectValidationMessagesRecorder.StandardErrorPrintStreamProjectValidationMessagesRecorder;
import static java.lang.System.exit;

public final class BuilderUsefulProjectUsingExecutor implements UsingExecutor<UsefulProject>
{
	@Override
	public void use(@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @NotNull final UsefulProject usefulProject)
	{
		final ErrorTrackingProjectValidationMessagesRecords projectValidationMessagesRecorder = new ErrorTrackingProjectValidationMessagesRecords(StandardErrorPrintStreamProjectValidationMessagesRecorder);

		final ProjectValidator projectValidator = new ProjectValidator(usefulProject);
		projectValidator.validateArtifacts(projectValidationMessagesRecorder);
		projectValidator.validateModuleOrderEntriesInModuleDependencyOrder(projectValidationMessagesRecorder);
		projectValidator.validateCanRunInspections(projectValidationMessagesRecorder);

		if (projectValidationMessagesRecorder.hasErrors())
		{
			exit(3);
			return;
		}

		projectValidator.validateInspections(projectValidationMessagesRecorder);
		if (projectValidationMessagesRecorder.hasErrors())
		{
			exit(3);
			return;
		}

		new ProjectOfflineRebuilder(usefulProject).offlineRebuild(projectValidationMessagesRecorder);
		if (projectValidationMessagesRecorder.hasErrors())
		{
			exit(3);
			return;
		}

		throw new IllegalStateException("Finish me");
	}

}
