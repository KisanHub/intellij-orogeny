/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.kisanhub.intellij.orogeny.plugin.rebuilding.ProjectOfflineRebuilder;
import com.kisanhub.intellij.orogeny.plugin.validation.ProjectValidator;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.CategorisedProjectValidationMessagesRecorder;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ExitingProjectValidationMessagesRecorder;
import com.kisanhub.intellij.useful.UsefulProject;
import com.kisanhub.intellij.useful.commandLine.commandLineApplicationStarterExs.UsingExecutor;
import org.jetbrains.annotations.NotNull;

import static java.lang.System.err;

public final class BuilderUsefulProjectUsingExecutor implements UsingExecutor<UsefulProject>
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	@Override
	public void use(@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @NotNull final UsefulProject usefulProject)
	{
		final boolean initialized = usefulProject.project.isInitialized();
		System.out.println("initialized = " + initialized);
		// true


		final ProjectManager instance = ProjectManager.getInstance();
		Project defaultProject = instance.getDefaultProject();
		final ProjectRootManager instance1 = ProjectRootManager.getInstance(defaultProject);
		Sdk jdk = instance1.getProjectSdk();
		System.out.println(jdk);

		final Sdk projectSdk = usefulProject.projectRootManager.getProjectSdk();
		System.out.println(projectSdk);


		assert err != null;
		final ExitingProjectValidationMessagesRecorder projectValidationMessagesRecorder = new ExitingProjectValidationMessagesRecorder(new CategorisedProjectValidationMessagesRecorder(), err);

		final ProjectValidator projectValidator = new ProjectValidator(usefulProject);
		projectValidator.validateArtifacts(projectValidationMessagesRecorder);
		projectValidator.validateModuleOrderEntriesInModuleDependencyOrder(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		projectValidator.validateCanRunInspections(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		projectValidator.validateInspections(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		new ProjectOfflineRebuilder(usefulProject).offlineRebuild(projectValidationMessagesRecorder);
		projectValidationMessagesRecorder.writeToPrintStreamAndExitIfHasErrors();

		projectValidationMessagesRecorder.writeToPrintStream();

		throw new IllegalStateException("TODO: rebuild artifacts; some are built as part of rebuild, though");
	}

}
