/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine.commandLineApplicationStarterExs;

import com.intellij.openapi.project.Project;
import com.kisanhub.intellij.useful.UsefulProject;
import com.kisanhub.intellij.useful.commandLine.usingExecutors.UsingExecutor;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractUsefulProjectUsingCommandLineApplicationStarterEx extends AbstractProjectUsingCommandLineApplicationStarterEx
{
	protected AbstractUsefulProjectUsingCommandLineApplicationStarterEx(@NotNull final UsingExecutor<UsefulProject> usefulProjectUsingExecutor)
	{
		super(new UsingExecutor<Project>()
		{
			@Override
			public void use(@NotNull final Project using)
			{
				usefulProjectUsingExecutor.use(new UsefulProject(using));
			}
		});
	}
}
